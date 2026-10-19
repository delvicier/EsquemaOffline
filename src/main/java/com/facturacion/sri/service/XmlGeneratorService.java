package com.facturacion.sri.service;

import com.facturacion.sri.xml.FacturaXml;
import com.facturacion.sri.xml.LiquidacionCompraXml;
import com.facturacion.sri.xml.NotaCreditoXml;
import com.facturacion.sri.xml.NotaDebitoXml;
import com.facturacion.sri.xml.ComprobanteRetencionXml;
import com.facturacion.sri.xml.GuiaRemisionXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class XmlGeneratorService {

    private final Map<Class<?>, JAXBContext> contextosJaxb = new ConcurrentHashMap<>();

    public String generarXmlFactura(FacturaXml facturaXml) {
        return marshallToXmlString(facturaXml);
    }

    public String generarXmlNotaCredito(NotaCreditoXml notaCreditoXml) {
        return marshallToXmlString(notaCreditoXml);
    }

    public String generarXmlNotaDebito(NotaDebitoXml notaDebitoXml) {
        return marshallToXmlString(notaDebitoXml);
    }

    public String generarXmlLiquidacionCompra(LiquidacionCompraXml liquidacionCompraXml) {
        return marshallToXmlString(liquidacionCompraXml);
    }

    public String generarXmlComprobanteRetencion(ComprobanteRetencionXml retencionXml) {
        return marshallToXmlString(retencionXml);
    }

    public String generarXmlGuiaRemision(GuiaRemisionXml guiaRemisionXml) {
        return marshallToXmlString(guiaRemisionXml);
    }

    public String marshallToXmlString(Object comprobanteXml) {
        try {
            JAXBContext context = contextosJaxb.computeIfAbsent(comprobanteXml.getClass(), clazz -> {
                try {
                    return JAXBContext.newInstance(clazz);
                } catch (JAXBException e) {
                    throw new RuntimeException("Error al inicializar JAXBContext para " + clazz.getSimpleName(), e);
                }
            });

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);

            StringWriter writer = new StringWriter();
            marshaller.marshal(comprobanteXml, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Error al generar el XML del comprobante: " + e.getMessage(), e);
        }
    }
}
