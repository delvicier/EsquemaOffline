package com.facturacion.sri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

public class EmitirNotaCreditoRequestDto {

    @NotBlank(message = "El RUC del emisor es obligatorio")
    @Pattern(regexp = "\\d{10}001", message = "El RUC del emisor debe tener 13 dígitos y terminar en 001")
    private String rucEmisor;

    // En Notas de Crédito NO se permite Consumidor Final (07) según sección 5.7 de la Ficha Técnica
    @NotBlank(message = "El tipo de identificación del comprador es obligatorio")
    @Pattern(regexp = "0[4568]", message = "Para Notas de Crédito use 04 (RUC), 05 (Cédula), 06 (Pasaporte) o 08 (Exterior)")
    private String tipoIdentificacionComprador;

    @NotBlank(message = "La identificación del comprador es obligatoria")
    @Size(max = 20)
    private String identificacionComprador;

    @NotBlank(message = "La razón social del comprador es obligatoria")
    @Size(max = 300)
    private String razonSocialComprador;

    @NotBlank(message = "El código del documento modificado es obligatorio (ej. '01' para Factura)")
    @Pattern(regexp = "\\d{2}")
    private String codDocModificado;

    @NotBlank(message = "El número del comprobante modificado es obligatorio (formato 001-001-000000001)")
    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{9}", message = "El formato debe ser 001-001-000000001")
    private String numDocModificado;

    @NotBlank(message = "La fecha de emisión del documento sustento es obligatoria (formato dd/MM/yyyy)")
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/20[0-9][0-9]", message = "Use el formato dd/MM/yyyy")
    private String fechaEmisionDocSustento;

    @NotBlank(message = "El motivo de la modificación es obligatorio")
    @Size(max = 300)
    private String motivo;

    @Email
    private String emailComprador;

    @NotEmpty(message = "Debe incluir al menos un ítem en el detalle de la Nota de Crédito")
    @Valid
    private List<EmitirFacturaRequestDto.DetalleItemDto> detalles;

    private Map<String, String> infoAdicional;

    public EmitirNotaCreditoRequestDto() {
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getTipoIdentificacionComprador() {
        return tipoIdentificacionComprador;
    }

    public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
        this.tipoIdentificacionComprador = tipoIdentificacionComprador;
    }

    public String getIdentificacionComprador() {
        return identificacionComprador;
    }

    public void setIdentificacionComprador(String identificacionComprador) {
        this.identificacionComprador = identificacionComprador;
    }

    public String getRazonSocialComprador() {
        return razonSocialComprador;
    }

    public void setRazonSocialComprador(String razonSocialComprador) {
        this.razonSocialComprador = razonSocialComprador;
    }

    public String getCodDocModificado() {
        return codDocModificado;
    }

    public void setCodDocModificado(String codDocModificado) {
        this.codDocModificado = codDocModificado;
    }

    public String getNumDocModificado() {
        return numDocModificado;
    }

    public void setNumDocModificado(String numDocModificado) {
        this.numDocModificado = numDocModificado;
    }

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEmailComprador() {
        return emailComprador;
    }

    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }

    public List<EmitirFacturaRequestDto.DetalleItemDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<EmitirFacturaRequestDto.DetalleItemDto> detalles) {
        this.detalles = detalles;
    }

    public Map<String, String> getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(Map<String, String> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }
}
