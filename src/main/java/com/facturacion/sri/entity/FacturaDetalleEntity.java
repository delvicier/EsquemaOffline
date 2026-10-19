package com.facturacion.sri.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "factura_detalles")
public class FacturaDetalleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "factura_id", nullable = false)
    private FacturaEntity factura;

    @Column(name = "codigo_principal", length = 25, nullable = false)
    private String codigoPrincipal;

    @Column(name = "codigo_auxiliar", length = 25)
    private String codigoAuxiliar;

    @Column(name = "descripcion", length = 300, nullable = false)
    private String descripcion;

    @Column(name = "cantidad", precision = 18, scale = 6, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", precision = 18, scale = 6, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "descuento", precision = 14, scale = 2, nullable = false)
    private BigDecimal descuento;

    @Column(name = "precio_total_sin_impuesto", precision = 14, scale = 2, nullable = false)
    private BigDecimal precioTotalSinImpuesto;

    @Column(name = "codigo_porcentaje_iva", length = 4, nullable = false)
    private String codigoPorcentajeIva;

    @Column(name = "tarifa_iva", precision = 4, scale = 2, nullable = false)
    private BigDecimal tarifaIva;

    @Column(name = "valor_iva", precision = 14, scale = 2, nullable = false)
    private BigDecimal valorIva;

    public FacturaDetalleEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FacturaEntity getFactura() {
        return factura;
    }

    public void setFactura(FacturaEntity factura) {
        this.factura = factura;
    }

    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    public void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
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

    public String getCodigoPorcentajeIva() {
        return codigoPorcentajeIva;
    }

    public void setCodigoPorcentajeIva(String codigoPorcentajeIva) {
        this.codigoPorcentajeIva = codigoPorcentajeIva;
    }

    public BigDecimal getTarifaIva() {
        return tarifaIva;
    }

    public void setTarifaIva(BigDecimal tarifaIva) {
        this.tarifaIva = tarifaIva;
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }
}
