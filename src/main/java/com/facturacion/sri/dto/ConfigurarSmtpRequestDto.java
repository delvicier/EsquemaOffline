package com.facturacion.sri.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfigurarSmtpRequestDto {

    @NotBlank(message = "El servidor SMTP (mailHost) es obligatorio, ej: smtp.gmail.com")
    private String mailHost;

    @NotNull(message = "El puerto SMTP (mailPort) es obligatorio, ej: 587 o 465")
    @Min(value = 1, message = "Puerto inválido")
    @Max(value = 65535, message = "Puerto inválido")
    private Integer mailPort;

    @NotBlank(message = "El usuario o correo SMTP (mailUser) es obligatorio")
    @Email(message = "El usuario SMTP debe tener formato de correo electrónico")
    private String mailUser;

    @NotBlank(message = "La contraseña SMTP (mailPass) es obligatoria")
    private String mailPass;

    private Boolean usarStartTls = true;

    private Boolean probarConexion = true;

    public ConfigurarSmtpRequestDto() {}

    public String getMailHost() { return mailHost; }
    public void setMailHost(String mailHost) { this.mailHost = mailHost; }

    public Integer getMailPort() { return mailPort; }
    public void setMailPort(Integer mailPort) { this.mailPort = mailPort; }

    public String getMailUser() { return mailUser; }
    public void setMailUser(String mailUser) { this.mailUser = mailUser; }

    public String getMailPass() { return mailPass; }
    public void setMailPass(String mailPass) { this.mailPass = mailPass; }

    public Boolean getUsarStartTls() { return usarStartTls; }
    public void setUsarStartTls(Boolean usarStartTls) { this.usarStartTls = usarStartTls; }

    public Boolean getProbarConexion() { return probarConexion; }
    public void setProbarConexion(Boolean probarConexion) { this.probarConexion = probarConexion; }
}