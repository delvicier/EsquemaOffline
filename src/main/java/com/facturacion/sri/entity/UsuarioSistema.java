package com.facturacion.sri.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_sistema")
public class UsuarioSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 60, nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "rol", length = 30, nullable = false)
    private String rol = "ROLE_ADMIN";

    @Column(name = "requiere_cambio_clave", nullable = false)
    private Boolean requiereCambioClave = true;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public UsuarioSistema() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getRequiereCambioClave() { return requiereCambioClave; }
    public void setRequiereCambioClave(Boolean requiereCambioClave) { this.requiereCambioClave = requiereCambioClave; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}