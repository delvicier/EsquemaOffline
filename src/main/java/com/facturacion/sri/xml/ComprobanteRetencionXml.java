package com.facturacion.sri.xml;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "comprobanteRetencion")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comprobanteRetencionType", propOrder = {
    "infoTributaria",
    "infoCompRetencion",
    "docsSustento",
    "infoAdicional"
})
public class ComprobanteRetencionXml {

    @XmlAttribute(name = "id", required = true)
    private String id = "comprobante";

    @XmlAttribute(name = "version", required = true)
    private String version = "2.0.0";

    @XmlElement(name = "infoTributaria", required = true)
    private FacturaXml.InfoTributaria infoTributaria;

    @XmlElement(name = "infoCompRetencion", required = true)
    private InfoCompRetencion infoCompRetencion;

    @XmlElement(name = "docsSustento", required = true)
    private DocsSustento docsSustento;

    @XmlElement(name = "infoAdicional")
    private FacturaXml.InfoAdicional infoAdicional;

    public ComprobanteRetencionXml() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public FacturaXml.InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public void setInfoTributaria(FacturaXml.InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    public InfoCompRetencion getInfoCompRetencion() {
        return infoCompRetencion;
    }

    public void setInfoCompRetencion(InfoCompRetencion infoCompRetencion) {
        this.infoCompRetencion = infoCompRetencion;
    }

    public DocsSustento getDocsSustento() {
        return docsSustento;
    }

    public void setDocsSustento(DocsSustento docsSustento) {
        this.docsSustento = docsSustento;
    }

    public FacturaXml.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(FacturaXml.InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    // =========================================================================
    // 1. NODO <infoCompRetencion>
    // =========================================================================
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "infoCompRetencionType", propOrder = {
        "fechaEmision",
        "dirEstablecimiento",
        "contribuyenteEspecial",
        "obligadoContabilidad",
        "tipoIdentificacionSujetoRetenido",
        "tipoSujetoRetenido",
        "parteRel",
        "razonSocialSujetoRetenido",
        "identificacionSujetoRetenido",
        "periodoFiscal"
    })
    public static class InfoCompRetencion {

        @XmlElement(required = true)
        private String fechaEmision; // dd/MM/yyyy
        @XmlElement
        private String dirEstablecimiento;
        @XmlElement
        private String contribuyenteEspecial;
        @XmlElement
        private String obligadoContabilidad; // "SI" o "NO"
        @XmlElement(required = true)
        private String tipoIdentificacionSujetoRetenido; // "04", "05", "06", "08"
        @XmlElement
        private String tipoSujetoRetenido; // Opcional ("01" Persona Natural, "02" Sociedad; obligatorio si es "08")
        @XmlElement(required = true)
        private String parteRel; // "SI" o "NO"
        @XmlElement(required = true)
        private String razonSocialSujetoRetenido;
        @XmlElement(required = true)
        private String identificacionSujetoRetenido;
        @XmlElement(required = true)
        private String periodoFiscal; // mm/aaaa (ej. "10/2026")

        public InfoCompRetencion() {
        }

        public String getFechaEmision() {
            return fechaEmision;
        }

        public void setFechaEmision(String fechaEmision) {
            this.fechaEmision = fechaEmision;
        }

        public String getDirEstablecimiento() {
            return dirEstablecimiento;
        }

        public void setDirEstablecimiento(String dirEstablecimiento) {
            this.dirEstablecimiento = dirEstablecimiento;
        }

        public String getContribuyenteEspecial() {
            return contribuyenteEspecial;
        }

        public void setContribuyenteEspecial(String contribuyenteEspecial) {
            this.contribuyenteEspecial = contribuyenteEspecial;
        }

        public String getObligadoContabilidad() {
            return obligadoContabilidad;
        }

        public void setObligadoContabilidad(String obligadoContabilidad) {
            this.obligadoContabilidad = obligadoContabilidad;
        }

        public String getTipoIdentificacionSujetoRetenido() {
            return tipoIdentificacionSujetoRetenido;
        }

        public void setTipoIdentificacionSujetoRetenido(String tipoIdentificacionSujetoRetenido) {
            this.tipoIdentificacionSujetoRetenido = tipoIdentificacionSujetoRetenido;
        }

        public String getTipoSujetoRetenido() {
            return tipoSujetoRetenido;
        }

        public void setTipoSujetoRetenido(String tipoSujetoRetenido) {
            this.tipoSujetoRetenido = tipoSujetoRetenido;
        }

        public String getParteRel() {
            return parteRel;
        }

        public void setParteRel(String parteRel) {
            this.parteRel = parteRel;
        }

        public String getRazonSocialSujetoRetenido() {
            return razonSocialSujetoRetenido;
        }

        public void setRazonSocialSujetoRetenido(String razonSocialSujetoRetenido) {
            this.razonSocialSujetoRetenido = razonSocialSujetoRetenido;
        }

        public String getIdentificacionSujetoRetenido() {
            return identificacionSujetoRetenido;
        }

        public void setIdentificacionSujetoRetenido(String identificacionSujetoRetenido) {
            this.identificacionSujetoRetenido = identificacionSujetoRetenido;
        }

        public String getPeriodoFiscal() {
            return periodoFiscal;
        }

        public void setPeriodoFiscal(String periodoFiscal) {
            this.periodoFiscal = periodoFiscal;
        }
    }

    // =========================================================================
    // 2. NODO <docsSustento>
    // =========================================================================
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "docsSustentoRetType")
    public static class DocsSustento {

        @XmlElement(name = "docSustento", required = true)
        private List<DocSustento> docSustento;

        public DocsSustento() {
        }

        public DocsSustento(List<DocSustento> docSustento) {
            this.docSustento = docSustento;
        }

        public List<DocSustento> getDocSustento() {
            return docSustento;
        }

        public void setDocSustento(List<DocSustento> docSustento) {
            this.docSustento = docSustento;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "docSustentoRetType", propOrder = {
        "codSustento",
        "codDocSustento",
        "numDocSustento",
        "fechaEmisionDocSustento",
        "fechaRegistroContable",
        "numAutDocSustento",
        "pagoLocExt",
        "tipoRegi",
        "paisEfecPago",
        "aplicConvDobTrib",
        "pagExtSujRetNorLeg",
        "pagoRegFis",
        "totalSinImpuestos",
        "importeTotal",
        "impuestosDocSustento",
        "retenciones",
        "pagos"
    })
    public static class DocSustento {

        @XmlElement(required = true)
        private String codSustento; // Catálogo ATS Tabla 5 (ej. "01")
        @XmlElement(required = true)
        private String codDocSustento; // Catálogo ATS Tabla 4 (ej. "01" Factura)
        @XmlElement(required = true)
        private String numDocSustento; // 15 dígitos sin guiones: "001001000000001"
        @XmlElement(required = true)
        private String fechaEmisionDocSustento; // dd/MM/yyyy
        @XmlElement
        private String fechaRegistroContable; // dd/MM/yyyy
        @XmlElement
        private String numAutDocSustento; // 10, 37 o 49 dígitos
        @XmlElement(required = true)
        private String pagoLocExt; // "01" Local, "02" Exterior
        @XmlElement
        private String tipoRegi;
        @XmlElement
        private String paisEfecPago;
        @XmlElement
        private String aplicConvDobTrib;
        @XmlElement
        private String pagExtSujRetNorLeg;
        @XmlElement
        private String pagoRegFis;
        @XmlElement(required = true)
        private BigDecimal totalSinImpuestos;
        @XmlElement(required = true)
        private BigDecimal importeTotal;
        @XmlElement(required = true)
        private ImpuestosDocSustento impuestosDocSustento;
        @XmlElement(required = true)
        private Retenciones retenciones;
        @XmlElement(required = true)
        private PagosRetencion pagos;

        public DocSustento() {
        }

        public String getCodSustento() {
            return codSustento;
        }

        public void setCodSustento(String codSustento) {
            this.codSustento = codSustento;
        }

        public String getCodDocSustento() {
            return codDocSustento;
        }

        public void setCodDocSustento(String codDocSustento) {
            this.codDocSustento = codDocSustento;
        }

        public String getNumDocSustento() {
            return numDocSustento;
        }

        public void setNumDocSustento(String numDocSustento) {
            this.numDocSustento = numDocSustento;
        }

        public String getFechaEmisionDocSustento() {
            return fechaEmisionDocSustento;
        }

        public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
            this.fechaEmisionDocSustento = fechaEmisionDocSustento;
        }

        public String getFechaRegistroContable() {
            return fechaRegistroContable;
        }

        public void setFechaRegistroContable(String fechaRegistroContable) {
            this.fechaRegistroContable = fechaRegistroContable;
        }

        public String getNumAutDocSustento() {
            return numAutDocSustento;
        }

        public void setNumAutDocSustento(String numAutDocSustento) {
            this.numAutDocSustento = numAutDocSustento;
        }

        public String getPagoLocExt() {
            return pagoLocExt;
        }

        public void setPagoLocExt(String pagoLocExt) {
            this.pagoLocExt = pagoLocExt;
        }

        public String getTipoRegi() {
            return tipoRegi;
        }

        public void setTipoRegi(String tipoRegi) {
            this.tipoRegi = tipoRegi;
        }

        public String getPaisEfecPago() {
            return paisEfecPago;
        }

        public void setPaisEfecPago(String paisEfecPago) {
            this.paisEfecPago = paisEfecPago;
        }

        public String getAplicConvDobTrib() {
            return aplicConvDobTrib;
        }

        public void setAplicConvDobTrib(String aplicConvDobTrib) {
            this.aplicConvDobTrib = aplicConvDobTrib;
        }

        public String getPagExtSujRetNorLeg() {
            return pagExtSujRetNorLeg;
        }

        public void setPagExtSujRetNorLeg(String pagExtSujRetNorLeg) {
            this.pagExtSujRetNorLeg = pagExtSujRetNorLeg;
        }

        public String getPagoRegFis() {
            return pagoRegFis;
        }

        public void setPagoRegFis(String pagoRegFis) {
            this.pagoRegFis = pagoRegFis;
        }

        public BigDecimal getTotalSinImpuestos() {
            return totalSinImpuestos;
        }

        public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
            this.totalSinImpuestos = totalSinImpuestos;
        }

        public BigDecimal getImporteTotal() {
            return importeTotal;
        }

        public void setImporteTotal(BigDecimal importeTotal) {
            this.importeTotal = importeTotal;
        }

        public ImpuestosDocSustento getImpuestosDocSustento() {
            return impuestosDocSustento;
        }

        public void setImpuestosDocSustento(ImpuestosDocSustento impuestosDocSustento) {
            this.impuestosDocSustento = impuestosDocSustento;
        }

        public Retenciones getRetenciones() {
            return retenciones;
        }

        public void setRetenciones(Retenciones retenciones) {
            this.retenciones = retenciones;
        }

        public PagosRetencion getPagos() {
            return pagos;
        }

        public void setPagos(PagosRetencion pagos) {
            this.pagos = pagos;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "impuestosDocSustentoRetType")
    public static class ImpuestosDocSustento {

        @XmlElement(name = "impuestoDocSustento", required = true)
        private List<ImpuestoDocSustento> impuestoDocSustento;

        public ImpuestosDocSustento() {
        }

        public ImpuestosDocSustento(List<ImpuestoDocSustento> impuestoDocSustento) {
            this.impuestoDocSustento = impuestoDocSustento;
        }

        public List<ImpuestoDocSustento> getImpuestoDocSustento() {
            return impuestoDocSustento;
        }

        public void setImpuestoDocSustento(List<ImpuestoDocSustento> impuestoDocSustento) {
            this.impuestoDocSustento = impuestoDocSustento;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "impuestoDocSustentoItemType", propOrder = {
        "codImpuestoDocSustento",
        "codigoPorcentaje",
        "baseImponible",
        "tarifa",
        "valorImpuesto"
    })
    public static class ImpuestoDocSustento {

        @XmlElement(required = true)
        private String codImpuestoDocSustento; // "2" = IVA
        @XmlElement(required = true)
        private String codigoPorcentaje; // "4" = 15%, "0" = 0%
        @XmlElement(required = true)
        private BigDecimal baseImponible;
        @XmlElement(required = true)
        private BigDecimal tarifa;
        @XmlElement(required = true)
        private BigDecimal valorImpuesto;

        public ImpuestoDocSustento() {
        }

        public ImpuestoDocSustento(String codImpuestoDocSustento, String codigoPorcentaje, BigDecimal baseImponible, BigDecimal tarifa, BigDecimal valorImpuesto) {
            this.codImpuestoDocSustento = codImpuestoDocSustento;
            this.codigoPorcentaje = codigoPorcentaje;
            this.baseImponible = baseImponible;
            this.tarifa = tarifa;
            this.valorImpuesto = valorImpuesto;
        }

        public String getCodImpuestoDocSustento() {
            return codImpuestoDocSustento;
        }

        public void setCodImpuestoDocSustento(String codImpuestoDocSustento) {
            this.codImpuestoDocSustento = codImpuestoDocSustento;
        }

        public String getCodigoPorcentaje() {
            return codigoPorcentaje;
        }

        public void setCodigoPorcentaje(String codigoPorcentaje) {
            this.codigoPorcentaje = codigoPorcentaje;
        }

        public BigDecimal getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(BigDecimal baseImponible) {
            this.baseImponible = baseImponible;
        }

        public BigDecimal getTarifa() {
            return tarifa;
        }

        public void setTarifa(BigDecimal tarifa) {
            this.tarifa = tarifa;
        }

        public BigDecimal getValorImpuesto() {
            return valorImpuesto;
        }

        public void setValorImpuesto(BigDecimal valorImpuesto) {
            this.valorImpuesto = valorImpuesto;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "retencionesRetType")
    public static class Retenciones {

        @XmlElement(name = "retencion", required = true)
        private List<Retencion> retencion;

        public Retenciones() {
        }

        public Retenciones(List<Retencion> retencion) {
            this.retencion = retencion;
        }

        public List<Retencion> getRetencion() {
            return retencion;
        }

        public void setRetencion(List<Retencion> retencion) {
            this.retencion = retencion;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "retencionItemType", propOrder = {
        "codigo",
        "codigoRetencion",
        "baseImponible",
        "porcentajeRetener",
        "valorRetenido"
    })
    public static class Retencion {

        @XmlElement(required = true)
        private String codigo; // Tabla 19: "1"=Renta, "2"=IVA, "6"=ISD
        @XmlElement(required = true)
        private String codigoRetencion; // Tabla 20: ej. "312", "9", "7"
        @XmlElement(required = true)
        private BigDecimal baseImponible;
        @XmlElement(required = true)
        private BigDecimal porcentajeRetener;
        @XmlElement(required = true)
        private BigDecimal valorRetenido;

        public Retencion() {
        }

        public Retencion(String codigo, String codigoRetencion, BigDecimal baseImponible, BigDecimal porcentajeRetener, BigDecimal valorRetenido) {
            this.codigo = codigo;
            this.codigoRetencion = codigoRetencion;
            this.baseImponible = baseImponible;
            this.porcentajeRetener = porcentajeRetener;
            this.valorRetenido = valorRetenido;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigoRetencion() {
            return codigoRetencion;
        }

        public void setCodigoRetencion(String codigoRetencion) {
            this.codigoRetencion = codigoRetencion;
        }

        public BigDecimal getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(BigDecimal baseImponible) {
            this.baseImponible = baseImponible;
        }

        public BigDecimal getPorcentajeRetener() {
            return porcentajeRetener;
        }

        public void setPorcentajeRetener(BigDecimal porcentajeRetener) {
            this.porcentajeRetener = porcentajeRetener;
        }

        public BigDecimal getValorRetenido() {
            return valorRetenido;
        }

        public void setValorRetenido(BigDecimal valorRetenido) {
            this.valorRetenido = valorRetenido;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "pagosRetencionType")
    public static class PagosRetencion {

        @XmlElement(name = "pago", required = true)
        private List<PagoRetencion> pago;

        public PagosRetencion() {
        }

        public PagosRetencion(List<PagoRetencion> pago) {
            this.pago = pago;
        }

        public List<PagoRetencion> getPago() {
            return pago;
        }

        public void setPago(List<PagoRetencion> pago) {
            this.pago = pago;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "pagoRetencionItemType", propOrder = {"formaPago", "total"})
    public static class PagoRetencion {

        @XmlElement(required = true)
        private String formaPago;
        @XmlElement(required = true)
        private BigDecimal total;

        public PagoRetencion() {
        }

        public PagoRetencion(String formaPago, BigDecimal total) {
            this.formaPago = formaPago;
            this.total = total;
        }

        public String getFormaPago() {
            return formaPago;
        }

        public void setFormaPago(String formaPago) {
            this.formaPago = formaPago;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }
    }
}
