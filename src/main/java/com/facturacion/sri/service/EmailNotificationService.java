package com.facturacion.sri.service;

import com.facturacion.sri.entity.Emisor;
import com.facturacion.sri.entity.FacturaEntity;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_AUT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'-05:00'");

    private final RidePdfGeneratorService ridePdfGeneratorService;

    public EmailNotificationService(RidePdfGeneratorService ridePdfGeneratorService) {
        this.ridePdfGeneratorService = ridePdfGeneratorService;
    }

    private JavaMailSenderImpl construirMailSenderDinamico(Emisor emisor) {
        if (emisor.getSmtpHost() == null || emisor.getSmtpHost().isBlank() ||
            emisor.getSmtpUsername() == null || emisor.getSmtpUsername().isBlank() ||
            emisor.getSmtpPassword() == null || emisor.getSmtpPassword().isBlank()) {
            throw new IllegalStateException(
                    "El emisor con RUC " + emisor.getRuc() + " aún no tiene configurado un servidor de correo SMTP. " +
                    "Configúrelo en PUT /api/v1/emisores/" + emisor.getRuc() + "/smtp");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emisor.getSmtpHost().trim());
        mailSender.setPort(emisor.getSmtpPort() != null ? emisor.getSmtpPort() : 587);
        mailSender.setUsername(emisor.getSmtpUsername().trim());
        mailSender.setPassword(emisor.getSmtpPassword().replace(" ", "").trim());
        mailSender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        boolean usarStartTls = emisor.getSmtpStartTls() == null || emisor.getSmtpStartTls();
        props.put("mail.smtp.starttls.enable", String.valueOf(usarStartTls));
        props.put("mail.smtp.starttls.required", String.valueOf(usarStartTls));

        if (mailSender.getPort() == 465) {
            props.put("mail.smtp.ssl.enable", "true");
        }

        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        return mailSender;
    }

    public void validarConexionSmtp(Emisor emisor) {
        try {
            JavaMailSenderImpl sender = construirMailSenderDinamico(emisor);
            sender.testConnection();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Fallo al autenticar con el servidor SMTP (" + emisor.getSmtpHost() + ":" + emisor.getSmtpPort() + "): " + e.getMessage());
        }
    }

    public void enviarComprobantePorEmail(FacturaEntity factura, String emailDestino) {
        if (emailDestino == null || emailDestino.isBlank()) {
            log.warn("No se envió correo para la factura {} porque el comprador no tiene email registrado.", factura.getClaveAcceso());
            return;
        }

        Emisor emisor = factura.getEmisor();
        JavaMailSenderImpl mailSender = construirMailSenderDinamico(emisor);

        try {
            String numeroFactura = String.format("%s-%s-%s",
                    emisor.getCodEstablecimiento(), emisor.getPtoEmision(), factura.getSecuencial());

            String xmlAutorizadoOficial = construirXmlConNodoAutorizacion(factura);
            byte[] xmlBytes = xmlAutorizadoOficial.getBytes(StandardCharsets.UTF_8);
            byte[] pdfRideBytes = ridePdfGeneratorService.generarRideFacturaPdf(factura);

            String nombreArchivoXml = "FAC-" + numeroFactura + ".xml";
            String nombreArchivoPdf = "RIDE-" + numeroFactura + ".pdf";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(emisor.getSmtpUsername().trim(), emisor.getRazonSocial());
            helper.setTo(emailDestino.trim());
            helper.setSubject("Comprobante Electrónico Autorizado - Factura Nro. " + numeroFactura + " - " + emisor.getRazonSocial());
            helper.setText(construirCuerpoHtml(factura, numeroFactura), true);

            helper.addAttachment(nombreArchivoXml, new ByteArrayResource(xmlBytes), "application/xml");
            helper.addAttachment(nombreArchivoPdf, new ByteArrayResource(pdfRideBytes), "application/pdf");

            mailSender.send(message);
            log.info(">>> Correo enviado exitosamente desde {} hacia {} con adjuntos {} y {}",
                    emisor.getSmtpUsername(), emailDestino, nombreArchivoXml, nombreArchivoPdf);

        } catch (Exception e) {
            log.error("Error al enviar el comprobante por correo a {}: {}", emailDestino, e.getMessage());
            throw new RuntimeException("Error al enviar correo electrónico: " + e.getMessage(), e);
        }
    }

    private String construirXmlConNodoAutorizacion(FacturaEntity factura) {
        String ambienteTexto = "2".equals(factura.getEmisor().getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS";
        String fechaAut = factura.getFechaAutorizacion() != null
                ? factura.getFechaAutorizacion().format(FORMATO_FECHA_AUT)
                : "";

        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <autorizacion>
                    <estado>%s</estado>
                    <numeroAutorizacion>%s</numeroAutorizacion>
                    <fechaAutorizacion>%s</fechaAutorizacion>
                    <ambiente>%s</ambiente>
                    <comprobante><![CDATA[%s]]></comprobante>
                </autorizacion>
                """.formatted(
                factura.getEstadoSri(),
                factura.getClaveAcceso(),
                fechaAut,
                ambienteTexto,
                factura.getXmlFirmado()
        );
    }

    private String construirCuerpoHtml(FacturaEntity factura, String numeroFactura) {
        Emisor emisor = factura.getEmisor();
        String totalFormateado = factura.getImporteTotal().setScale(2, RoundingMode.HALF_UP).toString();

        return """
                <div style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 6px; overflow: hidden;">
                    <div style="background-color: #19417d; color: #ffffff; padding: 16px; text-align: center;">
                        <h2 style="margin: 0; font-size: 18px;">%s</h2>
                        <p style="margin: 4px 0 0 0; font-size: 13px;">Notificación de Comprobante Electrónico SRI</p>
                    </div>
                    <div style="padding: 20px;">
                        <p>Estimado(a) <strong>%s</strong>,</p>
                        <p>Le informamos que se ha emitido su comprobante electrónico autorizado por el Servicio de Rentas Internas (SRI). Adjunto a este correo encontrará por separado su archivo <strong>.XML</strong> y su representación impresa <strong>.PDF (RIDE)</strong>.</p>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 15px; font-size: 13px;">
                            <tr style="background-color: #f5f7fa;">
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Tipo de Comprobante:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd;">FACTURA</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Número de Comprobante:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                            </tr>
                            <tr style="background-color: #f5f7fa;">
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Clave de Acceso / Autorización:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd; word-break: break-all;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Fecha de Emisión:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                            </tr>
                            <tr style="background-color: #f5f7fa;">
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Valor Total:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>$ %s USD</strong></td>
                            </tr>
                        </table>
                    </div>
                </div>
                """.formatted(
                emisor.getRazonSocial(),
                factura.getRazonSocialComprador(),
                numeroFactura,
                factura.getClaveAcceso(),
                factura.getFechaEmision().format(FORMATO_FECHA),
                totalFormateado
        );
    }
}