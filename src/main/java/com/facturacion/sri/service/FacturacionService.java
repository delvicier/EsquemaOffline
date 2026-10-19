package com.facturacion.sri.service;

import com.facturacion.sri.dto.*;
import com.facturacion.sri.entity.Emisor;
import com.facturacion.sri.entity.FacturaDetalleEntity;
import com.facturacion.sri.entity.FacturaEntity;
import com.facturacion.sri.entity.SecuencialComprobante;
import com.facturacion.sri.repository.EmisorRepository;
import com.facturacion.sri.repository.FacturaRepository;
import com.facturacion.sri.repository.SecuencialRepository;
import com.facturacion.sri.util.ClaveAccesoGenerator;
import com.facturacion.sri.xml.FacturaXml;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.facturacion.sri.xml.NotaCreditoXml;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FacturacionService {

    private static final String COD_DOC_FACTURA = "01";
    private static final String COD_IMPUESTO_IVA = "2";
    private static final BigDecimal LIMITE_CONSUMIDOR_FINAL = new BigDecimal("50.00");
    private static final DateTimeFormatter FORMATO_FECHA_XML = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ZoneId ZONA_ECUADOR = ZoneId.of("America/Guayaquil");

    private final EmisorRepository emisorRepository;
    private final SecuencialRepository secuencialRepository;
    private final FacturaRepository facturaRepository;
    private final ClaveAccesoGenerator claveAccesoGenerator;
    private final XmlGeneratorService xmlGeneratorService;
    private final XadesBesSignerService signerService;
    private final SriSoapClientService sriSoapClientService;
    private final RidePdfGeneratorService ridePdfGeneratorService;
    private final EmailNotificationService emailNotificationService;

    public FacturacionService(
            EmisorRepository emisorRepository,
            SecuencialRepository secuencialRepository,
            FacturaRepository facturaRepository,
            ClaveAccesoGenerator claveAccesoGenerator,
            XmlGeneratorService xmlGeneratorService,
            XadesBesSignerService signerService,
            SriSoapClientService sriSoapClientService,
            RidePdfGeneratorService ridePdfGeneratorService,
            EmailNotificationService emailNotificationService) {
        this.emisorRepository = emisorRepository;
        this.secuencialRepository = secuencialRepository;
        this.facturaRepository = facturaRepository;
        this.claveAccesoGenerator = claveAccesoGenerator;
        this.xmlGeneratorService = xmlGeneratorService;
        this.signerService = signerService;
        this.sriSoapClientService = sriSoapClientService;
        this.ridePdfGeneratorService = ridePdfGeneratorService;
        this.emailNotificationService = emailNotificationService;
    }

    @Transactional
    public FacturaResponseDto emitirFactura(EmitirFacturaRequestDto request) {
        Emisor emisor = emisorRepository.findByRucAndActivoTrue(request.getRucEmisor())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un emisor activo configurado con el RUC: " + request.getRucEmisor()));

        LocalDate fechaEmision = LocalDate.now(ZONA_ECUADOR);

        BigDecimal totalSinImpuestos = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;

        Map<String, FacturaXml.TotalImpuesto> mapaTotalImpuestos = new LinkedHashMap<>();
        List<FacturaXml.Detalle> listaDetallesXml = new ArrayList<>();
        List<FacturaDetalleEntity> listaDetallesEntity = new ArrayList<>();

        for (EmitirFacturaRequestDto.DetalleItemDto item : request.getDetalles()) {
            BigDecimal cantidad = item.getCantidad().setScale(6, RoundingMode.HALF_UP);
            BigDecimal precioUnitario = item.getPrecioUnitario().setScale(6, RoundingMode.HALF_UP);
            BigDecimal descuentoItem = item.getDescuento().setScale(2, RoundingMode.HALF_UP);

            BigDecimal subtotalBruto = cantidad.multiply(precioUnitario);
            BigDecimal baseImponibleItem = subtotalBruto.subtract(descuentoItem).setScale(2, RoundingMode.HALF_UP);

            if (baseImponibleItem.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "El descuento no puede superar el subtotal en el ítem: " + item.getDescripcion());
            }

            BigDecimal tarifa = item.getTarifaIva().setScale(2, RoundingMode.HALF_UP);
            BigDecimal valorIvaItem = baseImponibleItem.multiply(tarifa)
                    .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);

            totalSinImpuestos = totalSinImpuestos.add(baseImponibleItem);
            totalDescuento = totalDescuento.add(descuentoItem);
            totalIva = totalIva.add(valorIvaItem);

            String codPorcentaje = item.getCodigoPorcentajeIva().trim();
            FacturaXml.TotalImpuesto acumulado = mapaTotalImpuestos.get(codPorcentaje);
            if (acumulado == null) {
                acumulado = new FacturaXml.TotalImpuesto(
                        COD_IMPUESTO_IVA, codPorcentaje, baseImponibleItem, tarifa, valorIvaItem);
                mapaTotalImpuestos.put(codPorcentaje, acumulado);
            } else {
                acumulado.setBaseImponible(acumulado.getBaseImponible().add(baseImponibleItem));
                acumulado.setValor(acumulado.getValor().add(valorIvaItem));
            }

            FacturaXml.ImpuestoDetalle impuestoDetalleXml = new FacturaXml.ImpuestoDetalle(
                    COD_IMPUESTO_IVA, codPorcentaje, tarifa, baseImponibleItem, valorIvaItem);

            FacturaXml.Detalle detalleXml = new FacturaXml.Detalle();
            detalleXml.setCodigoPrincipal(item.getCodigoPrincipal().trim());
            detalleXml.setCodigoAuxiliar(item.getCodigoAuxiliar() != null && !item.getCodigoAuxiliar().isBlank()
                    ? item.getCodigoAuxiliar().trim() : null);
            detalleXml.setDescripcion(item.getDescripcion().trim());
            detalleXml.setCantidad(cantidad);
            detalleXml.setPrecioUnitario(precioUnitario);
            detalleXml.setDescuento(descuentoItem);
            detalleXml.setPrecioTotalSinImpuesto(baseImponibleItem);
            detalleXml.setImpuestos(new FacturaXml.ImpuestosDetalle(List.of(impuestoDetalleXml)));
            listaDetallesXml.add(detalleXml);

            FacturaDetalleEntity detEntity = new FacturaDetalleEntity();
            detEntity.setCodigoPrincipal(item.getCodigoPrincipal().trim());
            detEntity.setCodigoAuxiliar(item.getCodigoAuxiliar());
            detEntity.setDescripcion(item.getDescripcion().trim());
            detEntity.setCantidad(cantidad);
            detEntity.setPrecioUnitario(precioUnitario);
            detEntity.setDescuento(descuentoItem);
            detEntity.setPrecioTotalSinImpuesto(baseImponibleItem);
            detEntity.setCodigoPorcentajeIva(codPorcentaje);
            detEntity.setTarifaIva(tarifa);
            detEntity.setValorIva(valorIvaItem);
            listaDetallesEntity.add(detEntity);
        }

        BigDecimal propina = request.getPropina() != null
                ? request.getPropina().setScale(2, RoundingMode.HALF_UP)
                : new BigDecimal("0.00");

        BigDecimal maximoPropina = totalSinImpuestos.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        if (propina.compareTo(maximoPropina) > 0) {
            throw new IllegalArgumentException("La propina no puede superar el 10% del subtotal sin impuestos.");
        }

        BigDecimal importeTotal = totalSinImpuestos.add(totalIva).add(propina).setScale(2, RoundingMode.HALF_UP);

        if ("07".equals(request.getTipoIdentificacionComprador())) {
            if (!"9999999999999".equals(request.getIdentificacionComprador())) {
                throw new IllegalArgumentException(
                        "Para Venta a Consumidor Final (código 07) la identificación debe ser 13 dígitos de nueve (9999999999999).");
            }
            if (importeTotal.compareTo(LIMITE_CONSUMIDOR_FINAL) > 0) {
                throw new IllegalArgumentException(
                        "Las facturas a Consumidor Final no pueden superar los $50.00 USD. Identifique al adquirente con RUC, Cédula o Pasaporte.");
            }
        }

        SecuencialComprobante secComp = secuencialRepository.obtenerParaIncrementar(
                emisor.getId(), emisor.getCodEstablecimiento(), emisor.getPtoEmision(), COD_DOC_FACTURA
        ).orElseThrow(() -> new IllegalStateException(
                "No se ha configurado el número secuencial inicial para FACTURAS (01) en el punto de emisión "
                + emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision() + ". "
                + "Defínalo primero en PUT /api/v1/emisores/" + emisor.getRuc() + "/secuenciales"
        ));

        long nuevoNumero = secComp.getUltimoSecuencial() + 1;
        secComp.setUltimoSecuencial(nuevoNumero);
        secuencialRepository.save(secComp);

        String secuencial9Digitos = ClaveAccesoGenerator.formatearConCeros(String.valueOf(nuevoNumero), 9);

        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                fechaEmision,
                COD_DOC_FACTURA,
                emisor.getRuc(),
                emisor.getAmbiente(),
                emisor.getCodEstablecimiento(),
                emisor.getPtoEmision(),
                secuencial9Digitos,
                emisor.getTipoEmision()
        );

        FacturaXml.InfoTributaria infoTributaria = new FacturaXml.InfoTributaria();
        infoTributaria.setAmbiente(emisor.getAmbiente());
        infoTributaria.setTipoEmision(emisor.getTipoEmision());
        infoTributaria.setRazonSocial(emisor.getRazonSocial());
        infoTributaria.setNombreComercial(emisor.getNombreComercial());
        infoTributaria.setRuc(emisor.getRuc());
        infoTributaria.setClaveAcceso(claveAcceso);
        infoTributaria.setCodDoc(COD_DOC_FACTURA);
        infoTributaria.setEstab(emisor.getCodEstablecimiento());
        infoTributaria.setPtoEmi(emisor.getPtoEmision());
        infoTributaria.setSecuencial(secuencial9Digitos);
        infoTributaria.setDirMatriz(emisor.getDirMatriz());
        infoTributaria.setAgenteRetencion(emisor.getAgenteRetencion());
        infoTributaria.setContribuyenteRimpe(emisor.getContribuyenteRimpe());

        FacturaXml.Pago pagoXml = new FacturaXml.Pago(
                request.getFormaPago(), importeTotal, request.getPlazoPago(), request.getUnidadTiempoPago());

        FacturaXml.InfoFactura infoFactura = new FacturaXml.InfoFactura();
        infoFactura.setFechaEmision(fechaEmision.format(FORMATO_FECHA_XML));
        infoFactura.setDirEstablecimiento(emisor.getDirEstablecimiento());
        infoFactura.setContribuyenteEspecial(emisor.getContribuyenteEspecial());
        infoFactura.setObligadoContabilidad(emisor.getObligadoContabilidad());
        infoFactura.setTipoIdentificacionComprador(request.getTipoIdentificacionComprador());
        infoFactura.setGuiaRemision(request.getGuiaRemision());
        infoFactura.setRazonSocialComprador(request.getRazonSocialComprador().trim());
        infoFactura.setIdentificacionComprador(request.getIdentificacionComprador().trim());
        infoFactura.setDireccionComprador(request.getDireccionComprador() != null && !request.getDireccionComprador().isBlank()
                ? request.getDireccionComprador().trim() : null);
        infoFactura.setTotalSinImpuestos(totalSinImpuestos.setScale(2, RoundingMode.HALF_UP));
        infoFactura.setTotalDescuento(totalDescuento.setScale(2, RoundingMode.HALF_UP));
        infoFactura.setTotalConImpuestos(new FacturaXml.TotalConImpuestos(new ArrayList<>(mapaTotalImpuestos.values())));
        infoFactura.setPropina(propina);
        infoFactura.setImporteTotal(importeTotal);
        infoFactura.setMoneda("DOLAR");
        infoFactura.setPlaca(request.getPlaca() != null && !request.getPlaca().isBlank() ? request.getPlaca().trim() : null);
        infoFactura.setPagos(new FacturaXml.Pagos(List.of(pagoXml)));

        List<FacturaXml.CampoAdicional> camposAdicionales = new ArrayList<>();
        if (request.getEmailComprador() != null && !request.getEmailComprador().isBlank()) {
            camposAdicionales.add(new FacturaXml.CampoAdicional("Email", request.getEmailComprador().trim()));
        }
        if (request.getTelefonoComprador() != null && !request.getTelefonoComprador().isBlank()) {
            camposAdicionales.add(new FacturaXml.CampoAdicional("Telefono", request.getTelefonoComprador().trim()));
        }
        if (request.getInfoAdicional() != null) {
            request.getInfoAdicional().forEach((k, v) -> {
                if (v != null && !v.isBlank()) {
                    camposAdicionales.add(new FacturaXml.CampoAdicional(k.trim(), v.trim()));
                }
            });
        }
        if (emisor.getRucProveedorSoftware() != null && !emisor.getRucProveedorSoftware().isBlank()) {
            camposAdicionales.add(new FacturaXml.CampoAdicional("RUC Proveedor", emisor.getRucProveedorSoftware().trim()));
        }

        FacturaXml facturaXml = new FacturaXml();
        facturaXml.setId("comprobante");
        facturaXml.setVersion("2.1.0");
        facturaXml.setInfoTributaria(infoTributaria);
        facturaXml.setInfoFactura(infoFactura);
        facturaXml.setDetalles(new FacturaXml.Detalles(listaDetallesXml));
        facturaXml.setInfoAdicional(camposAdicionales.isEmpty() ? null : new FacturaXml.InfoAdicional(camposAdicionales));

        String xmlSinFirmar = xmlGeneratorService.generarXmlFactura(facturaXml);
        String xmlFirmado = (emisor.getArchivoFirmaP12() != null && emisor.getArchivoFirmaP12().length > 0)
                ? signerService.firmarXml(xmlSinFirmar, emisor.getArchivoFirmaP12(), emisor.getClaveFirmaP12())
                : signerService.firmarXml(xmlSinFirmar, emisor.getRutaFirmaP12(), emisor.getClaveFirmaP12());

        FacturaEntity facturaEntity = new FacturaEntity();
        facturaEntity.setEmisor(emisor);
        facturaEntity.setClaveAcceso(claveAcceso);
        facturaEntity.setSecuencial(secuencial9Digitos);
        facturaEntity.setFechaEmision(fechaEmision);
        facturaEntity.setTipoIdentificacionComprador(request.getTipoIdentificacionComprador());
        facturaEntity.setIdentificacionComprador(request.getIdentificacionComprador());
        facturaEntity.setRazonSocialComprador(request.getRazonSocialComprador());
        facturaEntity.setDireccionComprador(request.getDireccionComprador());
        facturaEntity.setEmailComprador(request.getEmailComprador());
        facturaEntity.setTotalSinImpuestos(totalSinImpuestos);
        facturaEntity.setTotalDescuento(totalDescuento);
        facturaEntity.setTotalIva(totalIva);
        facturaEntity.setImporteTotal(importeTotal);
        facturaEntity.setFormaPago(request.getFormaPago());
        facturaEntity.setEstadoSri("FIRMADA");
        facturaEntity.setXmlGenerado(xmlSinFirmar);
        facturaEntity.setXmlFirmado(xmlFirmado);

        listaDetallesEntity.forEach(facturaEntity::agregarDetalle);
        facturaRepository.save(facturaEntity);

        RespuestaRecepcionSriDto respRecepcion = sriSoapClientService.enviarComprobanteRecepcion(xmlFirmado, emisor.getAmbiente());
        List<MensajeSriDto> todosLosMensajes = new ArrayList<>(respRecepcion.getMensajes());

        String estadoAutorizacionFinal = "NO_ENVIADO";
        String fechaAutorizacionStr = null;

        if ("RECIBIDA".equalsIgnoreCase(respRecepcion.getEstado())) {
            facturaEntity.setEstadoSri("RECIBIDA");
            esperarProcesamientoSri(2500);

            RespuestaAutorizacionSriDto respAut = sriSoapClientService.consultarAutorizacionComprobante(claveAcceso, emisor.getAmbiente());
            estadoAutorizacionFinal = respAut.getEstado();
            fechaAutorizacionStr = respAut.getFechaAutorizacion();
            todosLosMensajes.addAll(respAut.getMensajes());

            facturaEntity.setEstadoSri(estadoAutorizacionFinal);
            if ("AUTORIZADO".equalsIgnoreCase(estadoAutorizacionFinal)) {
                facturaEntity.setFechaAutorizacion(LocalDateTime.now(ZONA_ECUADOR));
                facturaEntity.setXmlAutorizado(respAut.getXmlRespuestaRaw());

                if (facturaEntity.getEmailComprador() != null && !facturaEntity.getEmailComprador().isBlank()) {
                    try {
                        emailNotificationService.enviarComprobantePorEmail(facturaEntity, facturaEntity.getEmailComprador());
                    } catch (Exception ex) {
                        System.err.println("Advertencia: La factura se autorizó en el SRI pero falló el envío SMTP: " + ex.getMessage());
                    }
                }
            }
        } else {
            facturaEntity.setEstadoSri(respRecepcion.getEstado());
            estadoAutorizacionFinal = "DEVUELTA_EN_RECEPCION";
        }

        facturaEntity.setMensajesSri(todosLosMensajes.toString());
        facturaRepository.save(facturaEntity);

        String numeroComprobante = String.format("%s-%s-%s",
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), secuencial9Digitos);

        FacturaResponseDto responseDto = new FacturaResponseDto();
        responseDto.setFacturaId(facturaEntity.getId());
        responseDto.setRucEmisor(emisor.getRuc());
        responseDto.setNumeroComprobante(numeroComprobante);
        responseDto.setClaveAcceso(claveAcceso);
        responseDto.setAmbiente("2".equals(emisor.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS");
        responseDto.setEstadoRecepcion(respRecepcion.getEstado());
        responseDto.setEstadoAutorizacion(estadoAutorizacionFinal);
        responseDto.setFechaAutorizacion(fechaAutorizacionStr);
        responseDto.setTotalSinImpuestos(totalSinImpuestos);
        responseDto.setTotalDescuento(totalDescuento);
        responseDto.setTotalIva(totalIva);
        responseDto.setImporteTotal(importeTotal);
        responseDto.setMensajesSri(todosLosMensajes);
        return responseDto;
    }

    @Transactional
    public RespuestaAutorizacionSriDto consultarAutorizacionPorClave(String claveAcceso) {
        FacturaEntity factura = facturaRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new IllegalArgumentException("No existe una factura en la base de datos con la clave: " + claveAcceso));

        String ambiente = factura.getEmisor().getAmbiente();
        RespuestaAutorizacionSriDto respAut = sriSoapClientService.consultarAutorizacionComprobante(claveAcceso, ambiente);

        factura.setEstadoSri(respAut.getEstado());
        if ("AUTORIZADO".equalsIgnoreCase(respAut.getEstado())) {
            factura.setFechaAutorizacion(LocalDateTime.now(ZONA_ECUADOR));
            factura.setXmlAutorizado(respAut.getXmlRespuestaRaw());
        }
        if (!respAut.getMensajes().isEmpty()) {
            factura.setMensajesSri(respAut.getMensajes().toString());
        }
        facturaRepository.save(factura);

        return respAut;
    }

    @Transactional(readOnly = true)
    public String obtenerXmlFirmadoPorClave(String claveAcceso) {
        FacturaEntity factura = facturaRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la factura con clave: " + claveAcceso));
        return factura.getXmlFirmado();
    }

    @Transactional
    public FacturaResponseDto anularFacturaConNotaCredito(String claveAccesoFactura, String motivoAnulacion) {
        FacturaEntity facturaOriginal = facturaRepository.findByClaveAcceso(claveAccesoFactura)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró en la base de datos la factura con clave de acceso: " + claveAccesoFactura));

        if (!"AUTORIZADO".equalsIgnoreCase(facturaOriginal.getEstadoSri())) {
            throw new IllegalStateException(
                    "Solo se pueden anular con Nota de Crédito las facturas en estado AUTORIZADO. Estado actual: " + facturaOriginal.getEstadoSri());
        }

        if ("07".equals(facturaOriginal.getTipoIdentificacionComprador())) {
            throw new IllegalArgumentException(
                    "Según la sección 5.7 de la Ficha Técnica del SRI, no se pueden emitir Notas de Crédito a Consumidor Final (07).");
        }

        Emisor emisor = facturaOriginal.getEmisor();
        String codDocNotaCredito = "04";
        LocalDate fechaEmisionNC = LocalDate.now(ZONA_ECUADOR);

        SecuencialComprobante secComp = secuencialRepository.obtenerParaIncrementar(
                emisor.getId(), emisor.getCodEstablecimiento(), emisor.getPtoEmision(), codDocNotaCredito
        ).orElseThrow(() -> new IllegalStateException(
                "No se ha configurado el número secuencial inicial para NOTAS DE CRÉDITO (04) en el punto de emisión "
                + emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision() + ". "
                + "Defínalo primero en PUT /api/v1/emisores/" + emisor.getRuc() + "/secuenciales"
        ));

        long nuevoNumeroNC = secComp.getUltimoSecuencial() + 1;
        secComp.setUltimoSecuencial(nuevoNumeroNC);
        secuencialRepository.save(secComp);

        String secuencialNC9Digitos = ClaveAccesoGenerator.formatearConCeros(String.valueOf(nuevoNumeroNC), 9);

        String claveAccesoNC = claveAccesoGenerator.generarClaveAcceso(
                fechaEmisionNC,
                codDocNotaCredito,
                emisor.getRuc(),
                emisor.getAmbiente(),
                emisor.getCodEstablecimiento(),
                emisor.getPtoEmision(),
                secuencialNC9Digitos,
                emisor.getTipoEmision()
        );

        FacturaXml.InfoTributaria infoTributaria = new FacturaXml.InfoTributaria();
        infoTributaria.setAmbiente(emisor.getAmbiente());
        infoTributaria.setTipoEmision(emisor.getTipoEmision());
        infoTributaria.setRazonSocial(emisor.getRazonSocial());
        infoTributaria.setNombreComercial(emisor.getNombreComercial());
        infoTributaria.setRuc(emisor.getRuc());
        infoTributaria.setClaveAcceso(claveAccesoNC);
        infoTributaria.setCodDoc(codDocNotaCredito);
        infoTributaria.setEstab(emisor.getCodEstablecimiento());
        infoTributaria.setPtoEmi(emisor.getPtoEmision());
        infoTributaria.setSecuencial(secuencialNC9Digitos);
        infoTributaria.setDirMatriz(emisor.getDirMatriz());
        infoTributaria.setAgenteRetencion(emisor.getAgenteRetencion());
        infoTributaria.setContribuyenteRimpe(emisor.getContribuyenteRimpe());

        Map<String, NotaCreditoXml.TotalImpuestoNC> mapaImpuestosNC = new LinkedHashMap<>();
        List<NotaCreditoXml.DetalleNC> detallesNC = new ArrayList<>();

        for (FacturaDetalleEntity detOrig : facturaOriginal.getDetalles()) {
            String codPorcentaje = detOrig.getCodigoPorcentajeIva();
            BigDecimal baseImp = detOrig.getPrecioTotalSinImpuesto().setScale(2, RoundingMode.HALF_UP);
            BigDecimal valIva = detOrig.getValorIva().setScale(2, RoundingMode.HALF_UP);

            NotaCreditoXml.TotalImpuestoNC acum = mapaImpuestosNC.get(codPorcentaje);
            if (acum == null) {
                acum = new NotaCreditoXml.TotalImpuestoNC(COD_IMPUESTO_IVA, codPorcentaje, baseImp, valIva);
                mapaImpuestosNC.put(codPorcentaje, acum);
            } else {
                acum.setBaseImponible(acum.getBaseImponible().add(baseImp));
                acum.setValor(acum.getValor().add(valIva));
            }

            FacturaXml.ImpuestoDetalle impDet = new FacturaXml.ImpuestoDetalle(
                    COD_IMPUESTO_IVA,
                    codPorcentaje,
                    detOrig.getTarifaIva().setScale(2, RoundingMode.HALF_UP),
                    baseImp,
                    valIva
            );

            NotaCreditoXml.DetalleNC detNC = new NotaCreditoXml.DetalleNC();
            detNC.setCodigoInterno(detOrig.getCodigoPrincipal());
            detNC.setCodigoAdicional(detOrig.getCodigoAuxiliar());
            detNC.setDescripcion(detOrig.getDescripcion());
            detNC.setCantidad(detOrig.getCantidad().setScale(6, RoundingMode.HALF_UP));
            detNC.setPrecioUnitario(detOrig.getPrecioUnitario().setScale(6, RoundingMode.HALF_UP));
            detNC.setDescuento(detOrig.getDescuento().setScale(2, RoundingMode.HALF_UP));
            detNC.setPrecioTotalSinImpuesto(baseImp);
            detNC.setImpuestos(new FacturaXml.ImpuestosDetalle(List.of(impDet)));
            detallesNC.add(detNC);
        }

        String numDocModificado = String.format("%s-%s-%s",
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), facturaOriginal.getSecuencial());

        NotaCreditoXml.InfoNotaCredito infoNC = new NotaCreditoXml.InfoNotaCredito();
        infoNC.setFechaEmision(fechaEmisionNC.format(FORMATO_FECHA_XML));
        infoNC.setDirEstablecimiento(emisor.getDirEstablecimiento());
        infoNC.setTipoIdentificacionComprador(facturaOriginal.getTipoIdentificacionComprador());
        infoNC.setRazonSocialComprador(facturaOriginal.getRazonSocialComprador());
        infoNC.setIdentificacionComprador(facturaOriginal.getIdentificacionComprador());
        infoNC.setContribuyenteEspecial(emisor.getContribuyenteEspecial());
        infoNC.setObligadoContabilidad(emisor.getObligadoContabilidad());
        infoNC.setCodDocModificado("01"); // Sustento: Factura
        infoNC.setNumDocModificado(numDocModificado);
        infoNC.setFechaEmisionDocSustento(facturaOriginal.getFechaEmision().format(FORMATO_FECHA_XML));
        infoNC.setTotalSinImpuestos(facturaOriginal.getTotalSinImpuestos().setScale(2, RoundingMode.HALF_UP));
        infoNC.setValorModificacion(facturaOriginal.getImporteTotal().setScale(2, RoundingMode.HALF_UP));
        infoNC.setMoneda("DOLAR");
        infoNC.setTotalConImpuestos(new NotaCreditoXml.TotalConImpuestosNC(new ArrayList<>(mapaImpuestosNC.values())));
        infoNC.setMotivo(motivoAnulacion != null && !motivoAnulacion.isBlank()
                ? motivoAnulacion.trim() : "ANULACION DE FACTURA");

        List<FacturaXml.CampoAdicional> camposAdicionales = new ArrayList<>();
        if (facturaOriginal.getEmailComprador() != null && !facturaOriginal.getEmailComprador().isBlank()) {
            camposAdicionales.add(new FacturaXml.CampoAdicional("Email", facturaOriginal.getEmailComprador()));
        }

        NotaCreditoXml ncXml = new NotaCreditoXml();
        ncXml.setId("comprobante");
        ncXml.setVersion("1.1.0");
        ncXml.setInfoTributaria(infoTributaria);
        ncXml.setInfoNotaCredito(infoNC);
        ncXml.setDetalles(new NotaCreditoXml.Detalles(detallesNC));
        ncXml.setInfoAdicional(camposAdicionales.isEmpty() ? null : new FacturaXml.InfoAdicional(camposAdicionales));

        String xmlSinFirmar = xmlGeneratorService.generarXmlNotaCredito(ncXml);
        String xmlFirmado = (emisor.getArchivoFirmaP12() != null && emisor.getArchivoFirmaP12().length > 0)
                ? signerService.firmarXml(xmlSinFirmar, emisor.getArchivoFirmaP12(), emisor.getClaveFirmaP12())
                : signerService.firmarXml(xmlSinFirmar, emisor.getRutaFirmaP12(), emisor.getClaveFirmaP12());

        RespuestaRecepcionSriDto respRecepcion = sriSoapClientService.enviarComprobanteRecepcion(xmlFirmado, emisor.getAmbiente());
        List<MensajeSriDto> mensajes = new ArrayList<>(respRecepcion.getMensajes());
        String estadoAutorizacionNC = "DEVUELTA_EN_RECEPCION";
        String fechaAutNC = null;

        if ("RECIBIDA".equalsIgnoreCase(respRecepcion.getEstado())) {
            esperarProcesamientoSri(2500);
            RespuestaAutorizacionSriDto respAut = sriSoapClientService.consultarAutorizacionComprobante(claveAccesoNC, emisor.getAmbiente());
            estadoAutorizacionNC = respAut.getEstado();
            fechaAutNC = respAut.getFechaAutorizacion();
            mensajes.addAll(respAut.getMensajes());

            if ("AUTORIZADO".equalsIgnoreCase(estadoAutorizacionNC)) {
                facturaOriginal.setEstadoSri("ANULADA_CON_NC");
                facturaOriginal.setMensajesSri("Anulada mediante Nota de Crédito Nro. "
                        + emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision() + "-" + secuencialNC9Digitos
                        + " (Clave NC: " + claveAccesoNC + ")");
                facturaRepository.save(facturaOriginal);
            }
        }

        FacturaResponseDto response = new FacturaResponseDto();
        response.setFacturaId(facturaOriginal.getId());
        response.setRucEmisor(emisor.getRuc());
        response.setNumeroComprobante(emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision() + "-" + secuencialNC9Digitos);
        response.setClaveAcceso(claveAccesoNC);
        response.setAmbiente("2".equals(emisor.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS");
        response.setEstadoRecepcion(respRecepcion.getEstado());
        response.setEstadoAutorizacion(estadoAutorizacionNC);
        response.setFechaAutorizacion(fechaAutNC);
        response.setTotalSinImpuestos(facturaOriginal.getTotalSinImpuestos());
        response.setTotalDescuento(facturaOriginal.getTotalDescuento());
        response.setTotalIva(facturaOriginal.getTotalIva());
        response.setImporteTotal(facturaOriginal.getImporteTotal());
        response.setMensajesSri(mensajes);
        return response;
    }


    @Transactional
    public RespuestaAutorizacionSriDto verificarEstadoValidezSri(String claveAcceso) {
        FacturaEntity factura = facturaRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new IllegalArgumentException("No existe comprobante en la BD con clave: " + claveAcceso));

        RespuestaAutorizacionSriDto resp = sriSoapClientService.consultarEstadoValidezComprobante(
                claveAcceso, factura.getEmisor().getAmbiente());

        if ("ANULADO".equalsIgnoreCase(resp.getEstado()) || "PENDIENTE DE ANULAR".equalsIgnoreCase(resp.getEstado())) {
            factura.setEstadoSri(resp.getEstado());
            facturaRepository.save(factura);
        }
        return resp;
    }

    @Transactional(readOnly = true)
    public byte[] generarRidePdfPorClave(String claveAcceso) {
        FacturaEntity factura = facturaRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la factura con clave: " + claveAcceso));
        return ridePdfGeneratorService.generarRideFacturaPdf(factura);
    }

 
    @Transactional(readOnly = true)
    public void enviarFacturaPorEmailManual(String claveAcceso, String emailDestino) {
        FacturaEntity factura = facturaRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la factura con clave: " + claveAcceso));

        String correoFinal = (emailDestino != null && !emailDestino.isBlank())
                ? emailDestino.trim()
                : factura.getEmailComprador();

        if (correoFinal == null || correoFinal.isBlank()) {
            throw new IllegalArgumentException("Debe especificar un correo electrónico de destino.");
        }

        emailNotificationService.enviarComprobantePorEmail(factura, correoFinal);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarFacturasGuardadas() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (FacturaEntity f : facturaRepository.findAll()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", f.getId());
            item.put("numeroComprobante", f.getEmisor().getCodEstablecimiento() + "-" + f.getEmisor().getPtoEmision() + "-" + f.getSecuencial());
            item.put("claveAcceso", f.getClaveAcceso());
            item.put("fechaEmision", f.getFechaEmision().toString());
            item.put("comprador", f.getRazonSocialComprador());
            item.put("identificacionComprador", f.getIdentificacionComprador());
            item.put("importeTotal", f.getImporteTotal());
            item.put("estadoSri", f.getEstadoSri());
            item.put("fechaAutorizacion", f.getFechaAutorizacion());
            resultado.add(item);
        }
        return resultado;
    }

    private void esperarProcesamientoSri(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
