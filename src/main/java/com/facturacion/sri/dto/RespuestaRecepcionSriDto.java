package com.facturacion.sri.dto;

import java.util.ArrayList;
import java.util.List;

public class RespuestaRecepcionSriDto {

    private String estado;
    private String claveAcceso;
    private List<MensajeSriDto> mensajes = new ArrayList<>();
    private String xmlRespuestaRaw;

    public RespuestaRecepcionSriDto() {
    }

    public RespuestaRecepcionSriDto(String estado, String claveAcceso, List<MensajeSriDto> mensajes, String xmlRespuestaRaw) {
        this.estado = estado;
        this.claveAcceso = claveAcceso;
        this.mensajes = mensajes != null ? mensajes : new ArrayList<>();
        this.xmlRespuestaRaw = xmlRespuestaRaw;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
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
