package com.facturacion.sri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class EmitirGuiaRemisionRequestDto {

    @NotBlank(message = "El RUC del emisor es obligatorio")
    @Pattern(regexp = "\\d{10}001")
    private String rucEmisor;

    @NotBlank(message = "La dirección del punto de partida es obligatoria")
    @Size(max = 300)
    private String dirPartida;

    @NotBlank(message = "La razón social o nombres del transportista son obligatorios")
    @Size(max = 300)
    private String razonSocialTransportista;

    @NotBlank(message = "El tipo de identificación del transportista es obligatorio (04, 05, 06, 08)")
    @Pattern(regexp = "0[4568]")
    private String tipoIdentificacionTransportista;

    @NotBlank(message = "El RUC o cédula del transportista es obligatorio")
    @Size(max = 20)
    private String rucTransportista;

    @NotBlank(message = "La fecha de inicio de transporte es obligatoria (dd/MM/yyyy)")
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/20[0-9][0-9]")
    private String fechaIniTransporte;

    @NotBlank(message = "La fecha de fin de transporte es obligatoria (dd/MM/yyyy)")
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/20[0-9][0-9]")
    private String fechaFinTransporte;

    @NotBlank(message = "La placa del vehículo es obligatoria")
    @Size(max = 20)
    private String placa;

    // Datos del Destinatario
    @NotBlank(message = "La identificación del destinatario es obligatoria")
    @Size(max = 20)
    private String identificacionDestinatario;

    @NotBlank(message = "La razón social del destinatario es obligatoria")
    @Size(max = 300)
    private String razonSocialDestinatario;

    @NotBlank(message = "La dirección del destinatario es obligatoria")
    @Size(max = 300)
    private String dirDestinatario;

    @NotBlank(message = "El motivo del traslado es obligatorio")
    @Size(max = 300)
    private String motivoTraslado;

    private String docAduaneroUnico;
    private String codEstabDestino; // Ej: "001"
    private String ruta;            // Ej: "Quito - Cuenca"
    private String codDocSustento;           // Ej: "01"
    private String numDocSustento;           // Ej: "001-001-000000001"
    private String numAutDocSustento;        // Clave de acceso de 49 dígitos
    private String fechaEmisionDocSustento;  // dd/MM/yyyy

    @Email
    private String emailDestinatario;

    @NotEmpty(message = "Debe incluir al menos un ítem a transportar en la Guía de Remisión")
    @Valid
    private List<ItemGuiaDto> detalles;

    private Map<String, String> infoAdicional;

    public EmitirGuiaRemisionRequestDto() {
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getDirPartida() {
        return dirPartida;
    }

    public void setDirPartida(String dirPartida) {
        this.dirPartida = dirPartida;
    }

    public String getRazonSocialTransportista() {
        return razonSocialTransportista;
    }

    public void setRazonSocialTransportista(String razonSocialTransportista) {
        this.razonSocialTransportista = razonSocialTransportista;
    }

    public String getTipoIdentificacionTransportista() {
        return tipoIdentificacionTransportista;
    }

    public void setTipoIdentificacionTransportista(String tipoIdentificacionTransportista) {
        this.tipoIdentificacionTransportista = tipoIdentificacionTransportista;
    }

    public String getRucTransportista() {
        return rucTransportista;
    }

    public void setRucTransportista(String rucTransportista) {
        this.rucTransportista = rucTransportista;
    }

    public String getFechaIniTransporte() {
        return fechaIniTransporte;
    }

    public void setFechaIniTransporte(String fechaIniTransporte) {
        this.fechaIniTransporte = fechaIniTransporte;
    }

    public String getFechaFinTransporte() {
        return fechaFinTransporte;
    }

    public void setFechaFinTransporte(String fechaFinTransporte) {
        this.fechaFinTransporte = fechaFinTransporte;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getIdentificacionDestinatario() {
        return identificacionDestinatario;
    }

    public void setIdentificacionDestinatario(String identificacionDestinatario) {
        this.identificacionDestinatario = identificacionDestinatario;
    }

    public String getRazonSocialDestinatario() {
        return razonSocialDestinatario;
    }

    public void setRazonSocialDestinatario(String razonSocialDestinatario) {
        this.razonSocialDestinatario = razonSocialDestinatario;
    }

    public String getDirDestinatario() {
        return dirDestinatario;
    }

    public void setDirDestinatario(String dirDestinatario) {
        this.dirDestinatario = dirDestinatario;
    }

    public String getMotivoTraslado() {
        return motivoTraslado;
    }

    public void setMotivoTraslado(String motivoTraslado) {
        this.motivoTraslado = motivoTraslado;
    }

    public String getDocAduaneroUnico() {
        return docAduaneroUnico;
    }

    public void setDocAduaneroUnico(String docAduaneroUnico) {
        this.docAduaneroUnico = docAduaneroUnico;
    }

    public String getCodEstabDestino() {
        return codEstabDestino;
    }

    public void setCodEstabDestino(String codEstabDestino) {
        this.codEstabDestino = codEstabDestino;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getCodDocSustento() {
        return codDocSustento;
    }

    public void setCodDocSustento(String codDocSustento) {
        this.codDocSustento = codDocSustento;
    }

    public String getNumDocSustento() {
        return numDocSustento;
    }

    public void setNumDocSustento(String numDocSustento) {
        this.numDocSustento = numDocSustento;
    }

    public String getNumAutDocSustento() {
        return numAutDocSustento;
    }

    public void setNumAutDocSustento(String numAutDocSustento) {
        this.numAutDocSustento = numAutDocSustento;
    }

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

    public void setEmailDestinatario(String emailDestinatario) {
        this.emailDestinatario = emailDestinatario;
    }

    public List<ItemGuiaDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ItemGuiaDto> detalles) {
        this.detalles = detalles;
    }

    public Map<String, String> getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(Map<String, String> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public static class ItemGuiaDto {

        @NotBlank(message = "El código interno del producto es obligatorio")
        @Size(max = 25)
        private String codigoInterno;

        @Size(max = 25)
        private String codigoAdicional;

        @NotBlank(message = "La descripción de la mercadería es obligatoria")
        @Size(max = 300)
        private String descripcion;

        @NotNull(message = "La cantidad transportada es obligatoria")
        @DecimalMin(value = "0.000001")
        private BigDecimal cantidad;

        public ItemGuiaDto() {
        }

        public String getCodigoInterno() {
            return codigoInterno;
        }

        public void setCodigoInterno(String codigoInterno) {
            this.codigoInterno = codigoInterno;
        }

        public String getCodigoAdicional() {
            return codigoAdicional;
        }

        public void setCodigoAdicional(String codigoAdicional) {
            this.codigoAdicional = codigoAdicional;
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
    }
}
