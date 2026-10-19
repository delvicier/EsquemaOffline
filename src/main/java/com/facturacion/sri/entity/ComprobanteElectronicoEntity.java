package com.facturacion.sri.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobantes_electronicos")
public class ComprobanteElectronicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Emisor emisor;

    @Column(name = "cod_doc", length = 2, nullable = false)
    private String codDoc; // "03" Liquidación, "04" Nota Crédito, "05" Nota Débito

    @Column(name = "tipo_comprobante", length = 30, nullable = false)
    private String tipoComprobante; // LIQUIDACION_COMPRA, NOTA_CREDITO, NOTA_DEBITO

    @Column(name = "clave_acceso", length = 49, nullable = false, unique = true)
    private String claveAcceso;

    @Column(name = "secuencial", length = 9, nullable = false)
    private String secuencial;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "identificacion_receptor", length = 20, nullable = false)
    private String identificacionReceptor;

    @Column(name = "razon_social_receptor", length = 300, nullable = false)
    private String razonSocialReceptor;

    @Column(name = "num_doc_modificado", length = 17)
    private String numDocModificado; // Ej: "001-001-000000001" (Para NC y ND)

    @Column(name = "total_sin_impuestos", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalSinImpuestos;

    @Column(name = "total_iva", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalIva;

    @Column(name = "importe_total", precision = 14, scale = 2, nullable = false)
    private BigDecimal importeTotal;

    @Column(name = "estado_sri", length = 30, nullable = false)
    private String estadoSri;

    @Column(name = "fecha_autorizacion")
    private LocalDateTime fechaAutorizacion;

    @Column(name = "mensajes_sri", columnDefinition = "TEXT")
    private String mensajesSri;

    @Column(name = "xml_generado", columnDefinition = "TEXT")
    private String xmlGenerado;

    @Column(name = "xml_firmado", columnDefinition = "TEXT")
    private String xmlFirmado;

    @Column(name = "xml_autorizado", columnDefinition = "TEXT")
    private String xmlAutorizado;

    public ComprobanteElectronicoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emisor getEmisor() {
        return emisor;
    }

    public void setEmisor(Emisor emisor) {
        this.emisor = emisor;
    }

    public String getCodDoc() {
        return codDoc;
    }

    public void setCodDoc(String codDoc) {
        this.codDoc = codDoc;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(String secuencial) {
        this.secuencial = secuencial;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getIdentificacionReceptor() {
        return identificacionReceptor;
    }

    public void setIdentificacionReceptor(String identificacionReceptor) {
        this.identificacionReceptor = identificacionReceptor;
    }

    public String getRazonSocialReceptor() {
        return razonSocialReceptor;
    }

    public void setRazonSocialReceptor(String razonSocialReceptor) {
        this.razonSocialReceptor = razonSocialReceptor;
    }

    public String getNumDocModificado() {
        return numDocModificado;
    }

    public void setNumDocModificado(String numDocModificado) {
        this.numDocModificado = numDocModificado;
    }

    public BigDecimal getTotalSinImpuestos() {
        return totalSinImpuestos;
    }

    public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
        this.totalSinImpuestos = totalSinImpuestos;
    }

    public BigDecimal getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(BigDecimal totalIva) {
        this.totalIva = totalIva;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getEstadoSri() {
        return estadoSri;
    }

    public void setEstadoSri(String estadoSri) {
        this.estadoSri = estadoSri;
    }

    public LocalDateTime getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(LocalDateTime fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public String getMensajesSri() {
        return mensajesSri;
    }

    public void setMensajesSri(String mensajesSri) {
        this.mensajesSri = mensajesSri;
    }

    public String getXmlGenerado() {
        return xmlGenerado;
    }

    public void setXmlGenerado(String xmlGenerado) {
        this.xmlGenerado = xmlGenerado;
    }

    public String getXmlFirmado() {
        return xmlFirmado;
    }

    public void setXmlFirmado(String xmlFirmado) {
        this.xmlFirmado = xmlFirmado;
    }

    public String getXmlAutorizado() {
        return xmlAutorizado;
    }

    public void setXmlAutorizado(String xmlAutorizado) {
        this.xmlAutorizado = xmlAutorizado;
    }
}
