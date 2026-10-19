package com.facturacion.sri.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Emisor emisor;

    @Column(name = "clave_acceso", length = 49, nullable = false, unique = true)
    private String claveAcceso;

    @Column(name = "secuencial", length = 9, nullable = false)
    private String secuencial;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "tipo_identificacion_comprador", length = 2, nullable = false)
    private String tipoIdentificacionComprador;

    @Column(name = "identificacion_comprador", length = 20, nullable = false)
    private String identificacionComprador;

    @Column(name = "razon_social_comprador", length = 300, nullable = false)
    private String razonSocialComprador;

    @Column(name = "direccion_comprador", length = 300)
    private String direccionComprador;

    @Column(name = "email_comprador", length = 150)
    private String emailComprador;

    @Column(name = "total_sin_impuestos", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalSinImpuestos;

    @Column(name = "total_descuento", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalDescuento;

    @Column(name = "total_iva", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalIva;

    @Column(name = "importe_total", precision = 14, scale = 2, nullable = false)
    private BigDecimal importeTotal;

    @Column(name = "forma_pago", length = 2, nullable = false)
    private String formaPago;

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

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaDetalleEntity> detalles = new ArrayList<>();

    public FacturaEntity() {
    }

    public void agregarDetalle(FacturaDetalleEntity detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
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

    public String getTipoIdentificacionComprador() {
        return tipoIdentificacionComprador;
    }

    public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
        this.tipoIdentificacionComprador = tipoIdentificacionComprador;
    }

    public String getIdentificacionComprador() {
        return identificacionComprador;
    }

    public void setIdentificacionComprador(String identificacionComprador) {
        this.identificacionComprador = identificacionComprador;
    }

    public String getRazonSocialComprador() {
        return razonSocialComprador;
    }

    public void setRazonSocialComprador(String razonSocialComprador) {
        this.razonSocialComprador = razonSocialComprador;
    }

    public String getDireccionComprador() {
        return direccionComprador;
    }

    public void setDireccionComprador(String direccionComprador) {
        this.direccionComprador = direccionComprador;
    }

    public String getEmailComprador() {
        return emailComprador;
    }

    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
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

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
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

    public List<FacturaDetalleEntity> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<FacturaDetalleEntity> detalles) {
        this.detalles = detalles;
    }
}
