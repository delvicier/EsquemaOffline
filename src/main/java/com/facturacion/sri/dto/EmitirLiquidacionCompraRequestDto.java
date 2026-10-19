package com.facturacion.sri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class EmitirLiquidacionCompraRequestDto {

    @NotBlank(message = "El RUC del emisor es obligatorio")
    @Pattern(regexp = "\\d{10}001")
    private String rucEmisor;

    // En Liquidaciones de Compra NO está habilitado Consumidor Final (07) según sección 5.7 Ficha Técnica
    @NotBlank(message = "El tipo de identificación del proveedor es obligatorio")
    @Pattern(regexp = "0[4568]", message = "Para Liquidación de Compra use 04 (RUC), 05 (Cédula), 06 (Pasaporte) o 08 (Exterior)")
    private String tipoIdentificacionProveedor;

    @NotBlank(message = "La identificación del proveedor es obligatoria")
    @Size(max = 20)
    private String identificacionProveedor;

    @NotBlank(message = "La razón social o nombres del proveedor son obligatorios")
    @Size(max = 300)
    private String razonSocialProveedor;

    @Size(max = 300)
    private String direccionProveedor;

    @Email
    private String emailProveedor;

    @NotBlank(message = "La forma de pago es obligatoria (Tabla 24 SRI)")
    @Pattern(regexp = "\\d{2}")
    private String formaPago;

    private BigDecimal plazoPago;
    private String unidadTiempoPago;

    @NotEmpty(message = "La Liquidación de Compra debe tener al menos un ítem en el detalle")
    @Valid
    private List<EmitirFacturaRequestDto.DetalleItemDto> detalles;

    private Map<String, String> infoAdicional;

    public EmitirLiquidacionCompraRequestDto() {
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getTipoIdentificacionProveedor() {
        return tipoIdentificacionProveedor;
    }

    public void setTipoIdentificacionProveedor(String tipoIdentificacionProveedor) {
        this.tipoIdentificacionProveedor = tipoIdentificacionProveedor;
    }

    public String getIdentificacionProveedor() {
        return identificacionProveedor;
    }

    public void setIdentificacionProveedor(String identificacionProveedor) {
        this.identificacionProveedor = identificacionProveedor;
    }

    public String getRazonSocialProveedor() {
        return razonSocialProveedor;
    }

    public void setRazonSocialProveedor(String razonSocialProveedor) {
        this.razonSocialProveedor = razonSocialProveedor;
    }

    public String getDireccionProveedor() {
        return direccionProveedor;
    }

    public void setDireccionProveedor(String direccionProveedor) {
        this.direccionProveedor = direccionProveedor;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
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
