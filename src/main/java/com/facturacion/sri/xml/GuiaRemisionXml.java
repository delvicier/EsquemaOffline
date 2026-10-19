package com.facturacion.sri.xml;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "guiaRemision")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "guiaRemisionType", propOrder = {
    "infoTributaria",
    "infoGuiaRemision",
    "destinatarios",
    "infoAdicional"
})
public class GuiaRemisionXml {

    @XmlAttribute(name = "id", required = true)
    private String id = "comprobante";

    @XmlAttribute(name = "version", required = true)
    private String version = "1.1.0";

    @XmlElement(name = "infoTributaria", required = true)
    private FacturaXml.InfoTributaria infoTributaria;

    @XmlElement(name = "infoGuiaRemision", required = true)
    private InfoGuiaRemision infoGuiaRemision;

    @XmlElement(name = "destinatarios", required = true)
    private Destinatarios destinatarios;

    @XmlElement(name = "infoAdicional")
    private FacturaXml.InfoAdicional infoAdicional;

    public GuiaRemisionXml() {
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

    public InfoGuiaRemision getInfoGuiaRemision() {
        return infoGuiaRemision;
    }

    public void setInfoGuiaRemision(InfoGuiaRemision infoGuiaRemision) {
        this.infoGuiaRemision = infoGuiaRemision;
    }

    public Destinatarios getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(Destinatarios destinatarios) {
        this.destinatarios = destinatarios;
    }

    public FacturaXml.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(FacturaXml.InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    // =========================================================================
    // 1. NODO <infoGuiaRemision>
    // =========================================================================
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "infoGuiaRemisionType", propOrder = {
        "dirEstablecimiento",
        "dirPartida",
        "razonSocialTransportista",
        "tipoIdentificacionTransportista",
        "rucTransportista",
        "rise",
        "obligadoContabilidad",
        "contribuyenteEspecial",
        "fechaIniTransporte",
        "fechaFinTransporte",
        "placa"
    })
    public static class InfoGuiaRemision {

        @XmlElement
        private String dirEstablecimiento;
        @XmlElement(required = true)
        private String dirPartida;
        @XmlElement(required = true)
        private String razonSocialTransportista;
        @XmlElement(required = true)
        private String tipoIdentificacionTransportista; // Tabla 6: "04", "05", "06", "08"
        @XmlElement(required = true)
        private String rucTransportista;
        @XmlElement
        private String rise;
        @XmlElement
        private String obligadoContabilidad; // "SI" o "NO"
        @XmlElement
        private String contribuyenteEspecial;
        @XmlElement(required = true)
        private String fechaIniTransporte; // dd/MM/yyyy
        @XmlElement(required = true)
        private String fechaFinTransporte; // dd/MM/yyyy
        @XmlElement(required = true)
        private String placa;

        public InfoGuiaRemision() {
        }

        public String getDirEstablecimiento() {
            return dirEstablecimiento;
        }

        public void setDirEstablecimiento(String dirEstablecimiento) {
            this.dirEstablecimiento = dirEstablecimiento;
        }

        public String getDirPartida() {
            return dirPartida;
        }

        public void setDirPartida(String dirPartida) {
            this.dirPartida = dirPartida;
        }

        public String getRazonSocialTransportista() {
            return razonSocialTransportista;
        }

        public void setRazonSocialTransportista(String razonSocialTransportista) {
            this.razonSocialTransportista = razonSocialTransportista;
        }

        public String getTipoIdentificacionTransportista() {
            return tipoIdentificacionTransportista;
        }

        public void setTipoIdentificacionTransportista(String tipoIdentificacionTransportista) {
            this.tipoIdentificacionTransportista = tipoIdentificacionTransportista;
        }

        public String getRucTransportista() {
            return rucTransportista;
        }

        public void setRucTransportista(String rucTransportista) {
            this.rucTransportista = rucTransportista;
        }

        public String getRise() {
            return rise;
        }

        public void setRise(String rise) {
            this.rise = rise;
        }

        public String getObligadoContabilidad() {
            return obligadoContabilidad;
        }

        public void setObligadoContabilidad(String obligadoContabilidad) {
            this.obligadoContabilidad = obligadoContabilidad;
        }

        public String getContribuyenteEspecial() {
            return contribuyenteEspecial;
        }

        public void setContribuyenteEspecial(String contribuyenteEspecial) {
            this.contribuyenteEspecial = contribuyenteEspecial;
        }

        public String getFechaIniTransporte() {
            return fechaIniTransporte;
        }

        public void setFechaIniTransporte(String fechaIniTransporte) {
            this.fechaIniTransporte = fechaIniTransporte;
        }

        public String getFechaFinTransporte() {
            return fechaFinTransporte;
        }

        public void setFechaFinTransporte(String fechaFinTransporte) {
            this.fechaFinTransporte = fechaFinTransporte;
        }

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(String placa) {
            this.placa = placa;
        }
    }

    // =========================================================================
    // 2. NODO <destinatarios>
    // =========================================================================
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "destinatariosGuiaType")
    public static class Destinatarios {

        @XmlElement(name = "destinatario", required = true)
        private List<Destinatario> destinatario;

        public Destinatarios() {
        }

        public Destinatarios(List<Destinatario> destinatario) {
            this.destinatario = destinatario;
        }

        public List<Destinatario> getDestinatario() {
            return destinatario;
        }

        public void setDestinatario(List<Destinatario> destinatario) {
            this.destinatario = destinatario;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "destinatarioGuiaType", propOrder = {
        "identificacionDestinatario",
        "razonSocialDestinatario",
        "dirDestinatario",
        "motivoTraslado",
        "docAduaneroUnico",
        "codEstabDestino",
        "ruta",
        "codDocSustento",
        "numDocSustento",
        "numAutDocSustento",
        "fechaEmisionDocSustento",
        "detalles"
    })
    public static class Destinatario {

        @XmlElement(required = true)
        private String identificacionDestinatario;
        @XmlElement(required = true)
        private String razonSocialDestinatario;
        @XmlElement(required = true)
        private String dirDestinatario;
        @XmlElement(required = true)
        private String motivoTraslado;
        @XmlElement
        private String docAduaneroUnico;
        @XmlElement
        private String codEstabDestino; // 3 dígitos, ej. "001"
        @XmlElement
        private String ruta;
        @XmlElement
        private String codDocSustento; // Tabla 3: "01" Factura
        @XmlElement
        private String numDocSustento; // Formato 001-001-000000001
        @XmlElement
        private String numAutDocSustento; // 10, 37 o 49 dígitos
        @XmlElement
        private String fechaEmisionDocSustento; // dd/MM/yyyy
        @XmlElement(required = true)
        private DetallesGuia detalles;

        public Destinatario() {
        }

        public String getIdentificacionDestinatario() {
            return identificacionDestinatario;
        }

        public void setIdentificacionDestinatario(String identificacionDestinatario) {
            this.identificacionDestinatario = identificacionDestinatario;
        }

        public String getRazonSocialDestinatario() {
            return razonSocialDestinatario;
        }

        public void setRazonSocialDestinatario(String razonSocialDestinatario) {
            this.razonSocialDestinatario = razonSocialDestinatario;
        }

        public String getDirDestinatario() {
            return dirDestinatario;
        }

        public void setDirDestinatario(String dirDestinatario) {
            this.dirDestinatario = dirDestinatario;
        }

        public String getMotivoTraslado() {
            return motivoTraslado;
        }

        public void setMotivoTraslado(String motivoTraslado) {
            this.motivoTraslado = motivoTraslado;
        }

        public String getDocAduaneroUnico() {
            return docAduaneroUnico;
        }

        public void setDocAduaneroUnico(String docAduaneroUnico) {
            this.docAduaneroUnico = docAduaneroUnico;
        }

        public String getCodEstabDestino() {
            return codEstabDestino;
        }

        public void setCodEstabDestino(String codEstabDestino) {
            this.codEstabDestino = codEstabDestino;
        }

        public String getRuta() {
            return ruta;
        }

        public void setRuta(String ruta) {
            this.ruta = ruta;
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

        public String getNumAutDocSustento() {
            return numAutDocSustento;
        }

        public void setNumAutDocSustento(String numAutDocSustento) {
            this.numAutDocSustento = numAutDocSustento;
        }

        public String getFechaEmisionDocSustento() {
            return fechaEmisionDocSustento;
        }

        public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
            this.fechaEmisionDocSustento = fechaEmisionDocSustento;
        }

        public DetallesGuia getDetalles() {
            return detalles;
        }

        public void setDetalles(DetallesGuia detalles) {
            this.detalles = detalles;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "detallesGuiaType")
    public static class DetallesGuia {

        @XmlElement(name = "detalle", required = true)
        private List<DetalleGuia> detalle;

        public DetallesGuia() {
        }

        public DetallesGuia(List<DetalleGuia> detalle) {
            this.detalle = detalle;
        }

        public List<DetalleGuia> getDetalle() {
            return detalle;
        }

        public void setDetalle(List<DetalleGuia> detalle) {
            this.detalle = detalle;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "detalleGuiaItemType", propOrder = {
        "codigoInterno",
        "codigoAdicional",
        "descripcion",
        "cantidad",
        "detallesAdicionales"
    })
    public static class DetalleGuia {

        @XmlElement
        private String codigoInterno;
        @XmlElement
        private String codigoAdicional;
        @XmlElement(required = true)
        private String descripcion;
        @XmlElement(required = true)
        private BigDecimal cantidad; // Hasta 6 decimales en v1.1.0
        @XmlElement
        private FacturaXml.DetallesAdicionales detallesAdicionales;

        public DetalleGuia() {
        }

        public DetalleGuia(String codigoInterno, String codigoAdicional, String descripcion, BigDecimal cantidad) {
            this.codigoInterno = codigoInterno;
            this.codigoAdicional = codigoAdicional;
            this.descripcion = descripcion;
            this.cantidad = cantidad;
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

        public FacturaXml.DetallesAdicionales getDetallesAdicionales() {
            return detallesAdicionales;
        }

        public void setDetallesAdicionales(FacturaXml.DetallesAdicionales detallesAdicionales) {
            this.detallesAdicionales = detallesAdicionales;
        }
    }
}
