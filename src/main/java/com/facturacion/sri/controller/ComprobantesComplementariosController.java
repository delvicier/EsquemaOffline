package com.facturacion.sri.controller;

import com.facturacion.sri.dto.*;
import com.facturacion.sri.service.ComprobantesComplementariosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comprobantes")
public class ComprobantesComplementariosController {

    private final ComprobantesComplementariosService service;

    public ComprobantesComplementariosController(ComprobantesComplementariosService service) {
        this.service = service;
    }

    @PostMapping("/nota-credito/emitir")
    public ResponseEntity<FacturaResponseDto> emitirNotaCredito(@Valid @RequestBody EmitirNotaCreditoRequestDto request) {
        return ResponseEntity.ok(service.emitirNotaCredito(request));
    }

    @PostMapping("/nota-debito/emitir")
    public ResponseEntity<FacturaResponseDto> emitirNotaDebito(@Valid @RequestBody EmitirNotaDebitoRequestDto request) {
        return ResponseEntity.ok(service.emitirNotaDebito(request));
    }

    @PostMapping("/liquidacion-compra/emitir")
    public ResponseEntity<FacturaResponseDto> emitirLiquidacionCompra(@Valid @RequestBody EmitirLiquidacionCompraRequestDto request) {
        return ResponseEntity.ok(service.emitirLiquidacionCompra(request));
    }

    @GetMapping(value = "/{claveAcceso}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> descargarXml(@PathVariable String claveAcceso) {
        String xml = service.obtenerXmlFirmadoPorClave(claveAcceso);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + claveAcceso + ".xml\"")
                .body(xml);
    }

    @PostMapping("/retencion/emitir")
    public ResponseEntity<FacturaResponseDto> emitirComprobanteRetencion(
            @Valid @RequestBody EmitirComprobanteRetencionRequestDto request) {
        return ResponseEntity.ok(service.emitirComprobanteRetencion(request));
    }

    @PostMapping("/guia-remision/emitir")
    public ResponseEntity<FacturaResponseDto> emitirGuiaRemision(
            @Valid @RequestBody EmitirGuiaRemisionRequestDto request) {
        return ResponseEntity.ok(service.emitirGuiaRemision(request));
    }
}
