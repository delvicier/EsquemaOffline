package com.facturacion.sri.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambiarCredencialesRequestDto {

    @NotBlank(message = "Debe ingresar su contraseña actual")
    private String passwordActual;

    @NotBlank(message = "El nuevo nombre de usuario es obligatorio")
    @Size(min = 4, max = 60, message = "El usuario debe tener entre 4 y 60 caracteres")
    private String nuevoUsername;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String nuevoPassword;

    public CambiarCredencialesRequestDto() {}

    public String getPasswordActual() { return passwordActual; }
    public void setPasswordActual(String passwordActual) { this.passwordActual = passwordActual; }

    public String getNuevoUsername() { return nuevoUsername; }
    public void setNuevoUsername(String nuevoUsername) { this.nuevoUsername = nuevoUsername; }

    public String getNuevoPassword() { return nuevoPassword; }
    public void setNuevoPassword(String nuevoPassword) { this.nuevoPassword = nuevoPassword; }
}