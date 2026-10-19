package com.facturacion.sri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class EmitirFacturaRequestDto {

    @NotBlank(message = "El RUC del emisor es obligatorio")
    @Pattern(regexp = "\\d{10}001", message = "El RUC del emisor debe tener 13 dígitos y terminar en 001")
    private String rucEmisor;

    @NotBlank(message = "El tipo de identificación del comprador es obligatorio")
    @Pattern(regexp = "0[4-8]", message = "Tipo de identificación inválido según Tabla 6 del SRI (use 04 a 08)")
    private String tipoIdentificacionComprador;

    @NotBlank(message = "La identificación del comprador es obligatoria")
    @Size(max = 20, message = "La identificación del comprador no puede superar los 20 caracteres")
    private String identificacionComprador;

    @NotBlank(message = "La razón social o nombres del comprador son obligatorios")
    @Size(max = 300)
    private String razonSocialComprador;

    @Size(max = 300)
    private String direccionComprador;

    @Email(message = "El correo electrónico del comprador no tiene un formato válido")
    private String emailComprador;

    private String telefonoComprador;

    @NotBlank(message = "La forma de pago es obligatoria (Tabla 24 SRI)")
    @Pattern(regexp = "\\d{2}", message = "El código de forma de pago debe tener 2 dígitos")
    private String formaPago;

    private BigDecimal plazoPago;
    private String unidadTiempoPago;
    private BigDecimal propina;
    private String placa;
    private String guiaRemision;

    @NotEmpty(message = "La factura debe contener al menos un ítem en el detalle")
    @Valid
    private List<DetalleItemDto> detalles;

    private Map<String, String> infoAdicional;

    public EmitirFacturaRequestDto() {
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

    public String getDireccionComprador() {
        return direccionComprador;
    }

    public void setDireccionComprador(String direccionComprador) {
        this.direccionComprador = direccionComprador;
    }

    public String getEmailComprador() {
        return emailComprador;
    }

    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }

    public String getTelefonoComprador() {
        return telefonoComprador;
    }

    public void setTelefonoComprador(String telefonoComprador) {
        this.telefonoComprador = telefonoComprador;
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

    public BigDecimal getPropina() {
        return propina;
    }

    public void setPropina(BigDecimal propina) {
        this.propina = propina;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getGuiaRemision() {
        return guiaRemision;
    }

    public void setGuiaRemision(String guiaRemision) {
        this.guiaRemision = guiaRemision;
    }

    public List<DetalleItemDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleItemDto> detalles) {
        this.detalles = detalles;
    }

    public Map<String, String> getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(Map<String, String> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public static class DetalleItemDto {

        @NotBlank(message = "El código principal del producto es obligatorio")
        @Size(max = 25)
        private String codigoPrincipal;

        @Size(max = 25)
        private String codigoAuxiliar;

        @NotBlank(message = "La descripción del producto o servicio es obligatoria")
        @Size(max = 300)
        private String descripcion;

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.000001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidad;

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.000000", message = "El precio unitario no puede ser negativo")
        private BigDecimal precioUnitario;

        @NotNull(message = "El descuento es obligatorio (envíe 0.00 si no aplica)")
        @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
        private BigDecimal descuento;

        @NotBlank(message = "El código de porcentaje de IVA es obligatorio")
        private String codigoPorcentajeIva;

        @NotNull(message = "La tarifa de IVA es obligatoria")
        private BigDecimal tarifaIva;

        public DetalleItemDto() {
        }

        public String getCodigoPrincipal() {
            return codigoPrincipal;
        }

        public void setCodigoPrincipal(String codigoPrincipal) {
            this.codigoPrincipal = codigoPrincipal;
        }

        public String getCodigoAuxiliar() {
            return codigoAuxiliar;
        }

        public void setCodigoAuxiliar(String codigoAuxiliar) {
            this.codigoAuxiliar = codigoAuxiliar;
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

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getDescuento() {
            return descuento;
        }

        public void setDescuento(BigDecimal descuento) {
            this.descuento = descuento;
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
    }
}
