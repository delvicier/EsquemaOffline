package com.facturacion.sri.controller;

import com.facturacion.sri.dto.ConfigurarSecuencialesRequestDto;
import com.facturacion.sri.dto.ConfigurarSmtpRequestDto;
import com.facturacion.sri.entity.Emisor;
import com.facturacion.sri.entity.SecuencialComprobante;
import com.facturacion.sri.repository.EmisorRepository;
import com.facturacion.sri.repository.SecuencialRepository;
import com.facturacion.sri.service.EmailNotificationService;
import com.facturacion.sri.service.XadesBesSignerService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/emisores")
public class EmisorController {

    private final EmisorRepository emisorRepository;
    private final SecuencialRepository secuencialRepository;
    private final XadesBesSignerService signerService;
    private final EmailNotificationService emailNotificationService;

    public EmisorController(
            EmisorRepository emisorRepository,
            SecuencialRepository secuencialRepository,
            XadesBesSignerService signerService,
            EmailNotificationService emailNotificationService) {
        this.emisorRepository = emisorRepository;
        this.secuencialRepository = secuencialRepository;
        this.signerService = signerService;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * Configura el Emisor, guarda su certificado .p12 en PostgreSQL y exige el
     * último secuencial de factura emitido.
     */
    @PostMapping(value = "/configurar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> configurarEmisor(
            @RequestParam("archivoFirma") MultipartFile archivoFirma,
            @RequestParam("claveFirma") String claveFirma,
            @RequestParam("ruc") String ruc,
            @RequestParam("razonSocial") String razonSocial,
            @RequestParam(value = "nombreComercial", required = false) String nombreComercial,
            @RequestParam("dirMatriz") String dirMatriz,
            @RequestParam("dirEstablecimiento") String dirEstablecimiento,
            @RequestParam(value = "codEstablecimiento", defaultValue = "001") String codEstablecimiento,
            @RequestParam(value = "ptoEmision", defaultValue = "001") String ptoEmision,
            @RequestParam(value = "obligadoContabilidad", defaultValue = "NO") String obligadoContabilidad,
            @RequestParam(value = "contribuyenteEspecial", required = false) String contribuyenteEspecial,
            @RequestParam(value = "agenteRetencion", required = false) String agenteRetencion,
            @RequestParam(value = "contribuyenteRimpe", required = false) String contribuyenteRimpe,
            @RequestParam(value = "ambiente", defaultValue = "2") String ambiente,
            @RequestParam(value = "rucProveedorSoftware", required = false) String rucProveedorSoftware,
            // Obligatorio: coloque 0 si nunca ha facturado, o el último número emitido (ej. 2 si ya emitió la 000000002)
            @RequestParam("ultimoSecuencialFactura") Long ultimoSecuencialFactura,
            @RequestParam(value = "ultimoSecuencialNotaCredito", required = false) Long ultimoSecuencialNotaCredito,
            @RequestParam(value = "ultimoSecuencialNotaDebito", required = false) Long ultimoSecuencialNotaDebito,
            @RequestParam(value = "ultimoSecuencialLiquidacion", required = false) Long ultimoSecuencialLiquidacion,
            @RequestParam(value = "ultimoSecuencialGuiaRemision", required = false) Long ultimoSecuencialGuiaRemision,
            @RequestParam(value = "ultimoSecuencialRetencion", required = false) Long ultimoSecuencialRetencion,
            @RequestParam(value = "mailHost", required = false) String mailHost,
            @RequestParam(value = "mailPort", required = false) Integer mailPort,
            @RequestParam(value = "mailUser", required = false) String mailUser,
            @RequestParam(value = "mailPass", required = false) String mailPass) throws Exception {

        if (archivoFirma == null || archivoFirma.isEmpty()) {
            throw new IllegalArgumentException("Debe adjuntar el archivo de firma electrónica (.p12).");
        }
        if (ultimoSecuencialFactura == null || ultimoSecuencialFactura < 0) {
            throw new IllegalArgumentException(
                    "Debe especificar 'ultimoSecuencialFactura' (coloque 0 si es un punto de emisión nuevo, o el último número de factura emitido).");
        }

        byte[] p12Bytes = archivoFirma.getBytes();
        X509Certificate cert = signerService.validarYExtraerCertificado(p12Bytes, claveFirma);

        LocalDateTime fechaVigencia = cert.getNotAfter().toInstant()
                .atZone(ZoneId.of("America/Guayaquil"))
                .toLocalDateTime();
        String propietarioCert = cert.getSubjectX500Principal().getName();

        Emisor emisor = emisorRepository.findByRucAndActivoTrue(ruc.trim())
                .orElseGet(Emisor::new);

        emisor.setRuc(ruc.trim());
        emisor.setRazonSocial(razonSocial.trim());
        emisor.setNombreComercial(nombreComercial != null && !nombreComercial.isBlank() ? nombreComercial.trim() : null);
        emisor.setDirMatriz(dirMatriz.trim());
        emisor.setDirEstablecimiento(dirEstablecimiento.trim());
        emisor.setCodEstablecimiento(codEstablecimiento.trim());
        emisor.setPtoEmision(ptoEmision.trim());
        emisor.setObligadoContabilidad(obligadoContabilidad.trim().toUpperCase());
        emisor.setContribuyenteEspecial(contribuyenteEspecial != null && !contribuyenteEspecial.isBlank() ? contribuyenteEspecial.trim() : null);
        emisor.setAgenteRetencion(agenteRetencion != null && !agenteRetencion.isBlank() ? agenteRetencion.trim() : null);
        emisor.setContribuyenteRimpe(contribuyenteRimpe != null && !contribuyenteRimpe.isBlank() ? contribuyenteRimpe.trim() : null);
        emisor.setAmbiente(ambiente.trim());
        emisor.setTipoEmision("1");
        emisor.setArchivoFirmaP12(p12Bytes);
        emisor.setNombreArchivoFirma(archivoFirma.getOriginalFilename());
        emisor.setClaveFirmaP12(claveFirma);
        emisor.setFechaVigenciaFirma(fechaVigencia);
        emisor.setPropietarioFirma(propietarioCert);
        emisor.setRutaFirmaP12("DB_STORAGE");
        emisor.setRucProveedorSoftware(rucProveedorSoftware != null && !rucProveedorSoftware.isBlank() ? rucProveedorSoftware.trim() : null);

        if (mailHost != null && !mailHost.isBlank()) {
            emisor.setSmtpHost(mailHost.trim());
            emisor.setSmtpPort(mailPort != null ? mailPort : 587);
            emisor.setSmtpUsername(mailUser != null ? mailUser.trim() : null);
            emisor.setSmtpPassword(mailPass != null ? mailPass.replace(" ", "").trim() : null);
            emisor.setSmtpStartTls(true);
        }

        emisor.setActivo(true);
        emisor = emisorRepository.save(emisor);

        guardarOActualizarSecuencial(emisor, "01", ultimoSecuencialFactura);
        guardarOActualizarSecuencial(emisor, "03", ultimoSecuencialLiquidacion);
        guardarOActualizarSecuencial(emisor, "04", ultimoSecuencialNotaCredito);
        guardarOActualizarSecuencial(emisor, "05", ultimoSecuencialNotaDebito);
        guardarOActualizarSecuencial(emisor, "06", ultimoSecuencialGuiaRemision);
        guardarOActualizarSecuencial(emisor, "07", ultimoSecuencialRetencion);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Emisor, firma electrónica y secuenciales configurados exitosamente en PostgreSQL");
        respuesta.put("emisorId", emisor.getId());
        respuesta.put("ruc", emisor.getRuc());
        respuesta.put("razonSocial", emisor.getRazonSocial());
        respuesta.put("establecimiento", emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision());
        respuesta.put("ultimoSecuencialFacturaRegistrado", ultimoSecuencialFactura);
        respuesta.put("siguienteFacturaAEmitir", String.format("%s-%s-%09d",
                emisor.getCodEstablecimiento(), emisor.getPtoEmision(), ultimoSecuencialFactura + 1));
        respuesta.put("ambiente", "2".equals(emisor.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS");
        respuesta.put("archivoFirma", emisor.getNombreArchivoFirma());
        respuesta.put("fechaVigenciaCertificado", emisor.getFechaVigenciaFirma().toString());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Permite definir o actualizar en qué número secuencial va cada tipo de
     * comprobante (Factura, Nota de Crédito, Nota de Débito, Liquidación, Guía
     * de Remisión, Retención).
     */
    @PutMapping("/{ruc}/secuenciales")
    @Transactional
    public ResponseEntity<Map<String, Object>> actualizarSecuenciales(
            @PathVariable String ruc,
            @Valid @RequestBody ConfigurarSecuencialesRequestDto request) {

        Emisor emisor = emisorRepository.findByRucAndActivoTrue(ruc.trim())
                .orElseThrow(() -> new IllegalArgumentException("No existe un emisor activo con el RUC: " + ruc));

        guardarOActualizarSecuencial(emisor, "01", request.getUltimoSecuencialFactura());
        guardarOActualizarSecuencial(emisor, "03", request.getUltimoSecuencialLiquidacionCompra());
        guardarOActualizarSecuencial(emisor, "04", request.getUltimoSecuencialNotaCredito());
        guardarOActualizarSecuencial(emisor, "05", request.getUltimoSecuencialNotaDebito());
        guardarOActualizarSecuencial(emisor, "06", request.getUltimoSecuencialGuiaRemision());
        guardarOActualizarSecuencial(emisor, "07", request.getUltimoSecuencialRetencion());

        return consultarSecuenciales(ruc);
    }

    /**
     * Consulta en qué número secuencial se encuentra actualmente cada tipo de
     * comprobante en PostgreSQL.
     */
    @GetMapping("/{ruc}/secuenciales")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> consultarSecuenciales(@PathVariable String ruc) {
        Emisor emisor = emisorRepository.findByRucAndActivoTrue(ruc.trim())
                .orElseThrow(() -> new IllegalArgumentException("No existe un emisor activo con el RUC: " + ruc));

        List<SecuencialComprobante> lista = secuencialRepository.findByEmisorId(emisor.getId());
        Map<String, Object> detalleSecuenciales = new LinkedHashMap<>();

        for (SecuencialComprobante s : lista) {
            String nombreDoc = switch (s.getCodDoc()) {
                case "01" ->
                    "01_FACTURA";
                case "03" ->
                    "03_LIQUIDACION_COMPRA";
                case "04" ->
                    "04_NOTA_CREDITO";
                case "05" ->
                    "05_NOTA_DEBITO";
                case "06" ->
                    "06_GUIA_REMISION";
                case "07" ->
                    "07_RETENCION";
                default ->
                    s.getCodDoc();
            };
            detalleSecuenciales.put(nombreDoc, Map.of(
                    "ultimoEmitido", s.getUltimoSecuencial(),
                    "siguienteAEmitir", String.format("%s-%s-%09d",
                            s.getCodEstablecimiento(), s.getPtoEmision(), s.getUltimoSecuencial() + 1)
            ));
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("ruc", emisor.getRuc());
        respuesta.put("puntoEmision", emisor.getCodEstablecimiento() + "-" + emisor.getPtoEmision());
        respuesta.put("secuencialesConfigurados", detalleSecuenciales);

        return ResponseEntity.ok(respuesta);
    }

    private void guardarOActualizarSecuencial(Emisor emisor, String codDoc, Long valor) {
        if (valor != null && valor >= 0) {
            SecuencialComprobante sec = secuencialRepository.obtenerParaIncrementar(
                    emisor.getId(), emisor.getCodEstablecimiento(), emisor.getPtoEmision(), codDoc
            ).orElseGet(() -> new SecuencialComprobante(
                    emisor, emisor.getCodEstablecimiento(), emisor.getPtoEmision(), codDoc, 0L
            ));
            sec.setUltimoSecuencial(valor);
            secuencialRepository.save(sec);
        }
    }

    @PutMapping("/{ruc}/smtp")
    @Transactional
    public ResponseEntity<Map<String, Object>> configurarSmtpEmisor(
            @PathVariable String ruc,
            @Valid @RequestBody ConfigurarSmtpRequestDto request) {

        Emisor emisor = emisorRepository.findByRucAndActivoTrue(ruc.trim())
                .orElseThrow(() -> new IllegalArgumentException("No existe un emisor activo con el RUC: " + ruc));

        emisor.setSmtpHost(request.getMailHost().trim());
        emisor.setSmtpPort(request.getMailPort());
        emisor.setSmtpUsername(request.getMailUser().trim());
        emisor.setSmtpPassword(request.getMailPass().replace(" ", "").trim());
        emisor.setSmtpStartTls(request.getUsarStartTls() != null ? request.getUsarStartTls() : true);

        if (request.getProbarConexion() == null || request.getProbarConexion()) {
            emailNotificationService.validarConexionSmtp(emisor);
        }

        emisorRepository.save(emisor);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Servidor de correo SMTP verificado y guardado exitosamente en PostgreSQL.");
        respuesta.put("ruc", emisor.getRuc());
        respuesta.put("mailHost", emisor.getSmtpHost());
        respuesta.put("mailPort", emisor.getSmtpPort());
        respuesta.put("mailUser", emisor.getSmtpUsername());
        respuesta.put("startTls", emisor.getSmtpStartTls());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{ruc}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> consultarEmisor(@PathVariable String ruc) {
        Emisor emisor = emisorRepository.findByRucAndActivoTrue(ruc)
                .orElseThrow(() -> new IllegalArgumentException("No existe un emisor activo con el RUC: " + ruc));

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("emisorId", emisor.getId());
        respuesta.put("ruc", emisor.getRuc());
        respuesta.put("razonSocial", emisor.getRazonSocial());
        respuesta.put("nombreComercial", emisor.getNombreComercial());
        respuesta.put("dirMatriz", emisor.getDirMatriz());
        respuesta.put("dirEstablecimiento", emisor.getDirEstablecimiento());
        respuesta.put("codEstablecimiento", emisor.getCodEstablecimiento());
        respuesta.put("ptoEmision", emisor.getPtoEmision());
        respuesta.put("obligadoContabilidad", emisor.getObligadoContabilidad());
        respuesta.put("contribuyenteRimpe", emisor.getContribuyenteRimpe());
        respuesta.put("ambiente", "2".equals(emisor.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS");
        respuesta.put("firmaCargadaEnBd", emisor.getArchivoFirmaP12() != null && emisor.getArchivoFirmaP12().length > 0);
        respuesta.put("archivoFirma", emisor.getNombreArchivoFirma());
        respuesta.put("propietarioCertificado", emisor.getPropietarioFirma());
        respuesta.put("fechaVigenciaCertificado", emisor.getFechaVigenciaFirma());
        respuesta.put("smtpHost", emisor.getSmtpHost());
        respuesta.put("smtpPort", emisor.getSmtpPort());
        respuesta.put("smtpUsername", emisor.getSmtpUsername());
        respuesta.put("smtpConfigurado", emisor.getSmtpUsername() != null && !emisor.getSmtpUsername().isBlank());

        return ResponseEntity.ok(respuesta);
    }
}
