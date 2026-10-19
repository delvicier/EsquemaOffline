package com.facturacion.sri.service;

import com.facturacion.sri.dto.MensajeSriDto;
import com.facturacion.sri.dto.RespuestaAutorizacionSriDto;
import com.facturacion.sri.dto.RespuestaRecepcionSriDto;
import com.facturacion.sri.exception.SriGatewayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class SriSoapClientService {

    private static final int LIMITE_BYTES_COMPROBANTE = 320 * 1024;

    @Value("${sri.ws.pruebas.recepcion}")
    private String urlRecepcionPruebas;

    @Value("${sri.ws.pruebas.autorizacion}")
    private String urlAutorizacionPruebas;

    @Value("${sri.ws.pruebas.consulta}")
    private String urlConsultaPruebas;

    @Value("${sri.ws.produccion.recepcion}")
    private String urlRecepcionProduccion;

    @Value("${sri.ws.produccion.autorizacion}")
    private String urlAutorizacionProduccion;

    @Value("${sri.ws.produccion.consulta}")
    private String urlConsultaProduccion;

    private final HttpClient httpClient;

    public SriSoapClientService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public RespuestaRecepcionSriDto enviarComprobanteRecepcion(String xmlFirmado, String ambiente) {
        byte[] xmlBytes = xmlFirmado.getBytes(StandardCharsets.UTF_8);

        if (xmlBytes.length > LIMITE_BYTES_COMPROBANTE) {
            throw new IllegalArgumentException("El comprobante firmado supera el límite máximo de 320 KB permitido por el SRI.");
        }

        String xmlBase64 = Base64.getEncoder().encodeToString(xmlBytes);
        String urlDestino = "2".equals(ambiente) ? urlRecepcionProduccion : urlRecepcionPruebas;

        String soapEnvelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.recepcion">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <ec:validarComprobante>
                         <xml>%s</xml>
                      </ec:validarComprobante>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(xmlBase64);

        String responseXml = ejecutarPeticionSoap(urlDestino, soapEnvelope);
        return parsearRespuestaRecepcion(responseXml);
    }

    public RespuestaAutorizacionSriDto consultarAutorizacionComprobante(String claveAcceso, String ambiente) {
        if (claveAcceso == null || !claveAcceso.matches("\\d{49}")) {
            throw new IllegalArgumentException("La clave de acceso a consultar debe contener exactamente 49 dígitos numéricos.");
        }

        String urlDestino = "2".equals(ambiente) ? urlAutorizacionProduccion : urlAutorizacionPruebas;

        String soapEnvelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.autorizacion">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <ec:autorizacionComprobante>
                         <claveAccesoComprobante>%s</claveAccesoComprobante>
                      </ec:autorizacionComprobante>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(claveAcceso);

        String responseXml = ejecutarPeticionSoap(urlDestino, soapEnvelope);
        return parsearRespuestaAutorizacion(responseXml, claveAcceso);
    }

    public RespuestaAutorizacionSriDto consultarEstadoValidezComprobante(String claveAcceso, String ambiente) {
        if (claveAcceso == null || !claveAcceso.matches("\\d{49}")) {
            throw new IllegalArgumentException("La clave de acceso debe tener 49 dígitos numéricos.");
        }

        String urlDestino = "2".equals(ambiente) ? urlConsultaProduccion : urlConsultaPruebas;

        String soapEnvelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.consultas">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <ec:consultarEstadoAutorizacionComprobante>
                         <claveAcceso>%s</claveAcceso>
                      </ec:consultarEstadoAutorizacionComprobante>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(claveAcceso);

        String responseXml = ejecutarPeticionSoap(urlDestino, soapEnvelope);
        try {
            Document doc = parsearDom(responseXml);
            Element root = doc.getDocumentElement();

            String estadoAutorizacion = obtenerTextoTag(root, "estadoAutorizacion");
            if (estadoAutorizacion == null) {
                estadoAutorizacion = obtenerTextoTag(root, "estadoConsulta");
            }

            RespuestaAutorizacionSriDto dto = new RespuestaAutorizacionSriDto();
            dto.setClaveAccesoConsultada(claveAcceso);
            dto.setEstado(estadoAutorizacion != null ? estadoAutorizacion : "DESCONOCIDO");
            dto.setNumeroAutorizacion(claveAcceso);
            dto.setFechaAutorizacion(obtenerTextoTag(root, "fechaAutorizacion"));
            dto.setAmbiente("2".equals(ambiente) ? "PRODUCCIÓN" : "PRUEBAS");
            dto.setMensajes(extraerMensajes(root));
            dto.setXmlRespuestaRaw(responseXml);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear respuesta de ConsultaComprobante del SRI: " + e.getMessage(), e);
        }
    }

    private String ejecutarPeticionSoap(String url, String soapEnvelope) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "text/xml; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(soapEnvelope, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() >= 500) {
                throw new SriGatewayException(
                        "El servidor Web Service del SRI (" + url + ") devolvió un error HTTP " + response.statusCode()
                        + ". El servicio del SRI podría estar en mantenimiento temporal.",
                        502
                );
            }
            return response.body();
        } catch (HttpTimeoutException e) {
            throw new SriGatewayException(
                    "El servidor Web Service del SRI (" + url + ") no respondió dentro del límite de 30 segundos (Timeout).",
                    504,
                    e
            );
        } catch (SriGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new SriGatewayException(
                    "No fue posible conectarse con el Web Service SOAP del SRI (" + url + "): " + e.getMessage(),
                    502,
                    e
            );
        }
    }

    private RespuestaRecepcionSriDto parsearRespuestaRecepcion(String xmlRaw) {
        try {
            Document doc = parsearDom(xmlRaw);
            String estado = obtenerTextoTag(doc.getDocumentElement(), "estado");
            String claveAcceso = obtenerTextoTag(doc.getDocumentElement(), "claveAcceso");
            List<MensajeSriDto> mensajes = extraerMensajes(doc.getDocumentElement());

            return new RespuestaRecepcionSriDto(
                    estado != null ? estado : "SIN_RESPUESTA",
                    claveAcceso,
                    mensajes,
                    xmlRaw
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el XML de respuesta de Recepción del SRI: " + e.getMessage(), e);
        }
    }

    private RespuestaAutorizacionSriDto parsearRespuestaAutorizacion(String xmlRaw, String claveAccesoConsultada) {
        try {
            Document doc = parsearDom(xmlRaw);
            Element root = doc.getDocumentElement();

            NodeList autorizaciones = root.getElementsByTagName("autorizacion");
            if (autorizaciones.getLength() == 0) {
                RespuestaAutorizacionSriDto ppr = new RespuestaAutorizacionSriDto();
                ppr.setClaveAccesoConsultada(claveAccesoConsultada);
                ppr.setEstado("PPR");
                ppr.setMensajes(List.of(new MensajeSriDto(
                        "70",
                        "CLAVE DE ACCESO EN PROCESAMIENTO",
                        "El SRI aún está procesando el comprobante. Consulte nuevamente en unos segundos.",
                        "INFORMATIVO"
                )));
                ppr.setXmlRespuestaRaw(xmlRaw);
                return ppr;
            }

            Element autorizacionElem = (Element) autorizaciones.item(0);
            RespuestaAutorizacionSriDto dto = new RespuestaAutorizacionSriDto();
            dto.setClaveAccesoConsultada(claveAccesoConsultada);
            dto.setEstado(obtenerTextoTag(autorizacionElem, "estado"));
            dto.setNumeroAutorizacion(obtenerTextoTag(autorizacionElem, "numeroAutorizacion"));
            dto.setFechaAutorizacion(obtenerTextoTag(autorizacionElem, "fechaAutorizacion"));
            dto.setAmbiente(obtenerTextoTag(autorizacionElem, "ambiente"));
            dto.setComprobanteXml(obtenerTextoTag(autorizacionElem, "comprobante"));
            dto.setMensajes(extraerMensajes(autorizacionElem));
            dto.setXmlRespuestaRaw(xmlRaw);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el XML de respuesta de Autorización del SRI: " + e.getMessage(), e);
        }
    }

    private List<MensajeSriDto> extraerMensajes(Element contenedor) {
        List<MensajeSriDto> lista = new ArrayList<>();
        NodeList nodosMensaje = contenedor.getElementsByTagName("mensaje");

        for (int i = 0; i < nodosMensaje.getLength(); i++) {
            Element nodo = (Element) nodosMensaje.item(i);
            if (nodo.getElementsByTagName("identificador").getLength() > 0) {
                lista.add(new MensajeSriDto(
                        obtenerTextoTag(nodo, "identificador"),
                        obtenerTextoTag(nodo, "mensaje"),
                        obtenerTextoTag(nodo, "informacionAdicional"),
                        obtenerTextoTag(nodo, "tipo")
                ));
            }
        }
        return lista;
    }

    private Document parsearDom(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new InputSource(new StringReader(xml)));
    }

    private String obtenerTextoTag(Element padre, String nombreTag) {
        NodeList list = padre.getElementsByTagName(nombreTag);
        if (list.getLength() > 0 && list.item(0) != null) {
            return list.item(0).getTextContent().trim();
        }
        return null;
    }
}
