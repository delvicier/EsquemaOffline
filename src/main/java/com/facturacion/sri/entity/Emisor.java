package com.facturacion.sri.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emisores")
public class Emisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ruc", length = 13, nullable = false, unique = true)
    private String ruc;

    @Column(name = "razon_social", length = 300, nullable = false)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 300)
    private String nombreComercial;

    @Column(name = "dir_matriz", length = 300, nullable = false)
    private String dirMatriz;

    @Column(name = "dir_establecimiento", length = 300, nullable = false)
    private String dirEstablecimiento;

    @Column(name = "cod_establecimiento", length = 3, nullable = false)
    private String codEstablecimiento;

    @Column(name = "pto_emision", length = 3, nullable = false)
    private String ptoEmision;

    @Column(name = "contribuyente_especial", length = 13)
    private String contribuyenteEspecial;

    @Column(name = "obligado_contabilidad", length = 2, nullable = false)
    private String obligadoContabilidad;

    @Column(name = "agente_retencion", length = 8)
    private String agenteRetencion;

    @Column(name = "contribuyente_rimpe", length = 45)
    private String contribuyenteRimpe;

    @Column(name = "ambiente", length = 1, nullable = false)
    private String ambiente;

    @Column(name = "tipo_emision", length = 1, nullable = false)
    private String tipoEmision = "1";

    // Almacenamiento binario del certificado .p12 en PostgreSQL
    @Column(name = "archivo_firma_p12", columnDefinition = "BYTEA")
    private byte[] archivoFirmaP12;

    @Column(name = "nombre_archivo_firma", length = 150)
    private String nombreArchivoFirma;

    @Column(name = "clave_firma_p12", nullable = false)
    private String claveFirmaP12;

    @Column(name = "fecha_vigencia_firma")
    private LocalDateTime fechaVigenciaFirma;

    @Column(name = "propietario_firma", length = 400)
    private String propietarioFirma;

    @Column(name = "ruta_firma_p12")
    private String rutaFirmaP12;

    @Column(name = "ruc_proveedor_software", length = 13)
    private String rucProveedorSoftware;

    // Configuración dinámica de Servidor de Correo Saliente (SMTP) por Emisor
    @Column(name = "smtp_host", length = 150)
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username", length = 150)
    private String smtpUsername;

    @Column(name = "smtp_password", length = 250)
    private String smtpPassword;

    @Column(name = "smtp_starttls")
    private Boolean smtpStartTls = true;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Emisor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getDirMatriz() { return dirMatriz; }
    public void setDirMatriz(String dirMatriz) { this.dirMatriz = dirMatriz; }

    public String getDirEstablecimiento() { return dirEstablecimiento; }
    public void setDirEstablecimiento(String dirEstablecimiento) { this.dirEstablecimiento = dirEstablecimiento; }

    public String getCodEstablecimiento() { return codEstablecimiento; }
    public void setCodEstablecimiento(String codEstablecimiento) { this.codEstablecimiento = codEstablecimiento; }

    public String getPtoEmision() { return ptoEmision; }
    public void setPtoEmision(String ptoEmision) { this.ptoEmision = ptoEmision; }

    public String getContribuyenteEspecial() { return contribuyenteEspecial; }
    public void setContribuyenteEspecial(String contribuyenteEspecial) { this.contribuyenteEspecial = contribuyenteEspecial; }

    public String getObligadoContabilidad() { return obligadoContabilidad; }
    public void setObligadoContabilidad(String obligadoContabilidad) { this.obligadoContabilidad = obligadoContabilidad; }

    public String getAgenteRetencion() { return agenteRetencion; }
    public void setAgenteRetencion(String agenteRetencion) { this.agenteRetencion = agenteRetencion; }

    public String getContribuyenteRimpe() { return contribuyenteRimpe; }
    public void setContribuyenteRimpe(String contribuyenteRimpe) { this.contribuyenteRimpe = contribuyenteRimpe; }

    public String getAmbiente() { return ambiente; }
    public void setAmbiente(String ambiente) { this.ambiente = ambiente; }

    public String getTipoEmision() { return tipoEmision; }
    public void setTipoEmision(String tipoEmision) { this.tipoEmision = tipoEmision; }

    public byte[] getArchivoFirmaP12() { return archivoFirmaP12; }
    public void setArchivoFirmaP12(byte[] archivoFirmaP12) { this.archivoFirmaP12 = archivoFirmaP12; }

    public String getNombreArchivoFirma() { return nombreArchivoFirma; }
    public void setNombreArchivoFirma(String nombreArchivoFirma) { this.nombreArchivoFirma = nombreArchivoFirma; }

    public String getClaveFirmaP12() { return claveFirmaP12; }
    public void setClaveFirmaP12(String claveFirmaP12) { this.claveFirmaP12 = claveFirmaP12; }

    public LocalDateTime getFechaVigenciaFirma() { return fechaVigenciaFirma; }
    public void setFechaVigenciaFirma(LocalDateTime fechaVigenciaFirma) { this.fechaVigenciaFirma = fechaVigenciaFirma; }

    public String getPropietarioFirma() { return propietarioFirma; }
    public void setPropietarioFirma(String propietarioFirma) { this.propietarioFirma = propietarioFirma; }

    public String getRutaFirmaP12() { return rutaFirmaP12; }
    public void setRutaFirmaP12(String rutaFirmaP12) { this.rutaFirmaP12 = rutaFirmaP12; }

    public String getRucProveedorSoftware() { return rucProveedorSoftware; }
    public void setRucProveedorSoftware(String rucProveedorSoftware) { this.rucProveedorSoftware = rucProveedorSoftware; }

    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }

    public Integer getSmtpPort() { return smtpPort; }
    public void setSmtpPort(Integer smtpPort) { this.smtpPort = smtpPort; }

    public String getSmtpUsername() { return smtpUsername; }
    public void setSmtpUsername(String smtpUsername) { this.smtpUsername = smtpUsername; }

    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }

    public Boolean getSmtpStartTls() { return smtpStartTls; }
    public void setSmtpStartTls(Boolean smtpStartTls) { this.smtpStartTls = smtpStartTls; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}