package com.facturacion.sri.dto;

import jakarta.validation.constraints.Min;

public class ConfigurarSecuencialesRequestDto {

    @Min(value = 0, message = "El secuencial no puede ser negativo (use 0 si nunca ha emitido)")
    private Long ultimoSecuencialFactura;           // codDoc = "01"

    @Min(value = 0, message = "El secuencial no puede ser negativo")
    private Long ultimoSecuencialLiquidacionCompra; // codDoc = "03"

    @Min(value = 0, message = "El secuencial no puede ser negativo")
    private Long ultimoSecuencialNotaCredito;       // codDoc = "04"

    @Min(value = 0, message = "El secuencial no puede ser negativo")
    private Long ultimoSecuencialNotaDebito;        // codDoc = "05"

    @Min(value = 0, message = "El secuencial no puede ser negativo")
    private Long ultimoSecuencialGuiaRemision;      // codDoc = "06"

    @Min(value = 0, message = "El secuencial no puede ser negativo")
    private Long ultimoSecuencialRetencion;         // codDoc = "07"

    public ConfigurarSecuencialesRequestDto() {
    }

    public Long getUltimoSecuencialFactura() {
        return ultimoSecuencialFactura;
    }

    public void setUltimoSecuencialFactura(Long ultimoSecuencialFactura) {
        this.ultimoSecuencialFactura = ultimoSecuencialFactura;
    }

    public Long getUltimoSecuencialLiquidacionCompra() {
        return ultimoSecuencialLiquidacionCompra;
    }

    public void setUltimoSecuencialLiquidacionCompra(Long ultimoSecuencialLiquidacionCompra) {
        this.ultimoSecuencialLiquidacionCompra = ultimoSecuencialLiquidacionCompra;
    }

    public Long getUltimoSecuencialNotaCredito() {
        return ultimoSecuencialNotaCredito;
    }

    public void setUltimoSecuencialNotaCredito(Long ultimoSecuencialNotaCredito) {
        this.ultimoSecuencialNotaCredito = ultimoSecuencialNotaCredito;
    }

    public Long getUltimoSecuencialNotaDebito() {
        return ultimoSecuencialNotaDebito;
    }

    public void setUltimoSecuencialNotaDebito(Long ultimoSecuencialNotaDebito) {
        this.ultimoSecuencialNotaDebito = ultimoSecuencialNotaDebito;
    }

    public Long getUltimoSecuencialGuiaRemision() {
        return ultimoSecuencialGuiaRemision;
    }

    public void setUltimoSecuencialGuiaRemision(Long ultimoSecuencialGuiaRemision) {
        this.ultimoSecuencialGuiaRemision = ultimoSecuencialGuiaRemision;
    }

    public Long getUltimoSecuencialRetencion() {
        return ultimoSecuencialRetencion;
    }

    public void setUltimoSecuencialRetencion(Long ultimoSecuencialRetencion) {
        this.ultimoSecuencialRetencion = ultimoSecuencialRetencion;
    }
}
