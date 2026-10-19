package com.facturacion.sri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class EmitirNotaDebitoRequestDto {

    @NotBlank(message = "El RUC del emisor es obligatorio")
    @Pattern(regexp = "\\d{10}001")
    private String rucEmisor;

    @NotBlank(message = "El tipo de identificación del comprador es obligatorio")
    @Pattern(regexp = "0[4568]", message = "Para Notas de Débito use 04 (RUC), 05 (Cédula), 06 (Pasaporte) o 08 (Exterior)")
    private String tipoIdentificacionComprador;

    @NotBlank(message = "La identificación del comprador es obligatoria")
    @Size(max = 20)
    private String identificacionComprador;

    @NotBlank(message = "La razón social del comprador es obligatoria")
    @Size(max = 300)
    private String razonSocialComprador;

    @NotBlank(message = "El código del documento modificado es obligatorio (ej. '01')")
    @Pattern(regexp = "\\d{2}")
    private String codDocModificado;

    @NotBlank(message = "El número del comprobante modificado es obligatorio (001-001-000000001)")
    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{9}")
    private String numDocModificado;

    @NotBlank(message = "La fecha de emisión del documento sustento es obligatoria (dd/MM/yyyy)")
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/20[0-9][0-9]")
    private String fechaEmisionDocSustento;

    @NotBlank(message = "El código de porcentaje de IVA es obligatorio (Tabla 17 SRI: '4' para 15%, '0' para 0%)")
    private String codigoPorcentajeIva;

    @NotNull(message = "La tarifa de IVA es obligatoria (ej. 15.00, 0.00)")
    private BigDecimal tarifaIva;

    @NotBlank(message = "La forma de pago es obligatoria (Tabla 24 SRI)")
    @Pattern(regexp = "\\d{2}")
    private String formaPago;

    private BigDecimal plazoPago;
    private String unidadTiempoPago;

    @Email
    private String emailComprador;

    @NotEmpty(message = "Debe incluir al menos un motivo en la Nota de Débito")
    @Valid
    private List<MotivoItemDto> motivos;

    private Map<String, String> infoAdicional;

    public EmitirNotaDebitoRequestDto() {
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

    public String getCodigoPorcentajeIva() {
        return codigoPorcentajeIva;
    }

    public void setCodigoPorcentajeIva(String codigoPorcentajeIva) {
        this.codigoPorcentajeIva = codigoPorcentajeIva;
    }

    public BigDecimal getTarifaIva() {
        return tarifaIva;
    }

    public void setTarifaIva(BigDecimal tarifaIva) {
        this.tarifaIva = tarifaIva;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public BigDecimal getPlazoPago() {
        return plazoPago;
    }

    public void setPlazoPago(BigDecimal plazoPago) {
        this.plazoPago = plazoPago;
    }

    public String getUnidadTiempoPago() {
        return unidadTiempoPago;
    }

    public void setUnidadTiempoPago(String unidadTiempoPago) {
        this.unidadTiempoPago = unidadTiempoPago;
    }

    public String getEmailComprador() {
        return emailComprador;
    }

    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }

    public List<MotivoItemDto> getMotivos() {
        return motivos;
    }

    public void setMotivos(List<MotivoItemDto> motivos) {
        this.motivos = motivos;
    }

    public Map<String, String> getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(Map<String, String> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public static class MotivoItemDto {

        @NotBlank(message = "La razón del cargo o débito es obligatoria")
        @Size(max = 300)
        private String razon;

        @NotNull(message = "El valor del motivo es obligatorio")
        @DecimalMin(value = "0.00")
        private BigDecimal valor;

        public MotivoItemDto() {
        }

        public String getRazon() {
            return razon;
        }

        public void setRazon(String razon) {
            this.razon = razon;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }
}
