package com.facturacion.sri.dto;

import java.math.BigDecimal;
import java.util.List;

public class FacturaResponseDto {

    private Long facturaId;
    private String rucEmisor;
    private String numeroComprobante;
    private String claveAcceso;
    private String ambiente;
    private String estadoRecepcion;
    private String estadoAutorizacion;
    private String fechaAutorizacion;
    private BigDecimal totalSinImpuestos;
    private BigDecimal totalDescuento;
    private BigDecimal totalIva;
    private BigDecimal importeTotal;
    private List<MensajeSriDto> mensajesSri;

    public FacturaResponseDto() {
    }

    public Long getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getEstadoRecepcion() {
        return estadoRecepcion;
    }

    public void setEstadoRecepcion(String estadoRecepcion) {
        this.estadoRecepcion = estadoRecepcion;
    }

    public String getEstadoAutorizacion() {
        return estadoAutorizacion;
    }

    public void setEstadoAutorizacion(String estadoAutorizacion) {
        this.estadoAutorizacion = estadoAutorizacion;
    }

    public String getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(String fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
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

    public List<MensajeSriDto> getMensajesSri() {
        return mensajesSri;
    }

    public void setMensajesSri(List<MensajeSriDto> mensajesSri) {
        this.mensajesSri = mensajesSri;
    }
}
