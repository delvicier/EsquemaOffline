package com.facturacion.sri.dto;

public class MensajeSriDto {

    private String identificador;
    private String mensaje;
    private String informacionAdicional;
    private String tipo;

    public MensajeSriDto() {
    }

    public MensajeSriDto(String identificador, String mensaje, String informacionAdicional, String tipo) {
        this.identificador = identificador;
        this.mensaje = mensaje;
        this.informacionAdicional = informacionAdicional;
        this.tipo = tipo;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getInformacionAdicional() {
        return informacionAdicional;
    }

    public void setInformacionAdicional(String informacionAdicional) {
        this.informacionAdicional = informacionAdicional;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return String.format("[%s - Código %s] %s (%s)",
                tipo, identificador, mensaje, informacionAdicional != null ? informacionAdicional : "Sin detalle adicional");
    }
}
