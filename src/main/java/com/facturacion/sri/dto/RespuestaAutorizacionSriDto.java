package com.facturacion.sri.dto;

import java.util.ArrayList;
import java.util.List;

public class RespuestaAutorizacionSriDto {

    private String claveAccesoConsultada;
    private String estado;
    private String numeroAutorizacion;
    private String fechaAutorizacion;
    private String ambiente;
    private String comprobanteXml;
    private List<MensajeSriDto> mensajes = new ArrayList<>();
    private String xmlRespuestaRaw;

    public RespuestaAutorizacionSriDto() {
    }

    public String getClaveAccesoConsultada() {
        return claveAccesoConsultada;
    }

    public void setClaveAccesoConsultada(String claveAccesoConsultada) {
        this.claveAccesoConsultada = claveAccesoConsultada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public String getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(String fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getComprobanteXml() {
        return comprobanteXml;
    }

    public void setComprobanteXml(String comprobanteXml) {
        this.comprobanteXml = comprobanteXml;
    }

    public List<MensajeSriDto> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<MensajeSriDto> mensajes) {
        this.mensajes = mensajes;
    }

    public String getXmlRespuestaRaw() {
        return xmlRespuestaRaw;
    }

    public void setXmlRespuestaRaw(String xmlRespuestaRaw) {
        this.xmlRespuestaRaw = xmlRespuestaRaw;
    }
}
