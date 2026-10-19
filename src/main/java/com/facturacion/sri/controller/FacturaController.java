package com.facturacion.sri.controller;

import com.facturacion.sri.dto.EmitirFacturaRequestDto;
import com.facturacion.sri.dto.FacturaResponseDto;
import com.facturacion.sri.dto.RespuestaAutorizacionSriDto;
import com.facturacion.sri.service.FacturacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facturas")
public class FacturaController {

    private final FacturacionService facturacionService;

    public FacturaController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @PostMapping("/emitir")
    public ResponseEntity<FacturaResponseDto> emitirFactura(@Valid @RequestBody EmitirFacturaRequestDto request) {
        FacturaResponseDto response = facturacionService.emitirFactura(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{claveAcceso}/autorizacion")
    public ResponseEntity<RespuestaAutorizacionSriDto> consultarAutorizacion(@PathVariable String claveAcceso) {
        return ResponseEntity.ok(facturacionService.consultarAutorizacionPorClave(claveAcceso));
    }

    @GetMapping(value = "/{claveAcceso}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> descargarXmlFirmado(@PathVariable String claveAcceso) {
        String xmlFirmado = facturacionService.obtenerXmlFirmadoPorClave(claveAcceso);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + claveAcceso + ".xml\"")
                .body(xmlFirmado);
    }

    /**
     * Anula una factura autorizada emitiendo automáticamente su Nota de Crédito
     * electrónica en el SRI.
     */
    @PostMapping("/{claveAcceso}/anular-con-nota-credito")
    public ResponseEntity<FacturaResponseDto> anularConNotaCredito(
            @PathVariable String claveAcceso,
            @RequestParam(defaultValue = "ANULACION DE FACTURA") String motivo) {
        return ResponseEntity.ok(facturacionService.anularFacturaConNotaCredito(claveAcceso, motivo));
    }

    /**
     * Consulta en el WS ConsultaComprobante del SRI el estado de
     * validez/anulación ("AUTORIZADO", "PENDIENTE DE ANULAR", "ANULADO").
     */
    @GetMapping("/{claveAcceso}/estado-validez")
    public ResponseEntity<RespuestaAutorizacionSriDto> verificarEstadoValidez(@PathVariable String claveAcceso) {
        return ResponseEntity.ok(facturacionService.verificarEstadoValidezSri(claveAcceso));
    }

    /**
     * Genera y descarga la Representación Impresa del Documento Electrónico
     * (RIDE) en formato PDF con código de barras Code128 de los 49 dígitos de
     * la clave de acceso.
     */
    @GetMapping(value = "/{claveAcceso}/ride-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarRidePdf(@PathVariable String claveAcceso) {
        byte[] pdfBytes = facturacionService.generarRidePdfPorClave(claveAcceso);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"RIDE_" + claveAcceso + ".pdf\"")
                .body(pdfBytes);
    }

    /**
     * Envía o reenvía por correo electrónico el archivo .XML y el .PDF RIDE por
     * separado.
     */
    @PostMapping("/{claveAcceso}/enviar-email")
    public ResponseEntity< java.util.Map<String, String>> enviarEmailComprobante(
            @PathVariable String claveAcceso,
            @RequestParam(required = false) String email) {
        facturacionService.enviarFacturaPorEmailManual(claveAcceso, email);
        return ResponseEntity.ok(java.util.Map.of(
                "mensaje", "El comprobante XML y su RIDE PDF han sido enviados por correo exitosamente.",
                "claveAcceso", claveAcceso
        ));
    }

    /**
     * Lista todas las facturas almacenadas en PostgreSQL con su estado del SRI.
     */
    @GetMapping
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> listarFacturas() {
        return ResponseEntity.ok(facturacionService.listarFacturasGuardadas());
    }
}
