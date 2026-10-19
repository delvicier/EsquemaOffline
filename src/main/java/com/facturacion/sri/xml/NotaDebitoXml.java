package com.facturacion.sri.xml;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "notaDebito")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notaDebitoType", propOrder = {
    "infoTributaria",
    "infoNotaDebito",
    "motivos",
    "infoAdicional"
})
public class NotaDebitoXml {

    @XmlAttribute(name = "id", required = true)
    private String id = "comprobante";

    @XmlAttribute(name = "version", required = true)
    private String version = "1.0.0";

    @XmlElement(name = "infoTributaria", required = true)
    private FacturaXml.InfoTributaria infoTributaria;

    @XmlElement(name = "infoNotaDebito", required = true)
    private InfoNotaDebito infoNotaDebito;

    @XmlElement(name = "motivos", required = true)
    private Motivos motivos;

    @XmlElement(name = "infoAdicional")
    private FacturaXml.InfoAdicional infoAdicional;

    public NotaDebitoXml() {
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

    public InfoNotaDebito getInfoNotaDebito() {
        return infoNotaDebito;
    }

    public void setInfoNotaDebito(InfoNotaDebito infoNotaDebito) {
        this.infoNotaDebito = infoNotaDebito;
    }

    public Motivos getMotivos() {
        return motivos;
    }

    public void setMotivos(Motivos motivos) {
        this.motivos = motivos;
    }

    public FacturaXml.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(FacturaXml.InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "infoNotaDebitoType", propOrder = {
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
        "impuestos",
        "valorTotal",
        "pagos"
    })
    public static class InfoNotaDebito {

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
        private String codDocModificado; // "01" Factura
        @XmlElement(required = true)
        private String numDocModificado; // 001-001-000000001
        @XmlElement(required = true)
        private String fechaEmisionDocSustento; // dd/MM/yyyy
        @XmlElement(required = true)
        private BigDecimal totalSinImpuestos;
        @XmlElement(required = true)
        private ImpuestosND impuestos;
        @XmlElement(required = true)
        private BigDecimal valorTotal;
        @XmlElement
        private FacturaXml.Pagos pagos;

        public InfoNotaDebito() {
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

        public ImpuestosND getImpuestos() {
            return impuestos;
        }

        public void setImpuestos(ImpuestosND impuestos) {
            this.impuestos = impuestos;
        }

        public BigDecimal getValorTotal() {
            return valorTotal;
        }

        public void setValorTotal(BigDecimal valorTotal) {
            this.valorTotal = valorTotal;
        }

        public FacturaXml.Pagos getPagos() {
            return pagos;
        }

        public void setPagos(FacturaXml.Pagos pagos) {
            this.pagos = pagos;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "impuestosNDType")
    public static class ImpuestosND {

        @XmlElement(name = "impuesto", required = true)
        private List<FacturaXml.ImpuestoDetalle> impuesto;

        public ImpuestosND() {
        }

        public ImpuestosND(List<FacturaXml.ImpuestoDetalle> impuesto) {
            this.impuesto = impuesto;
        }

        public List<FacturaXml.ImpuestoDetalle> getImpuesto() {
            return impuesto;
        }

        public void setImpuesto(List<FacturaXml.ImpuestoDetalle> impuesto) {
            this.impuesto = impuesto;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "motivosNDType")
    public static class Motivos {

        @XmlElement(name = "motivo", required = true)
        private List<Motivo> motivo;

        public Motivos() {
        }

        public Motivos(List<Motivo> motivo) {
            this.motivo = motivo;
        }

        public List<Motivo> getMotivo() {
            return motivo;
        }

        public void setMotivo(List<Motivo> motivo) {
            this.motivo = motivo;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "motivoNDType", propOrder = {"razon", "valor"})
    public static class Motivo {

        @XmlElement(required = true)
        private String razon;
        @XmlElement(required = true)
        private BigDecimal valor;

        public Motivo() {
        }

        public Motivo(String razon, BigDecimal valor) {
            this.razon = razon;
            this.valor = valor;
        }

        public String getRazon() {
            return razon;
        }

        public void setRazon(String razon) {
            this.razon = razon;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }
}
