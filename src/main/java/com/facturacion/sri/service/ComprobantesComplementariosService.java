package com.facturacion.sri.service;

import com.facturacion.sri.dto.*;
import com.facturacion.sri.entity.ComprobanteElectronicoEntity;
import com.facturacion.sri.entity.Emisor;
import com.facturacion.sri.entity.SecuencialComprobante;
import com.facturacion.sri.repository.ComprobanteElectronicoRepository;
import com.facturacion.sri.repository.EmisorRepository;
import com.facturacion.sri.repository.SecuencialRepository;
import com.facturacion.sri.util.ClaveAccesoGenerator;
import com.facturacion.sri.xml.FacturaXml;
import com.facturacion.sri.xml.LiquidacionCompraXml;
import com.facturacion.sri.xml.NotaCreditoXml;
import com.facturacion.sri.xml.NotaDebitoXml;
import com.facturacion.sri.xml.GuiaRemisionXml;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.facturacion.sri.xml.ComprobanteRetencionXml;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ComprobantesComplementariosService {

    private static final String COD_DOC_LIQUIDACION = "03";
    private static final String COD_DOC_NOTA_CREDITO = "04";
    private static final String COD_DOC_NOTA_DEBITO = "05";
    private static final String COD_IMPUESTO_IVA = "2";

    private static final DateTimeFormatter FORMATO_FECHA_XML = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ZoneId ZONA_ECUADOR = ZoneId.of("America/Guayaquil");

    private final EmisorRepository emisorRepository;
    private final SecuencialRepository secuencialRepository;
    private final ComprobanteElectronicoRepository comprobanteRepository;
    private final ClaveAccesoGenerator claveAccesoGenerator;
    private final XmlGeneratorService xmlGeneratorService;
    private final XadesBesSignerService signerService;
    private final SriSoapClientService sriSoapClientService;

    public ComprobantesComplementariosService(
            EmisorRepository emisorRepository,
            SecuencialRepository secuencialRepository,
            ComprobanteElectronicoRepository comprobanteRepository,
            ClaveAccesoGenerator claveAccesoGenerator,
            XmlGeneratorService xmlGeneratorService,
            XadesBesSignerService signerService,
            SriSoapClientService sriSoapClientService) {
        this.emisorRepository = emisorRepository;
        this.secuencialRepository = secuencialRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.claveAccesoGenerator = claveAccesoGenerator;
        this.xmlGeneratorService = xmlGeneratorService;
        this.signerService = signerService;
        this.sriSoapClientService = sriSoapClientService;
    }

    // =========================================================================
    // 1. EMISIÓN DE NOTA DE CRÉDITO (codDoc = "04", v1.1.0)
    // =========================================================================
    @Transactional
    public FacturaResponseDto emitirNotaCredito(EmitirNotaCreditoRequestDto request) {
        Emisor emisor = obtenerEmisorActivo(request.getRucEmisor());
        LocalDate fechaEmision = LocalDate.now(ZONA_ECUADOR);

        BigDecimal totalSinImpuestos = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;

        Map<String, NotaCreditoXml.TotalImpuestoNC> mapaImpuestos = new LinkedHashMap<>();
        List<NotaCreditoXml.DetalleNC> detallesXml = new ArrayList<>();

        for (EmitirFacturaRequestDto.DetalleItemDto item : request.getDetalles()) {
            BigDecimal cantidad = item.getCantidad().setScale(6, RoundingMode.HALF_UP);
            BigDecimal precioUnitario = item.getPrecioUnitario().setScale(6, RoundingMode.HALF_UP);
            BigDecimal descuento = item.getDescuento().setScale(2, RoundingMode.HALF_UP);

            BigDecimal baseImp = cantidad.multiply(precioUnitario).subtract(descuento).setScale(2, RoundingMode.HALF_UP);
            BigDecimal tarifa = item.getTarifaIva().setScale(2, RoundingMode.HALF_UP);
            BigDecimal valorIva = baseImp.multiply(tarifa).divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);

            totalSinImpuestos = totalSinImpuestos.add(baseImp);
            totalDescuento = totalDescuento.add(descuento);
            totalIva = totalIva.add(valorIva);

            String codPorcentaje = item.getCodigoPorcentajeIva().trim();
            NotaCreditoXml.TotalImpuestoNC acum = mapaImpuestos.get(codPorcentaje);
            if (acum == null) {
                acum = new NotaCreditoXml.TotalImpuestoNC(COD_IMPUESTO_IVA, codPorcentaje, baseImp, valorIva);
                mapaImpuestos.put(codPorcentaje, acum);
            } else {
                acum.setBaseImponible(acum.getBaseImponible().add(baseImp));
                acum.setValor(acum.getValor().add(valorIva));
            }

            FacturaXml.ImpuestoDetalle impDet = new FacturaXml.ImpuestoDetalle(
                    COD_IMPUESTO_IVA, codPorcentaje, tarifa, baseImp, valorIva);

            NotaCreditoXml.DetalleNC det = new NotaCreditoXml.DetalleNC();
            det.setCodigoInterno(item.getCodigoPrincipal().trim());
            det.setCodigoAdicional(item.getCodigoAuxiliar() != null && !item.getCodigoAuxiliar().isBlank()
                    ? item.getCodigoAuxiliar().trim() : null);
            det.setDescripcion(item.getDescripcion().trim());
            det.setCantidad(cantidad);
            det.setPrecioUnitario(precioUnitario);
            det.setDescuento(descuento);
            det.setPrecioTotalSinImpuesto(baseImp);
            det.setImpuestos(new FacturaXml.ImpuestosDetalle(List.of(impDet)));
            detallesXml.add(det);
        }

        BigDecimal valorModificacion = totalSinImpuestos.add(totalIva).setScale(2, RoundingMode.HALF_UP);

        String secuencial9 = obtenerSiguienteSecuencial(emisor, COD_DOC_NOTA_CREDITO);
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                fechaEmision, COD_DOC_NOTA_CREDITO, emisor.getRuc(), emisor.getAmbiente(),
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), secuencial9, emisor.getTipoEmision()
        );

        FacturaXml.InfoTributaria infoTrib = construirInfoTributaria(emisor, COD_DOC_NOTA_CREDITO, claveAcceso, secuencial9);

        NotaCreditoXml.InfoNotaCredito infoNC = new NotaCreditoXml.InfoNotaCredito();
        infoNC.setFechaEmision(fechaEmision.format(FORMATO_FECHA_XML));
        infoNC.setDirEstablecimiento(emisor.getDirEstablecimiento());
        infoNC.setTipoIdentificacionComprador(request.getTipoIdentificacionComprador());
        infoNC.setRazonSocialComprador(request.getRazonSocialComprador().trim());
        infoNC.setIdentificacionComprador(request.getIdentificacionComprador().trim());
        infoNC.setContribuyenteEspecial(emisor.getContribuyenteEspecial());
        infoNC.setObligadoContabilidad(emisor.getObligadoContabilidad());
        infoNC.setCodDocModificado(request.getCodDocModificado());
        infoNC.setNumDocModificado(request.getNumDocModificado());
        infoNC.setFechaEmisionDocSustento(request.getFechaEmisionDocSustento());
        infoNC.setTotalSinImpuestos(totalSinImpuestos.setScale(2, RoundingMode.HALF_UP));
        infoNC.setValorModificacion(valorModificacion);
        infoNC.setMoneda("DOLAR");
        infoNC.setTotalConImpuestos(new NotaCreditoXml.TotalConImpuestosNC(new ArrayList<>(mapaImpuestos.values())));
        infoNC.setMotivo(request.getMotivo().trim());

        NotaCreditoXml ncXml = new NotaCreditoXml();
        ncXml.setId("comprobante");
        ncXml.setVersion("1.1.0");
        ncXml.setInfoTributaria(infoTrib);
        ncXml.setInfoNotaCredito(infoNC);
        ncXml.setDetalles(new NotaCreditoXml.Detalles(detallesXml));
        ncXml.setInfoAdicional(construirInfoAdicional(request.getEmailComprador(), request.getInfoAdicional(), emisor));

        String xmlSinFirmar = xmlGeneratorService.generarXmlNotaCredito(ncXml);
        return procesarFirmaYEnvioSri(
                emisor, COD_DOC_NOTA_CREDITO, "NOTA_CREDITO", claveAcceso, secuencial9, fechaEmision,
                request.getIdentificacionComprador(), request.getRazonSocialComprador(), request.getNumDocModificado(),
                totalSinImpuestos, totalDescuento, totalIva, valorModificacion, xmlSinFirmar
        );
    }

    // =========================================================================
    // 2. EMISIÓN DE NOTA DE DÉBITO (codDoc = "05", v1.0.0)
    // =========================================================================
    @Transactional
    public FacturaResponseDto emitirNotaDebito(EmitirNotaDebitoRequestDto request) {
        Emisor emisor = obtenerEmisorActivo(request.getRucEmisor());
        LocalDate fechaEmision = LocalDate.now(ZONA_ECUADOR);

        BigDecimal totalSinImpuestos = BigDecimal.ZERO;
        List<NotaDebitoXml.Motivo> listaMotivosXml = new ArrayList<>();

        for (EmitirNotaDebitoRequestDto.MotivoItemDto m : request.getMotivos()) {
            BigDecimal val = m.getValor().setScale(2, RoundingMode.HALF_UP);
            totalSinImpuestos = totalSinImpuestos.add(val);
            listaMotivosXml.add(new NotaDebitoXml.Motivo(m.getRazon().trim(), val));
        }

        totalSinImpuestos = totalSinImpuestos.setScale(2, RoundingMode.HALF_UP);
        BigDecimal tarifa = request.getTarifaIva().setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorIva = totalSinImpuestos.multiply(tarifa).divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
        BigDecimal valorTotal = totalSinImpuestos.add(valorIva).setScale(2, RoundingMode.HALF_UP);

        String secuencial9 = obtenerSiguienteSecuencial(emisor, COD_DOC_NOTA_DEBITO);
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                fechaEmision, COD_DOC_NOTA_DEBITO, emisor.getRuc(), emisor.getAmbiente(),
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), secuencial9, emisor.getTipoEmision()
        );

        FacturaXml.InfoTributaria infoTrib = construirInfoTributaria(emisor, COD_DOC_NOTA_DEBITO, claveAcceso, secuencial9);

        FacturaXml.ImpuestoDetalle impuestoND = new FacturaXml.ImpuestoDetalle(
                COD_IMPUESTO_IVA, request.getCodigoPorcentajeIva().trim(), tarifa, totalSinImpuestos, valorIva);

        FacturaXml.Pago pagoND = new FacturaXml.Pago(
                request.getFormaPago(), valorTotal, request.getPlazoPago(), request.getUnidadTiempoPago());

        NotaDebitoXml.InfoNotaDebito infoND = new NotaDebitoXml.InfoNotaDebito();
        infoND.setFechaEmision(fechaEmision.format(FORMATO_FECHA_XML));
        infoND.setDirEstablecimiento(emisor.getDirEstablecimiento());
        infoND.setTipoIdentificacionComprador(request.getTipoIdentificacionComprador());
        infoND.setRazonSocialComprador(request.getRazonSocialComprador().trim());
        infoND.setIdentificacionComprador(request.getIdentificacionComprador().trim());
        infoND.setContribuyenteEspecial(emisor.getContribuyenteEspecial());
        infoND.setObligadoContabilidad(emisor.getObligadoContabilidad());
        infoND.setCodDocModificado(request.getCodDocModificado());
        infoND.setNumDocModificado(request.getNumDocModificado());
        infoND.setFechaEmisionDocSustento(request.getFechaEmisionDocSustento());
        infoND.setTotalSinImpuestos(totalSinImpuestos);
        infoND.setImpuestos(new NotaDebitoXml.ImpuestosND(List.of(impuestoND)));
        infoND.setValorTotal(valorTotal);
        infoND.setPagos(new FacturaXml.Pagos(List.of(pagoND)));

        NotaDebitoXml ndXml = new NotaDebitoXml();
        ndXml.setId("comprobante");
        ndXml.setVersion("1.0.0");
        ndXml.setInfoTributaria(infoTrib);
        ndXml.setInfoNotaDebito(infoND);
        ndXml.setMotivos(new NotaDebitoXml.Motivos(listaMotivosXml));
        ndXml.setInfoAdicional(construirInfoAdicional(request.getEmailComprador(), request.getInfoAdicional(), emisor));

        String xmlSinFirmar = xmlGeneratorService.generarXmlNotaDebito(ndXml);
        return procesarFirmaYEnvioSri(
                emisor, COD_DOC_NOTA_DEBITO, "NOTA_DEBITO", claveAcceso, secuencial9, fechaEmision,
                request.getIdentificacionComprador(), request.getRazonSocialComprador(), request.getNumDocModificado(),
                totalSinImpuestos, BigDecimal.ZERO, valorIva, valorTotal, xmlSinFirmar
        );
    }

    // =========================================================================
    // 3. EMISIÓN DE LIQUIDACIÓN DE COMPRA (codDoc = "03", v1.1.0)
    // =========================================================================
    @Transactional
    public FacturaResponseDto emitirLiquidacionCompra(EmitirLiquidacionCompraRequestDto request) {
        Emisor emisor = obtenerEmisorActivo(request.getRucEmisor());
        LocalDate fechaEmision = LocalDate.now(ZONA_ECUADOR);

        BigDecimal totalSinImpuestos = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;

        Map<String, FacturaXml.TotalImpuesto> mapaTotalImpuestos = new LinkedHashMap<>();
        List<FacturaXml.Detalle> listaDetallesXml = new ArrayList<>();

        for (EmitirFacturaRequestDto.DetalleItemDto item : request.getDetalles()) {
            BigDecimal cantidad = item.getCantidad().setScale(6, RoundingMode.HALF_UP);
            BigDecimal precioUnitario = item.getPrecioUnitario().setScale(6, RoundingMode.HALF_UP);
            BigDecimal descuentoItem = item.getDescuento().setScale(2, RoundingMode.HALF_UP);

            BigDecimal baseImponibleItem = cantidad.multiply(precioUnitario).subtract(descuentoItem).setScale(2, RoundingMode.HALF_UP);
            BigDecimal tarifa = item.getTarifaIva().setScale(2, RoundingMode.HALF_UP);
            BigDecimal valorIvaItem = baseImponibleItem.multiply(tarifa).divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);

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
        }

        BigDecimal importeTotal = totalSinImpuestos.add(totalIva).setScale(2, RoundingMode.HALF_UP);

        String secuencial9 = obtenerSiguienteSecuencial(emisor, COD_DOC_LIQUIDACION);
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                fechaEmision, COD_DOC_LIQUIDACION, emisor.getRuc(), emisor.getAmbiente(),
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), secuencial9, emisor.getTipoEmision()
        );

        FacturaXml.InfoTributaria infoTrib = construirInfoTributaria(emisor, COD_DOC_LIQUIDACION, claveAcceso, secuencial9);
        FacturaXml.Pago pagoLC = new FacturaXml.Pago(
                request.getFormaPago(), importeTotal, request.getPlazoPago(), request.getUnidadTiempoPago());

        LiquidacionCompraXml.InfoLiquidacionCompra infoLC = new LiquidacionCompraXml.InfoLiquidacionCompra();
        infoLC.setFechaEmision(fechaEmision.format(FORMATO_FECHA_XML));
        infoLC.setDirEstablecimiento(emisor.getDirEstablecimiento());
        infoLC.setContribuyenteEspecial(emisor.getContribuyenteEspecial());
        infoLC.setObligadoContabilidad(emisor.getObligadoContabilidad());
        infoLC.setTipoIdentificacionProveedor(request.getTipoIdentificacionProveedor());
        infoLC.setRazonSocialProveedor(request.getRazonSocialProveedor().trim());
        infoLC.setIdentificacionProveedor(request.getIdentificacionProveedor().trim());
        infoLC.setDireccionProveedor(request.getDireccionProveedor() != null && !request.getDireccionProveedor().isBlank()
                ? request.getDireccionProveedor().trim() : null);
        infoLC.setTotalSinImpuestos(totalSinImpuestos.setScale(2, RoundingMode.HALF_UP));
        infoLC.setTotalDescuento(totalDescuento.setScale(2, RoundingMode.HALF_UP));
        infoLC.setTotalConImpuestos(new FacturaXml.TotalConImpuestos(new ArrayList<>(mapaTotalImpuestos.values())));
        infoLC.setImporteTotal(importeTotal);
        infoLC.setMoneda("DOLAR");
        infoLC.setPagos(new FacturaXml.Pagos(List.of(pagoLC)));

        LiquidacionCompraXml lcXml = new LiquidacionCompraXml();
        lcXml.setId("comprobante");
        lcXml.setVersion("1.1.0");
        lcXml.setInfoTributaria(infoTrib);
        lcXml.setInfoLiquidacionCompra(infoLC);
        lcXml.setDetalles(new FacturaXml.Detalles(listaDetallesXml));
        lcXml.setInfoAdicional(construirInfoAdicional(request.getEmailProveedor(), request.getInfoAdicional(), emisor));

        String xmlSinFirmar = xmlGeneratorService.generarXmlLiquidacionCompra(lcXml);
        return procesarFirmaYEnvioSri(
                emisor, COD_DOC_LIQUIDACION, "LIQUIDACION_COMPRA", claveAcceso, secuencial9, fechaEmision,
                request.getIdentificacionProveedor(), request.getRazonSocialProveedor(), null,
                totalSinImpuestos, totalDescuento, totalIva, importeTotal, xmlSinFirmar
        );
    }

    @Transactional(readOnly = true)
    public String obtenerXmlFirmadoPorClave(String claveAcceso) {
        ComprobanteElectronicoEntity comp = comprobanteRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new IllegalArgumentException("No existe comprobante con la clave: " + claveAcceso));
        return comp.getXmlFirmado();
    }

    // =========================================================================
    // 4. EMISIÓN DE COMPROBANTE DE RETENCIÓN (codDoc = "07", v2.0.0 ATS)
    // =========================================================================
    @Transactional
    public FacturaResponseDto emitirComprobanteRetencion(EmitirComprobanteRetencionRequestDto request) {
        Emisor emisor = obtenerEmisorActivo(request.getRucEmisor());
        String codDocRetencion = "07";
        LocalDate fechaEmision = LocalDate.now(ZONA_ECUADOR);

        // Limpiar guiones de numDocSustento para dejar exactamente 15 dígitos numéricos (Regla XSD v2.0.0)
        String numDocSustentoLimpio = request.getNumDocSustento().replace("-", "").trim();
        if (!numDocSustentoLimpio.matches("\\d{15}")) {
            throw new IllegalArgumentException("El número de documento sustento debe contener 15 dígitos (ej. 001001000000001).");
        }

        BigDecimal baseSustento = request.getBaseImponibleSustento().setScale(2, RoundingMode.HALF_UP);
        BigDecimal tarifaSustento = request.getTarifaIvaSustento().setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorIvaSustento = baseSustento.multiply(tarifaSustento)
                .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
        BigDecimal importeTotalSustento = baseSustento.add(valorIvaSustento).setScale(2, RoundingMode.HALF_UP);

        // Construir lista de retenciones y sumar el total retenido
        List<ComprobanteRetencionXml.Retencion> listaRetencionesXml = new ArrayList<>();
        BigDecimal totalRetenido = BigDecimal.ZERO;

        for (EmitirComprobanteRetencionRequestDto.ItemRetencionDto item : request.getRetenciones()) {
            BigDecimal baseRet = item.getBaseImponible().setScale(2, RoundingMode.HALF_UP);
            BigDecimal porcRet = item.getPorcentajeRetener().setScale(2, RoundingMode.HALF_UP);
            BigDecimal valRetenido = baseRet.multiply(porcRet)
                    .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);

            totalRetenido = totalRetenido.add(valRetenido);

            listaRetencionesXml.add(new ComprobanteRetencionXml.Retencion(
                    item.getCodigo().trim(),
                    item.getCodigoRetencion().trim(),
                    baseRet,
                    porcRet,
                    valRetenido
            ));
        }

        String secuencial9 = obtenerSiguienteSecuencial(emisor, codDocRetencion);
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                fechaEmision, codDocRetencion, emisor.getRuc(), emisor.getAmbiente(),
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), secuencial9, emisor.getTipoEmision()
        );

        FacturaXml.InfoTributaria infoTrib = construirInfoTributaria(emisor, codDocRetencion, claveAcceso, secuencial9);

        ComprobanteRetencionXml.InfoCompRetencion infoCompRet = new ComprobanteRetencionXml.InfoCompRetencion();
        infoCompRet.setFechaEmision(fechaEmision.format(FORMATO_FECHA_XML));
        infoCompRet.setDirEstablecimiento(emisor.getDirEstablecimiento());
        infoCompRet.setContribuyenteEspecial(emisor.getContribuyenteEspecial());
        infoCompRet.setObligadoContabilidad(emisor.getObligadoContabilidad());
        infoCompRet.setTipoIdentificacionSujetoRetenido(request.getTipoIdentificacionSujetoRetenido());
        infoCompRet.setTipoSujetoRetenido(request.getTipoSujetoRetenido());
        infoCompRet.setParteRel(request.getParteRel());
        infoCompRet.setRazonSocialSujetoRetenido(request.getRazonSocialSujetoRetenido().trim());
        infoCompRet.setIdentificacionSujetoRetenido(request.getIdentificacionSujetoRetenido().trim());
        infoCompRet.setPeriodoFiscal(request.getPeriodoFiscal().trim());

        ComprobanteRetencionXml.ImpuestoDocSustento impSustento = new ComprobanteRetencionXml.ImpuestoDocSustento(
                COD_IMPUESTO_IVA,
                request.getCodigoPorcentajeIvaSustento().trim(),
                baseSustento,
                tarifaSustento,
                valorIvaSustento
        );

        ComprobanteRetencionXml.PagoRetencion pagoRet = new ComprobanteRetencionXml.PagoRetencion(
                request.getFormaPago(), importeTotalSustento);

        ComprobanteRetencionXml.DocSustento docSustento = new ComprobanteRetencionXml.DocSustento();
        docSustento.setCodSustento(request.getCodSustento());
        docSustento.setCodDocSustento(request.getCodDocSustento());
        docSustento.setNumDocSustento(numDocSustentoLimpio);
        docSustento.setFechaEmisionDocSustento(request.getFechaEmisionDocSustento());
        docSustento.setFechaRegistroContable(fechaEmision.format(FORMATO_FECHA_XML));
        docSustento.setNumAutDocSustento(request.getNumAutDocSustento());
        docSustento.setPagoLocExt("01"); // Pago a residente local
        docSustento.setTotalSinImpuestos(baseSustento);
        docSustento.setImporteTotal(importeTotalSustento);
        docSustento.setImpuestosDocSustento(new ComprobanteRetencionXml.ImpuestosDocSustento(List.of(impSustento)));
        docSustento.setRetenciones(new ComprobanteRetencionXml.Retenciones(listaRetencionesXml));
        docSustento.setPagos(new ComprobanteRetencionXml.PagosRetencion(List.of(pagoRet)));

        ComprobanteRetencionXml retXml = new ComprobanteRetencionXml();
        retXml.setId("comprobante");
        retXml.setVersion("2.0.0");
        retXml.setInfoTributaria(infoTrib);
        retXml.setInfoCompRetencion(infoCompRet);
        retXml.setDocsSustento(new ComprobanteRetencionXml.DocsSustento(List.of(docSustento)));
        retXml.setInfoAdicional(construirInfoAdicional(request.getEmailSujetoRetenido(), request.getInfoAdicional(), emisor));

        String xmlSinFirmar = xmlGeneratorService.generarXmlComprobanteRetencion(retXml);
        return procesarFirmaYEnvioSri(
                emisor, codDocRetencion, "COMPROBANTE_RETENCION", claveAcceso, secuencial9, fechaEmision,
                request.getIdentificacionSujetoRetenido(), request.getRazonSocialSujetoRetenido(), numDocSustentoLimpio,
                baseSustento, BigDecimal.ZERO, valorIvaSustento, totalRetenido, xmlSinFirmar
        );
    }

    // =========================================================================
    // 5. EMISIÓN DE GUÍA DE REMISIÓN (codDoc = "06", v1.1.0)
    // =========================================================================
    @Transactional
    public FacturaResponseDto emitirGuiaRemision(EmitirGuiaRemisionRequestDto request) {
        Emisor emisor = obtenerEmisorActivo(request.getRucEmisor());
        String codDocGuia = "06";
        LocalDate fechaEmision = LocalDate.now(ZONA_ECUADOR);

        // Validar Error 82 del SRI: fechaIniTransporte no puede ser menor a la fecha de emisión
        LocalDate fechaIni = LocalDate.parse(request.getFechaIniTransporte(), FORMATO_FECHA_XML);
        LocalDate fechaFin = LocalDate.parse(request.getFechaFinTransporte(), FORMATO_FECHA_XML);
        if (fechaIni.isBefore(fechaEmision)) {
            throw new IllegalArgumentException(
                    "Error 82 SRI: La fecha de inicio de transporte (" + request.getFechaIniTransporte()
                    + ") no puede ser menor a la fecha de emisión de la guía (" + fechaEmision.format(FORMATO_FECHA_XML) + ").");
        }
        if (fechaFin.isBefore(fechaIni)) {
            throw new IllegalArgumentException("La fecha de fin de transporte no puede ser anterior a la fecha de inicio.");
        }

        List<GuiaRemisionXml.DetalleGuia> listaDetallesGuia = new ArrayList<>();
        for (EmitirGuiaRemisionRequestDto.ItemGuiaDto item : request.getDetalles()) {
            listaDetallesGuia.add(new GuiaRemisionXml.DetalleGuia(
                    item.getCodigoInterno().trim(),
                    item.getCodigoAdicional() != null && !item.getCodigoAdicional().isBlank() ? item.getCodigoAdicional().trim() : null,
                    item.getDescripcion().trim(),
                    item.getCantidad().setScale(6, RoundingMode.HALF_UP)
            ));
        }

        String secuencial9 = obtenerSiguienteSecuencial(emisor, codDocGuia);
        String claveAcceso = claveAccesoGenerator.generarClaveAcceso(
                fechaEmision, codDocGuia, emisor.getRuc(), emisor.getAmbiente(),
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), secuencial9, emisor.getTipoEmision()
        );

        FacturaXml.InfoTributaria infoTrib = construirInfoTributaria(emisor, codDocGuia, claveAcceso, secuencial9);

        GuiaRemisionXml.InfoGuiaRemision infoGuia = new GuiaRemisionXml.InfoGuiaRemision();
        infoGuia.setDirEstablecimiento(emisor.getDirEstablecimiento());
        infoGuia.setDirPartida(request.getDirPartida().trim());
        infoGuia.setRazonSocialTransportista(request.getRazonSocialTransportista().trim());
        infoGuia.setTipoIdentificacionTransportista(request.getTipoIdentificacionTransportista());
        infoGuia.setRucTransportista(request.getRucTransportista().trim());
        infoGuia.setObligadoContabilidad(emisor.getObligadoContabilidad());
        infoGuia.setContribuyenteEspecial(emisor.getContribuyenteEspecial());
        infoGuia.setFechaIniTransporte(request.getFechaIniTransporte());
        infoGuia.setFechaFinTransporte(request.getFechaFinTransporte());
        infoGuia.setPlaca(request.getPlaca().trim());

        GuiaRemisionXml.Destinatario destinatario = new GuiaRemisionXml.Destinatario();
        destinatario.setIdentificacionDestinatario(request.getIdentificacionDestinatario().trim());
        destinatario.setRazonSocialDestinatario(request.getRazonSocialDestinatario().trim());
        destinatario.setDirDestinatario(request.getDirDestinatario().trim());
        destinatario.setMotivoTraslado(request.getMotivoTraslado().trim());
        destinatario.setDocAduaneroUnico(request.getDocAduaneroUnico());
        destinatario.setCodEstabDestino(request.getCodEstabDestino());
        destinatario.setRuta(request.getRuta());
        destinatario.setCodDocSustento(request.getCodDocSustento());
        destinatario.setNumDocSustento(request.getNumDocSustento());
        destinatario.setNumAutDocSustento(request.getNumAutDocSustento());
        destinatario.setFechaEmisionDocSustento(request.getFechaEmisionDocSustento());
        destinatario.setDetalles(new GuiaRemisionXml.DetallesGuia(listaDetallesGuia));

        GuiaRemisionXml guiaXml = new GuiaRemisionXml();
        guiaXml.setId("comprobante");
        guiaXml.setVersion("1.1.0");
        guiaXml.setInfoTributaria(infoTrib);
        guiaXml.setInfoGuiaRemision(infoGuia);
        guiaXml.setDestinatarios(new GuiaRemisionXml.Destinatarios(List.of(destinatario)));
        guiaXml.setInfoAdicional(construirInfoAdicional(request.getEmailDestinatario(), request.getInfoAdicional(), emisor));

        String xmlSinFirmar = xmlGeneratorService.generarXmlGuiaRemision(guiaXml);
        return procesarFirmaYEnvioSri(
                emisor, codDocGuia, "GUIA_REMISION", claveAcceso, secuencial9, fechaEmision,
                request.getIdentificacionDestinatario(), request.getRazonSocialDestinatario(), request.getNumDocSustento(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, xmlSinFirmar
        );
    }

    // =========================================================================
    // MÉTODOS AUXILIARES COMUNES
    // =========================================================================
    private FacturaResponseDto procesarFirmaYEnvioSri(
            Emisor emisor, String codDoc, String tipoComprobante, String claveAcceso, String secuencial9,
            LocalDate fechaEmision, String idReceptor, String razonSocialReceptor, String numDocModificado,
            BigDecimal totalSinImpuestos, BigDecimal totalDescuento, BigDecimal totalIva, BigDecimal importeTotal,
            String xmlSinFirmar) {

        String xmlFirmado = signerService.firmarXml(xmlSinFirmar, emisor.getRutaFirmaP12(), emisor.getClaveFirmaP12());

        ComprobanteElectronicoEntity entity = new ComprobanteElectronicoEntity();
        entity.setEmisor(emisor);
        entity.setCodDoc(codDoc);
        entity.setTipoComprobante(tipoComprobante);
        entity.setClaveAcceso(claveAcceso);
        entity.setSecuencial(secuencial9);
        entity.setFechaEmision(fechaEmision);
        entity.setIdentificacionReceptor(idReceptor);
        entity.setRazonSocialReceptor(razonSocialReceptor);
        entity.setNumDocModificado(numDocModificado);
        entity.setTotalSinImpuestos(totalSinImpuestos);
        entity.setTotalIva(totalIva);
        entity.setImporteTotal(importeTotal);
        entity.setEstadoSri("FIRMADA");
        entity.setXmlGenerado(xmlSinFirmar);
        entity.setXmlFirmado(xmlFirmado);
        comprobanteRepository.save(entity);

        RespuestaRecepcionSriDto respRecepcion = sriSoapClientService.enviarComprobanteRecepcion(xmlFirmado, emisor.getAmbiente());
        List<MensajeSriDto> todosLosMensajes = new ArrayList<>(respRecepcion.getMensajes());

        String estadoAutorizacionFinal = "DEVUELTA_EN_RECEPCION";
        String fechaAutorizacionStr = null;

        if ("RECIBIDA".equalsIgnoreCase(respRecepcion.getEstado())) {
            entity.setEstadoSri("RECIBIDA");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            RespuestaAutorizacionSriDto respAut = sriSoapClientService.consultarAutorizacionComprobante(claveAcceso, emisor.getAmbiente());
            estadoAutorizacionFinal = respAut.getEstado();
            fechaAutorizacionStr = respAut.getFechaAutorizacion();
            todosLosMensajes.addAll(respAut.getMensajes());

            entity.setEstadoSri(estadoAutorizacionFinal);
            if ("AUTORIZADO".equalsIgnoreCase(estadoAutorizacionFinal)) {
                entity.setFechaAutorizacion(LocalDateTime.now(ZONA_ECUADOR));
                entity.setXmlAutorizado(respAut.getXmlRespuestaRaw());
            }
        } else {
            entity.setEstadoSri(respRecepcion.getEstado());
        }

        entity.setMensajesSri(todosLosMensajes.toString());
        comprobanteRepository.save(entity);

        FacturaResponseDto response = new FacturaResponseDto();
        response.setFacturaId(entity.getId());
        response.setRucEmisor(emisor.getRuc());
        response.setNumeroComprobante(emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision() + "-" + secuencial9);
        response.setClaveAcceso(claveAcceso);
        response.setAmbiente("2".equals(emisor.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS");
        response.setEstadoRecepcion(respRecepcion.getEstado());
        response.setEstadoAutorizacion(estadoAutorizacionFinal);
        response.setFechaAutorizacion(fechaAutorizacionStr);
        response.setTotalSinImpuestos(totalSinImpuestos);
        response.setTotalDescuento(totalDescuento);
        response.setTotalIva(totalIva);
        response.setImporteTotal(importeTotal);
        response.setMensajesSri(todosLosMensajes);
        return response;
    }

    private Emisor obtenerEmisorActivo(String ruc) {
        return emisorRepository.findByRucAndActivoTrue(ruc)
                .orElseThrow(() -> new IllegalArgumentException("No existe un emisor activo con el RUC: " + ruc));
    }

    private String obtenerSiguienteSecuencial(Emisor emisor, String codDoc) {
        SecuencialComprobante sec = secuencialRepository.obtenerParaIncrementar(
                emisor.getId(), emisor.getCodEstablecimiento(), emisor.getPtoEmision(), codDoc
        ).orElseThrow(() -> new IllegalStateException(
                "No se ha definido el secuencial inicial para el tipo de documento '" + codDoc + "' en el punto de emisión "
                + emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision() + ". "
                + "Defínalo primero en PUT /api/v1/emisores/" + emisor.getRuc() + "/secuenciales"
        ));
        long siguiente = sec.getUltimoSecuencial() + 1;
        sec.setUltimoSecuencial(siguiente);
        secuencialRepository.save(sec);
        return ClaveAccesoGenerator.formatearConCeros(String.valueOf(siguiente), 9);
    }

    private FacturaXml.InfoTributaria construirInfoTributaria(Emisor emisor, String codDoc, String claveAcceso, String secuencial9) {
        FacturaXml.InfoTributaria infoTrib = new FacturaXml.InfoTributaria();
        infoTrib.setAmbiente(emisor.getAmbiente());
        infoTrib.setTipoEmision(emisor.getTipoEmision());
        infoTrib.setRazonSocial(emisor.getRazonSocial());
        infoTrib.setNombreComercial(emisor.getNombreComercial());
        infoTrib.setRuc(emisor.getRuc());
        infoTrib.setClaveAcceso(claveAcceso);
        infoTrib.setCodDoc(codDoc);
        infoTrib.setEstab(emisor.getCodEstablecimiento());
        infoTrib.setPtoEmi(emisor.getPtoEmision());
        infoTrib.setSecuencial(secuencial9);
        infoTrib.setDirMatriz(emisor.getDirMatriz());
        infoTrib.setAgenteRetencion(emisor.getAgenteRetencion());
        infoTrib.setContribuyenteRimpe(emisor.getContribuyenteRimpe());
        return infoTrib;
    }

    private FacturaXml.InfoAdicional construirInfoAdicional(String email, Map<String, String> extras, Emisor emisor) {
        List<FacturaXml.CampoAdicional> campos = new ArrayList<>();
        if (email != null && !email.isBlank()) {
            campos.add(new FacturaXml.CampoAdicional("Email", email.trim()));
        }
        if (extras != null) {
            extras.forEach((k, v) -> {
                if (v != null && !v.isBlank()) {
                    campos.add(new FacturaXml.CampoAdicional(k.trim(), v.trim()));
                }
            });
        }
        if (emisor.getRucProveedorSoftware() != null && !emisor.getRucProveedorSoftware().isBlank()) {
            campos.add(new FacturaXml.CampoAdicional("RUC Proveedor", emisor.getRucProveedorSoftware().trim()));
        }
        return campos.isEmpty() ? null : new FacturaXml.InfoAdicional(campos);
    }
}
