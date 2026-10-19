package com.facturacion.sri.xml;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;

@XmlRootElement(name = "liquidacionCompra")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "liquidacionCompraType", propOrder = {
    "infoTributaria",
    "infoLiquidacionCompra",
    "detalles",
    "infoAdicional"
})
public class LiquidacionCompraXml {

    @XmlAttribute(name = "id", required = true)
    private String id = "comprobante";

    @XmlAttribute(name = "version", required = true)
    private String version = "1.1.0";

    @XmlElement(name = "infoTributaria", required = true)
    private FacturaXml.InfoTributaria infoTributaria;

    @XmlElement(name = "infoLiquidacionCompra", required = true)
    private InfoLiquidacionCompra infoLiquidacionCompra;

    @XmlElement(name = "detalles", required = true)
    private FacturaXml.Detalles detalles;

    @XmlElement(name = "infoAdicional")
    private FacturaXml.InfoAdicional infoAdicional;

    public LiquidacionCompraXml() {
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

    public InfoLiquidacionCompra getInfoLiquidacionCompra() {
        return infoLiquidacionCompra;
    }

    public void setInfoLiquidacionCompra(InfoLiquidacionCompra infoLiquidacionCompra) {
        this.infoLiquidacionCompra = infoLiquidacionCompra;
    }

    public FacturaXml.Detalles getDetalles() {
        return detalles;
    }

    public void setDetalles(FacturaXml.Detalles detalles) {
        this.detalles = detalles;
    }

    public FacturaXml.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(FacturaXml.InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "infoLiquidacionCompraType", propOrder = {
        "fechaEmision",
        "dirEstablecimiento",
        "contribuyenteEspecial",
        "obligadoContabilidad",
        "tipoIdentificacionProveedor",
        "razonSocialProveedor",
        "identificacionProveedor",
        "direccionProveedor",
        "totalSinImpuestos",
        "totalDescuento",
        "totalConImpuestos",
        "importeTotal",
        "moneda",
        "pagos"
    })
    public static class InfoLiquidacionCompra {

        @XmlElement(required = true)
        private String fechaEmision;
        @XmlElement
        private String dirEstablecimiento;
        @XmlElement
        private String contribuyenteEspecial;
        @XmlElement
        private String obligadoContabilidad;
        @XmlElement(required = true)
        private String tipoIdentificacionProveedor; // "04", "05", "06", "08" (No admite "07")
        @XmlElement(required = true)
        private String razonSocialProveedor;
        @XmlElement(required = true)
        private String identificacionProveedor;
        @XmlElement
        private String direccionProveedor;
        @XmlElement(required = true)
        private BigDecimal totalSinImpuestos;
        @XmlElement(required = true)
        private BigDecimal totalDescuento;
        @XmlElement(required = true)
        private FacturaXml.TotalConImpuestos totalConImpuestos;
        @XmlElement(required = true)
        private BigDecimal importeTotal;
        @XmlElement
        private String moneda;
        @XmlElement
        private FacturaXml.Pagos pagos;

        public InfoLiquidacionCompra() {
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

        public String getTipoIdentificacionProveedor() {
            return tipoIdentificacionProveedor;
        }

        public void setTipoIdentificacionProveedor(String tipoIdentificacionProveedor) {
            this.tipoIdentificacionProveedor = tipoIdentificacionProveedor;
        }

        public String getRazonSocialProveedor() {
            return razonSocialProveedor;
        }

        public void setRazonSocialProveedor(String razonSocialProveedor) {
            this.razonSocialProveedor = razonSocialProveedor;
        }

        public String getIdentificacionProveedor() {
            return identificacionProveedor;
        }

        public void setIdentificacionProveedor(String identificacionProveedor) {
            this.identificacionProveedor = identificacionProveedor;
        }

        public String getDireccionProveedor() {
            return direccionProveedor;
        }

        public void setDireccionProveedor(String direccionProveedor) {
            this.direccionProveedor = direccionProveedor;
        }

        public BigDecimal getTotalSinImpuestos() {
            return totalSinImpuestos;
        }

        public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
            this.totalSinImpuestos = totalSinImpuestos;
        }

        public BigDecimal getTotalDescuento() {
            return totalDescuento;
        }

        public void setTotalDescuento(BigDecimal totalDescuento) {
            this.totalDescuento = totalDescuento;
        }

        public FacturaXml.TotalConImpuestos getTotalConImpuestos() {
            return totalConImpuestos;
        }

        public void setTotalConImpuestos(FacturaXml.TotalConImpuestos totalConImpuestos) {
            this.totalConImpuestos = totalConImpuestos;
        }

        public BigDecimal getImporteTotal() {
            return importeTotal;
        }

        public void setImporteTotal(BigDecimal importeTotal) {
            this.importeTotal = importeTotal;
        }

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
        }

        public FacturaXml.Pagos getPagos() {
            return pagos;
        }

        public void setPagos(FacturaXml.Pagos pagos) {
            this.pagos = pagos;
        }
    }
}
