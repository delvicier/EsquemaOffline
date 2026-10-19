package com.facturacion.sri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class EmitirComprobanteRetencionRequestDto {

    @NotBlank(message = "El RUC del emisor es obligatorio")
    @Pattern(regexp = "\\d{10}001")
    private String rucEmisor;

    // Sección 5.7 Ficha Técnica: No se permite Consumidor Final (07) en Comprobantes de Retención
    @NotBlank(message = "El tipo de identificación del sujeto retenido es obligatorio")
    @Pattern(regexp = "0[4568]", message = "Use 04 (RUC), 05 (Cédula), 06 (Pasaporte) o 08 (Exterior)")
    private String tipoIdentificacionSujetoRetenido;

    private String tipoSujetoRetenido; // Opcional: "01" Persona Natural, "02" Sociedad (Obligatorio si es "08")

    @NotBlank(message = "El campo parteRel es obligatorio ('SI' o 'NO')")
    @Pattern(regexp = "SI|NO", message = "El campo parteRel debe ser 'SI' o 'NO'")
    private String parteRel;

    @NotBlank(message = "La identificación del sujeto retenido es obligatoria")
    @Size(max = 20)
    private String identificacionSujetoRetenido;

    @NotBlank(message = "La razón social del sujeto retenido es obligatoria")
    @Size(max = 300)
    private String razonSocialSujetoRetenido;

    @NotBlank(message = "El periodo fiscal es obligatorio en formato mm/aaaa (ej. 10/2026)")
    @Pattern(regexp = "(0[1-9]|1[012])/20[0-9][0-9]", message = "El formato de periodoFiscal debe ser mm/aaaa")
    private String periodoFiscal;

    // Datos del Documento Sustento (Factura o Liquidación que origina la retención)
    @NotBlank(message = "El código de sustento es obligatorio (ej. '01' Crédito Tributario)")
    @Pattern(regexp = "\\d{2}")
    private String codSustento;

    @NotBlank(message = "El código de documento sustento es obligatorio (ej. '01' Factura)")
    @Pattern(regexp = "\\d{2}")
    private String codDocSustento;

    @NotBlank(message = "El número de documento sustento es obligatorio (15 dígitos o 001-001-000000001)")
    private String numDocSustento;

    @NotBlank(message = "La fecha de emisión del documento sustento es obligatoria (dd/MM/yyyy)")
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/20[0-9][0-9]")
    private String fechaEmisionDocSustento;

    private String numAutDocSustento; // Clave de acceso de 49 dígitos de la factura sustento

    @NotBlank(message = "El código de porcentaje de IVA del documento sustento es obligatorio (ej. '4' para 15%, '0' para 0%)")
    private String codigoPorcentajeIvaSustento;

    @NotNull(message = "La tarifa de IVA del documento sustento es obligatoria (ej. 15.00, 0.00)")
    private BigDecimal tarifaIvaSustento;

    @NotNull(message = "La base imponible (totalSinImpuestos) del documento sustento es obligatoria")
    @DecimalMin(value = "0.00")
    private BigDecimal baseImponibleSustento;

    @NotBlank(message = "La forma de pago es obligatoria (ej. '01' o '20')")
    @Pattern(regexp = "\\d{2}")
    private String formaPago;

    @Email
    private String emailSujetoRetenido;

    @NotEmpty(message = "Debe incluir al menos una línea de retención (Renta, IVA o ISD)")
    @Valid
    private List<ItemRetencionDto> retenciones;

    private Map<String, String> infoAdicional;

    public EmitirComprobanteRetencionRequestDto() {
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getTipoIdentificacionSujetoRetenido() {
        return tipoIdentificacionSujetoRetenido;
    }

    public void setTipoIdentificacionSujetoRetenido(String tipoIdentificacionSujetoRetenido) {
        this.tipoIdentificacionSujetoRetenido = tipoIdentificacionSujetoRetenido;
    }

    public String getTipoSujetoRetenido() {
        return tipoSujetoRetenido;
    }

    public void setTipoSujetoRetenido(String tipoSujetoRetenido) {
        this.tipoSujetoRetenido = tipoSujetoRetenido;
    }

    public String getParteRel() {
        return parteRel;
    }

    public void setParteRel(String parteRel) {
        this.parteRel = parteRel;
    }

    public String getIdentificacionSujetoRetenido() {
        return identificacionSujetoRetenido;
    }

    public void setIdentificacionSujetoRetenido(String identificacionSujetoRetenido) {
        this.identificacionSujetoRetenido = identificacionSujetoRetenido;
    }

    public String getRazonSocialSujetoRetenido() {
        return razonSocialSujetoRetenido;
    }

    public void setRazonSocialSujetoRetenido(String razonSocialSujetoRetenido) {
        this.razonSocialSujetoRetenido = razonSocialSujetoRetenido;
    }

    public String getPeriodoFiscal() {
        return periodoFiscal;
    }

    public void setPeriodoFiscal(String periodoFiscal) {
        this.periodoFiscal = periodoFiscal;
    }

    public String getCodSustento() {
        return codSustento;
    }

    public void setCodSustento(String codSustento) {
        this.codSustento = codSustento;
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

    public String getFechaEmisionDocSustento() {
        return fechaEmisionDocSustento;
    }

    public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
        this.fechaEmisionDocSustento = fechaEmisionDocSustento;
    }

    public String getNumAutDocSustento() {
        return numAutDocSustento;
    }

    public void setNumAutDocSustento(String numAutDocSustento) {
        this.numAutDocSustento = numAutDocSustento;
    }

    public String getCodigoPorcentajeIvaSustento() {
        return codigoPorcentajeIvaSustento;
    }

    public void setCodigoPorcentajeIvaSustento(String codigoPorcentajeIvaSustento) {
        this.codigoPorcentajeIvaSustento = codigoPorcentajeIvaSustento;
    }

    public BigDecimal getTarifaIvaSustento() {
        return tarifaIvaSustento;
    }

    public void setTarifaIvaSustento(BigDecimal tarifaIvaSustento) {
        this.tarifaIvaSustento = tarifaIvaSustento;
    }

    public BigDecimal getBaseImponibleSustento() {
        return baseImponibleSustento;
    }

    public void setBaseImponibleSustento(BigDecimal baseImponibleSustento) {
        this.baseImponibleSustento = baseImponibleSustento;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getEmailSujetoRetenido() {
        return emailSujetoRetenido;
    }

    public void setEmailSujetoRetenido(String emailSujetoRetenido) {
        this.emailSujetoRetenido = emailSujetoRetenido;
    }

    public List<ItemRetencionDto> getRetenciones() {
        return retenciones;
    }

    public void setRetenciones(List<ItemRetencionDto> retenciones) {
        this.retenciones = retenciones;
    }

    public Map<String, String> getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(Map<String, String> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public static class ItemRetencionDto {

        // Tabla 19: "1" = Renta, "2" = IVA, "6" = ISD
        @NotBlank(message = "El código de impuesto a retener es obligatorio ('1'=Renta, '2'=IVA, '6'=ISD)")
        @Pattern(regexp = "[126]")
        private String codigo;

        @NotBlank(message = "El código de retención es obligatorio (Tabla 20 SRI)")
        @Size(max = 5)
        private String codigoRetencion;

        @NotNull(message = "La base imponible de la retención es obligatoria")
        @DecimalMin(value = "0.00")
        private BigDecimal baseImponible;

        @NotNull(message = "El porcentaje a retener es obligatorio")
        @DecimalMin(value = "0.00")
        private BigDecimal porcentajeRetener;

        public ItemRetencionDto() {
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigoRetencion() {
            return codigoRetencion;
        }

        public void setCodigoRetencion(String codigoRetencion) {
            this.codigoRetencion = codigoRetencion;
        }

        public BigDecimal getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(BigDecimal baseImponible) {
            this.baseImponible = baseImponible;
        }

        public BigDecimal getPorcentajeRetener() {
            return porcentajeRetener;
        }

        public void setPorcentajeRetener(BigDecimal porcentajeRetener) {
            this.porcentajeRetener = porcentajeRetener;
        }
    }
}
