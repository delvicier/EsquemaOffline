package com.facturacion.sri.xml;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "notaCredito")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "infoTributaria",
    "infoNotaCredito",
    "detalles",
    "infoAdicional"
})
public class NotaCreditoXml {

    @XmlAttribute(name = "id", required = true)
    private String id = "comprobante";

    @XmlAttribute(name = "version", required = true)
    private String version = "1.1.0";

    @XmlElement(name = "infoTributaria", required = true)
    private FacturaXml.InfoTributaria infoTributaria;

    @XmlElement(name = "infoNotaCredito", required = true)
    private InfoNotaCredito infoNotaCredito;

    @XmlElement(name = "detalles", required = true)
    private Detalles detalles;

    @XmlElement(name = "infoAdicional")
    private FacturaXml.InfoAdicional infoAdicional;

    public NotaCreditoXml() {
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

    public InfoNotaCredito getInfoNotaCredito() {
        return infoNotaCredito;
    }

    public void setInfoNotaCredito(InfoNotaCredito infoNotaCredito) {
        this.infoNotaCredito = infoNotaCredito;
    }

    public Detalles getDetalles() {
        return detalles;
    }

    public void setDetalles(Detalles detalles) {
        this.detalles = detalles;
    }

    public FacturaXml.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(FacturaXml.InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "fechaEmision",
        "dirEstablecimiento",
        "tipoIdentificacionComprador",
        "razonSocialComprador",
        "identificacionComprador",
        "contribuyenteEspecial",
        "obligadoContabilidad",
        "rise",
        "codDocModificado",
        "numDocModificado",
        "fechaEmisionDocSustento",
        "totalSinImpuestos",
        "valorModificacion",
        "moneda",
        "totalConImpuestos",
        "motivo"
    })
    public static class InfoNotaCredito {

        @XmlElement(required = true)
        private String fechaEmision;
        @XmlElement
        private String dirEstablecimiento;
        @XmlElement(required = true)
        private String tipoIdentificacionComprador;
        @XmlElement(required = true)
        private String razonSocialComprador;
        @XmlElement(required = true)
        private String identificacionComprador;
        @XmlElement
        private String contribuyenteEspecial;
        @XmlElement
        private String obligadoContabilidad;
        @XmlElement
        private String rise;
        @XmlElement(required = true)
        private String codDocModificado; // "01" para Factura
        @XmlElement(required = true)
        private String numDocModificado; // Ej: "001-001-000000001"
        @XmlElement(required = true)
        private String fechaEmisionDocSustento; // dd/MM/yyyy
        @XmlElement(required = true)
        private BigDecimal totalSinImpuestos;
        @XmlElement(required = true)
        private BigDecimal valorModificacion;
        @XmlElement
        private String moneda;
        @XmlElement(required = true)
        private TotalConImpuestosNC totalConImpuestos;
        @XmlElement(required = true)
        private String motivo;

        public InfoNotaCredito() {
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

        public String getTipoIdentificacionComprador() {
            return tipoIdentificacionComprador;
        }

        public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
            this.tipoIdentificacionComprador = tipoIdentificacionComprador;
        }

        public String getRazonSocialComprador() {
            return razonSocialComprador;
        }

        public void setRazonSocialComprador(String razonSocialComprador) {
            this.razonSocialComprador = razonSocialComprador;
        }

        public String getIdentificacionComprador() {
            return identificacionComprador;
        }

        public void setIdentificacionComprador(String identificacionComprador) {
            this.identificacionComprador = identificacionComprador;
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

        public String getRise() {
            return rise;
        }

        public void setRise(String rise) {
            this.rise = rise;
        }

        public String getCodDocModificado() {
            return codDocModificado;
        }

        public void setCodDocModificado(String codDocModificado) {
            this.codDocModificado = codDocModificado;
        }

        public String getNumDocModificado() {
            return numDocModificado;
        }

        public void setNumDocModificado(String numDocModificado) {
            this.numDocModificado = numDocModificado;
        }

        public String getFechaEmisionDocSustento() {
            return fechaEmisionDocSustento;
        }

        public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
            this.fechaEmisionDocSustento = fechaEmisionDocSustento;
        }

        public BigDecimal getTotalSinImpuestos() {
            return totalSinImpuestos;
        }

        public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
            this.totalSinImpuestos = totalSinImpuestos;
        }

        public BigDecimal getValorModificacion() {
            return valorModificacion;
        }

        public void setValorModificacion(BigDecimal valorModificacion) {
            this.valorModificacion = valorModificacion;
        }

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
        }

        public TotalConImpuestosNC getTotalConImpuestos() {
            return totalConImpuestos;
        }

        public void setTotalConImpuestos(TotalConImpuestosNC totalConImpuestos) {
            this.totalConImpuestos = totalConImpuestos;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalConImpuestosNC {

        @XmlElement(name = "totalImpuesto", required = true)
        private List<TotalImpuestoNC> totalImpuesto;

        public TotalConImpuestosNC() {
        }

        public TotalConImpuestosNC(List<TotalImpuestoNC> totalImpuesto) {
            this.totalImpuesto = totalImpuesto;
        }

        public List<TotalImpuestoNC> getTotalImpuesto() {
            return totalImpuesto;
        }

        public void setTotalImpuesto(List<TotalImpuestoNC> totalImpuesto) {
            this.totalImpuesto = totalImpuesto;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"codigo", "codigoPorcentaje", "baseImponible", "valor", "valorDevolucionIva"})
    public static class TotalImpuestoNC {

        @XmlElement(required = true)
        private String codigo;
        @XmlElement(required = true)
        private String codigoPorcentaje;
        @XmlElement(required = true)
        private BigDecimal baseImponible;
        @XmlElement(required = true)
        private BigDecimal valor;
        @XmlElement
        private BigDecimal valorDevolucionIva;

        public TotalImpuestoNC() {
        }

        public TotalImpuestoNC(String codigo, String codigoPorcentaje, BigDecimal baseImponible, BigDecimal valor) {
            this.codigo = codigo;
            this.codigoPorcentaje = codigoPorcentaje;
            this.baseImponible = baseImponible;
            this.valor = valor;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
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

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public BigDecimal getValorDevolucionIva() {
            return valorDevolucionIva;
        }

        public void setValorDevolucionIva(BigDecimal valorDevolucionIva) {
            this.valorDevolucionIva = valorDevolucionIva;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Detalles {

        @XmlElement(name = "detalle", required = true)
        private List<DetalleNC> detalle;

        public Detalles() {
        }

        public Detalles(List<DetalleNC> detalle) {
            this.detalle = detalle;
        }

        public List<DetalleNC> getDetalle() {
            return detalle;
        }

        public void setDetalle(List<DetalleNC> detalle) {
            this.detalle = detalle;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "codigoInterno",
        "codigoAdicional",
        "descripcion",
        "cantidad",
        "precioUnitario",
        "descuento",
        "precioTotalSinImpuesto",
        "detallesAdicionales",
        "impuestos"
    })
    public static class DetalleNC {

        @XmlElement
        private String codigoInterno;
        @XmlElement
        private String codigoAdicional;
        @XmlElement(required = true)
        private String descripcion;
        @XmlElement(required = true)
        private BigDecimal cantidad;
        @XmlElement(required = true)
        private BigDecimal precioUnitario;
        @XmlElement
        private BigDecimal descuento;
        @XmlElement(required = true)
        private BigDecimal precioTotalSinImpuesto;
        @XmlElement
        private FacturaXml.DetallesAdicionales detallesAdicionales;
        @XmlElement(required = true)
        private FacturaXml.ImpuestosDetalle impuestos;

        public DetalleNC() {
        }

        public String getCodigoInterno() {
            return codigoInterno;
        }

        public void setCodigoInterno(String codigoInterno) {
            this.codigoInterno = codigoInterno;
        }

        public String getCodigoAdicional() {
            return codigoAdicional;
        }

        public void setCodigoAdicional(String codigoAdicional) {
            this.codigoAdicional = codigoAdicional;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public BigDecimal getCantidad() {
            return cantidad;
        }

        public void setCantidad(BigDecimal cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getDescuento() {
            return descuento;
        }

        public void setDescuento(BigDecimal descuento) {
            this.descuento = descuento;
        }

        public BigDecimal getPrecioTotalSinImpuesto() {
            return precioTotalSinImpuesto;
        }

        public void setPrecioTotalSinImpuesto(BigDecimal precioTotalSinImpuesto) {
            this.precioTotalSinImpuesto = precioTotalSinImpuesto;
        }

        public FacturaXml.DetallesAdicionales getDetallesAdicionales() {
            return detallesAdicionales;
        }

        public void setDetallesAdicionales(FacturaXml.DetallesAdicionales detallesAdicionales) {
            this.detallesAdicionales = detallesAdicionales;
        }

        public FacturaXml.ImpuestosDetalle getImpuestos() {
            return impuestos;
        }

        public void setImpuestos(FacturaXml.ImpuestosDetalle impuestos) {
            this.impuestos = impuestos;
        }
    }
}
