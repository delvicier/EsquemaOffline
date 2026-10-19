package com.facturacion.sri.xml;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "factura")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "infoTributaria",
    "infoFactura",
    "detalles",
    "infoAdicional"
})
public class FacturaXml {

    @XmlAttribute(name = "id", required = true)
    private String id = "comprobante";

    @XmlAttribute(name = "version", required = true)
    private String version = "2.1.0";

    @XmlElement(name = "infoTributaria", required = true)
    private InfoTributaria infoTributaria;

    @XmlElement(name = "infoFactura", required = true)
    private InfoFactura infoFactura;

    @XmlElement(name = "detalles", required = true)
    private Detalles detalles;

    @XmlElement(name = "infoAdicional")
    private InfoAdicional infoAdicional;

    public FacturaXml() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    public InfoFactura getInfoFactura() {
        return infoFactura;
    }

    public void setInfoFactura(InfoFactura infoFactura) {
        this.infoFactura = infoFactura;
    }

    public Detalles getDetalles() {
        return detalles;
    }

    public void setDetalles(Detalles detalles) {
        this.detalles = detalles;
    }

    public InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(InfoAdicional infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "ambiente", "tipoEmision", "razonSocial", "nombreComercial", "ruc",
        "claveAcceso", "codDoc", "estab", "ptoEmi", "secuencial",
        "dirMatriz", "agenteRetencion", "contribuyenteRimpe"
    })
    public static class InfoTributaria {

        @XmlElement(required = true)
        private String ambiente;
        @XmlElement(required = true)
        private String tipoEmision;
        @XmlElement(required = true)
        private String razonSocial;
        @XmlElement
        private String nombreComercial;
        @XmlElement(required = true)
        private String ruc;
        @XmlElement(required = true)
        private String claveAcceso;
        @XmlElement(required = true)
        private String codDoc;
        @XmlElement(required = true)
        private String estab;
        @XmlElement(required = true)
        private String ptoEmi;
        @XmlElement(required = true)
        private String secuencial;
        @XmlElement(required = true)
        private String dirMatriz;
        @XmlElement
        private String agenteRetencion;
        @XmlElement
        private String contribuyenteRimpe;

        public InfoTributaria() {
        }

        public String getAmbiente() {
            return ambiente;
        }

        public void setAmbiente(String ambiente) {
            this.ambiente = ambiente;
        }

        public String getTipoEmision() {
            return tipoEmision;
        }

        public void setTipoEmision(String tipoEmision) {
            this.tipoEmision = tipoEmision;
        }

        public String getRazonSocial() {
            return razonSocial;
        }

        public void setRazonSocial(String razonSocial) {
            this.razonSocial = razonSocial;
        }

        public String getNombreComercial() {
            return nombreComercial;
        }

        public void setNombreComercial(String nombreComercial) {
            this.nombreComercial = nombreComercial;
        }

        public String getRuc() {
            return ruc;
        }

        public void setRuc(String ruc) {
            this.ruc = ruc;
        }

        public String getClaveAcceso() {
            return claveAcceso;
        }

        public void setClaveAcceso(String claveAcceso) {
            this.claveAcceso = claveAcceso;
        }

        public String getCodDoc() {
            return codDoc;
        }

        public void setCodDoc(String codDoc) {
            this.codDoc = codDoc;
        }

        public String getEstab() {
            return estab;
        }

        public void setEstab(String estab) {
            this.estab = estab;
        }

        public String getPtoEmi() {
            return ptoEmi;
        }

        public void setPtoEmi(String ptoEmi) {
            this.ptoEmi = ptoEmi;
        }

        public String getSecuencial() {
            return secuencial;
        }

        public void setSecuencial(String secuencial) {
            this.secuencial = secuencial;
        }

        public String getDirMatriz() {
            return dirMatriz;
        }

        public void setDirMatriz(String dirMatriz) {
            this.dirMatriz = dirMatriz;
        }

        public String getAgenteRetencion() {
            return agenteRetencion;
        }

        public void setAgenteRetencion(String agenteRetencion) {
            this.agenteRetencion = agenteRetencion;
        }

        public String getContribuyenteRimpe() {
            return contribuyenteRimpe;
        }

        public void setContribuyenteRimpe(String contribuyenteRimpe) {
            this.contribuyenteRimpe = contribuyenteRimpe;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "fechaEmision", "dirEstablecimiento", "contribuyenteEspecial", "obligadoContabilidad",
        "tipoIdentificacionComprador", "guiaRemision", "razonSocialComprador", "identificacionComprador",
        "direccionComprador", "totalSinImpuestos", "totalSubsidio", "totalDescuento",
        "totalConImpuestos", "propina", "importeTotal", "moneda", "placa", "pagos",
        "valorRetIva", "valorRetRenta"
    })
    public static class InfoFactura {

        @XmlElement(required = true)
        private String fechaEmision;
        @XmlElement
        private String dirEstablecimiento;
        @XmlElement
        private String contribuyenteEspecial;
        @XmlElement
        private String obligadoContabilidad;
        @XmlElement(required = true)
        private String tipoIdentificacionComprador;
        @XmlElement
        private String guiaRemision;
        @XmlElement(required = true)
        private String razonSocialComprador;
        @XmlElement(required = true)
        private String identificacionComprador;
        @XmlElement
        private String direccionComprador;
        @XmlElement(required = true)
        private BigDecimal totalSinImpuestos;
        @XmlElement
        private BigDecimal totalSubsidio;
        @XmlElement(required = true)
        private BigDecimal totalDescuento;
        @XmlElement(required = true)
        private TotalConImpuestos totalConImpuestos;
        @XmlElement(required = true)
        private BigDecimal propina;
        @XmlElement(required = true)
        private BigDecimal importeTotal;
        @XmlElement
        private String moneda;
        @XmlElement
        private String placa;
        @XmlElement
        private Pagos pagos;
        @XmlElement
        private BigDecimal valorRetIva;
        @XmlElement
        private BigDecimal valorRetRenta;

        public InfoFactura() {
        }

        public String getFechaEmision() {
            return fechaEmision;
        }

        public void setFechaEmision(String fechaEmision) {
            this.fechaEmision = fechaEmision;
        }

        public String getDirEstablecimiento() {
            return dirEstablecimiento;
        }

        public void setDirEstablecimiento(String dirEstablecimiento) {
            this.dirEstablecimiento = dirEstablecimiento;
        }

        public String getContribuyenteEspecial() {
            return contribuyenteEspecial;
        }

        public void setContribuyenteEspecial(String contribuyenteEspecial) {
            this.contribuyenteEspecial = contribuyenteEspecial;
        }

        public String getObligadoContabilidad() {
            return obligadoContabilidad;
        }

        public void setObligadoContabilidad(String obligadoContabilidad) {
            this.obligadoContabilidad = obligadoContabilidad;
        }

        public String getTipoIdentificacionComprador() {
            return tipoIdentificacionComprador;
        }

        public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
            this.tipoIdentificacionComprador = tipoIdentificacionComprador;
        }

        public String getGuiaRemision() {
            return guiaRemision;
        }

        public void setGuiaRemision(String guiaRemision) {
            this.guiaRemision = guiaRemision;
        }

        public String getRazonSocialComprador() {
            return razonSocialComprador;
        }

        public void setRazonSocialComprador(String razonSocialComprador) {
            this.razonSocialComprador = razonSocialComprador;
        }

        public String getIdentificacionComprador() {
            return identificacionComprador;
        }

        public void setIdentificacionComprador(String identificacionComprador) {
            this.identificacionComprador = identificacionComprador;
        }

        public String getDireccionComprador() {
            return direccionComprador;
        }

        public void setDireccionComprador(String direccionComprador) {
            this.direccionComprador = direccionComprador;
        }

        public BigDecimal getTotalSinImpuestos() {
            return totalSinImpuestos;
        }

        public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
            this.totalSinImpuestos = totalSinImpuestos;
        }

        public BigDecimal getTotalSubsidio() {
            return totalSubsidio;
        }

        public void setTotalSubsidio(BigDecimal totalSubsidio) {
            this.totalSubsidio = totalSubsidio;
        }

        public BigDecimal getTotalDescuento() {
            return totalDescuento;
        }

        public void setTotalDescuento(BigDecimal totalDescuento) {
            this.totalDescuento = totalDescuento;
        }

        public TotalConImpuestos getTotalConImpuestos() {
            return totalConImpuestos;
        }

        public void setTotalConImpuestos(TotalConImpuestos totalConImpuestos) {
            this.totalConImpuestos = totalConImpuestos;
        }

        public BigDecimal getPropina() {
            return propina;
        }

        public void setPropina(BigDecimal propina) {
            this.propina = propina;
        }

        public BigDecimal getImporteTotal() {
            return importeTotal;
        }

        public void setImporteTotal(BigDecimal importeTotal) {
            this.importeTotal = importeTotal;
        }

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
        }

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(String placa) {
            this.placa = placa;
        }

        public Pagos getPagos() {
            return pagos;
        }

        public void setPagos(Pagos pagos) {
            this.pagos = pagos;
        }

        public BigDecimal getValorRetIva() {
            return valorRetIva;
        }

        public void setValorRetIva(BigDecimal valorRetIva) {
            this.valorRetIva = valorRetIva;
        }

        public BigDecimal getValorRetRenta() {
            return valorRetRenta;
        }

        public void setValorRetRenta(BigDecimal valorRetRenta) {
            this.valorRetRenta = valorRetRenta;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalConImpuestos {

        @XmlElement(name = "totalImpuesto", required = true)
        private List<TotalImpuesto> totalImpuesto;

        public TotalConImpuestos() {
        }

        public TotalConImpuestos(List<TotalImpuesto> totalImpuesto) {
            this.totalImpuesto = totalImpuesto;
        }

        public List<TotalImpuesto> getTotalImpuesto() {
            return totalImpuesto;
        }

        public void setTotalImpuesto(List<TotalImpuesto> totalImpuesto) {
            this.totalImpuesto = totalImpuesto;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "codigo", "codigoPorcentaje", "descuentoAdicional",
        "baseImponible", "tarifa", "valor", "valorDevolucionIva"
    })
    public static class TotalImpuesto {

        @XmlElement(required = true)
        private String codigo;
        @XmlElement(required = true)
        private String codigoPorcentaje;
        @XmlElement
        private BigDecimal descuentoAdicional;
        @XmlElement(required = true)
        private BigDecimal baseImponible;
        @XmlElement
        private BigDecimal tarifa;
        @XmlElement(required = true)
        private BigDecimal valor;
        @XmlElement
        private BigDecimal valorDevolucionIva;

        public TotalImpuesto() {
        }

        public TotalImpuesto(String codigo, String codigoPorcentaje, BigDecimal baseImponible, BigDecimal tarifa, BigDecimal valor) {
            this.codigo = codigo;
            this.codigoPorcentaje = codigoPorcentaje;
            this.baseImponible = baseImponible;
            this.tarifa = tarifa;
            this.valor = valor;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigoPorcentaje() {
            return codigoPorcentaje;
        }

        public void setCodigoPorcentaje(String codigoPorcentaje) {
            this.codigoPorcentaje = codigoPorcentaje;
        }

        public BigDecimal getDescuentoAdicional() {
            return descuentoAdicional;
        }

        public void setDescuentoAdicional(BigDecimal descuentoAdicional) {
            this.descuentoAdicional = descuentoAdicional;
        }

        public BigDecimal getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(BigDecimal baseImponible) {
            this.baseImponible = baseImponible;
        }

        public BigDecimal getTarifa() {
            return tarifa;
        }

        public void setTarifa(BigDecimal tarifa) {
            this.tarifa = tarifa;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public BigDecimal getValorDevolucionIva() {
            return valorDevolucionIva;
        }

        public void setValorDevolucionIva(BigDecimal valorDevolucionIva) {
            this.valorDevolucionIva = valorDevolucionIva;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Pagos {

        @XmlElement(name = "pago", required = true)
        private List<Pago> pago;

        public Pagos() {
        }

        public Pagos(List<Pago> pago) {
            this.pago = pago;
        }

        public List<Pago> getPago() {
            return pago;
        }

        public void setPago(List<Pago> pago) {
            this.pago = pago;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"formaPago", "total", "plazo", "unidadTiempo"})
    public static class Pago {

        @XmlElement(required = true)
        private String formaPago;
        @XmlElement(required = true)
        private BigDecimal total;
        @XmlElement
        private BigDecimal plazo;
        @XmlElement
        private String unidadTiempo;

        public Pago() {
        }

        public Pago(String formaPago, BigDecimal total, BigDecimal plazo, String unidadTiempo) {
            this.formaPago = formaPago;
            this.total = total;
            this.plazo = plazo;
            this.unidadTiempo = unidadTiempo;
        }

        public String getFormaPago() {
            return formaPago;
        }

        public void setFormaPago(String formaPago) {
            this.formaPago = formaPago;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public BigDecimal getPlazo() {
            return plazo;
        }

        public void setPlazo(BigDecimal plazo) {
            this.plazo = plazo;
        }

        public String getUnidadTiempo() {
            return unidadTiempo;
        }

        public void setUnidadTiempo(String unidadTiempo) {
            this.unidadTiempo = unidadTiempo;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Detalles {

        @XmlElement(name = "detalle", required = true)
        private List<Detalle> detalle;

        public Detalles() {
        }

        public Detalles(List<Detalle> detalle) {
            this.detalle = detalle;
        }

        public List<Detalle> getDetalle() {
            return detalle;
        }

        public void setDetalle(List<Detalle> detalle) {
            this.detalle = detalle;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
        "codigoPrincipal", "codigoAuxiliar", "descripcion", "unidadMedida",
        "cantidad", "precioUnitario", "precioSinSubsidio", "descuento",
        "precioTotalSinImpuesto", "detallesAdicionales", "impuestos"
    })
    public static class Detalle {

        @XmlElement(required = true)
        private String codigoPrincipal;
        @XmlElement
        private String codigoAuxiliar;
        @XmlElement(required = true)
        private String descripcion;
        @XmlElement
        private String unidadMedida;
        @XmlElement(required = true)
        private BigDecimal cantidad;
        @XmlElement(required = true)
        private BigDecimal precioUnitario;
        @XmlElement
        private BigDecimal precioSinSubsidio;
        @XmlElement(required = true)
        private BigDecimal descuento;
        @XmlElement(required = true)
        private BigDecimal precioTotalSinImpuesto;
        @XmlElement
        private DetallesAdicionales detallesAdicionales;
        @XmlElement(required = true)
        private ImpuestosDetalle impuestos;

        public Detalle() {
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

        public String getUnidadMedida() {
            return unidadMedida;
        }

        public void setUnidadMedida(String unidadMedida) {
            this.unidadMedida = unidadMedida;
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

        public BigDecimal getPrecioSinSubsidio() {
            return precioSinSubsidio;
        }

        public void setPrecioSinSubsidio(BigDecimal precioSinSubsidio) {
            this.precioSinSubsidio = precioSinSubsidio;
        }

        public BigDecimal getDescuento() {
            return descuento;
        }

        public void setDescuento(BigDecimal descuento) {
            this.descuento = descuento;
        }

        public BigDecimal getPrecioTotalSinImpuesto() {
            return precioTotalSinImpuesto;
        }

        public void setPrecioTotalSinImpuesto(BigDecimal precioTotalSinImpuesto) {
            this.precioTotalSinImpuesto = precioTotalSinImpuesto;
        }

        public DetallesAdicionales getDetallesAdicionales() {
            return detallesAdicionales;
        }

        public void setDetallesAdicionales(DetallesAdicionales detallesAdicionales) {
            this.detallesAdicionales = detallesAdicionales;
        }

        public ImpuestosDetalle getImpuestos() {
            return impuestos;
        }

        public void setImpuestos(ImpuestosDetalle impuestos) {
            this.impuestos = impuestos;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DetallesAdicionales {

        @XmlElement(name = "detAdicional", required = true)
        private List<DetAdicional> detAdicional;

        public DetallesAdicionales() {
        }

        public DetallesAdicionales(List<DetAdicional> detAdicional) {
            this.detAdicional = detAdicional;
        }

        public List<DetAdicional> getDetAdicional() {
            return detAdicional;
        }

        public void setDetAdicional(List<DetAdicional> detAdicional) {
            this.detAdicional = detAdicional;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DetAdicional {

        @XmlAttribute(name = "nombre", required = true)
        private String nombre;
        @XmlAttribute(name = "valor", required = true)
        private String valor;

        public DetAdicional() {
        }

        public DetAdicional(String nombre, String valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ImpuestosDetalle {

        @XmlElement(name = "impuesto", required = true)
        private List<ImpuestoDetalle> impuesto;

        public ImpuestosDetalle() {
        }

        public ImpuestosDetalle(List<ImpuestoDetalle> impuesto) {
            this.impuesto = impuesto;
        }

        public List<ImpuestoDetalle> getImpuesto() {
            return impuesto;
        }

        public void setImpuesto(List<ImpuestoDetalle> impuesto) {
            this.impuesto = impuesto;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"codigo", "codigoPorcentaje", "tarifa", "baseImponible", "valor"})
    public static class ImpuestoDetalle {

        @XmlElement(required = true)
        private String codigo;
        @XmlElement(required = true)
        private String codigoPorcentaje;
        @XmlElement(required = true)
        private BigDecimal tarifa;
        @XmlElement(required = true)
        private BigDecimal baseImponible;
        @XmlElement(required = true)
        private BigDecimal valor;

        public ImpuestoDetalle() {
        }

        public ImpuestoDetalle(String codigo, String codigoPorcentaje, BigDecimal tarifa, BigDecimal baseImponible, BigDecimal valor) {
            this.codigo = codigo;
            this.codigoPorcentaje = codigoPorcentaje;
            this.tarifa = tarifa;
            this.baseImponible = baseImponible;
            this.valor = valor;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigoPorcentaje() {
            return codigoPorcentaje;
        }

        public void setCodigoPorcentaje(String codigoPorcentaje) {
            this.codigoPorcentaje = codigoPorcentaje;
        }

        public BigDecimal getTarifa() {
            return tarifa;
        }

        public void setTarifa(BigDecimal tarifa) {
            this.tarifa = tarifa;
        }

        public BigDecimal getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(BigDecimal baseImponible) {
            this.baseImponible = baseImponible;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InfoAdicional {

        @XmlElement(name = "campoAdicional", required = true)
        private List<CampoAdicional> campoAdicional;

        public InfoAdicional() {
        }

        public InfoAdicional(List<CampoAdicional> campoAdicional) {
            this.campoAdicional = campoAdicional;
        }

        public List<CampoAdicional> getCampoAdicional() {
            return campoAdicional;
        }

        public void setCampoAdicional(List<CampoAdicional> campoAdicional) {
            this.campoAdicional = campoAdicional;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CampoAdicional {

        @XmlAttribute(name = "nombre", required = true)
        private String nombre;
        @XmlValue
        private String valor;

        public CampoAdicional() {
        }

        public CampoAdicional(String nombre, String valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }
    }
}
