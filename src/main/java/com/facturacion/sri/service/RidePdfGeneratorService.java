package com.facturacion.sri.service;

import com.facturacion.sri.entity.Emisor;
import com.facturacion.sri.entity.FacturaDetalleEntity;
import com.facturacion.sri.entity.FacturaEntity;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class RidePdfGeneratorService {

    private static final Font FONT_TITULO_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
    private static final Font FONT_SUBTITULO_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
    private static final Font FONT_PEQUENA = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.BLACK);
    private static final Font FONT_TABLA_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, Color.WHITE);

    private static final Color COLOR_HEADER_TABLA = new Color(25, 65, 125);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Map<String, String> CATALOGO_FORMAS_PAGO = Map.of(
            "01", "01 - SIN UTILIZACION DEL SISTEMA FINANCIERO",
            "15", "15 - COMPENSACION DE DEUDAS",
            "16", "16 - TARJETA DE DEBITO",
            "17", "17 - DINERO ELECTRONICO",
            "18", "18 - TARJETA PREPAGO",
            "19", "19 - TARJETA DE CREDITO",
            "20", "20 - OTROS CON UTILIZACION DEL SISTEMA FINANCIERO",
            "21", "21 - ENDOSO DE TITULOS"
    );

    public byte[] generarRideFacturaPdf(FacturaEntity factura) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 25, 25, 25, 25);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            Emisor emisor = factura.getEmisor();
            String numeroFactura = String.format("%s-%s-%s",
                    emisor.getCodEstablecimiento(), emisor.getPtoEmision(), factura.getSecuencial());


            PdfPTable tablaCabecera = new PdfPTable(2);
            tablaCabecera.setWidthPercentage(100);
            tablaCabecera.setWidths(new float[]{48f, 52f});
            tablaCabecera.setSpacingAfter(10f);

            PdfPCell celdaIzquierda = new PdfPCell();
            celdaIzquierda.setBorder(Rectangle.BOX);
            celdaIzquierda.setPadding(8f);

            Paragraph pRazonSocial = new Paragraph(emisor.getRazonSocial(), FONT_TITULO_BOLD);
            pRazonSocial.setSpacingAfter(6f);
            celdaIzquierda.addElement(pRazonSocial);

            if (emisor.getNombreComercial() != null && !emisor.getNombreComercial().isBlank()) {
                celdaIzquierda.addElement(crearLineaTexto("Nombre Comercial: ", emisor.getNombreComercial()));
            }
            celdaIzquierda.addElement(crearLineaTexto("Dirección Matriz: ", emisor.getDirMatriz()));
            celdaIzquierda.addElement(crearLineaTexto("Dirección Sucursal: ", emisor.getDirEstablecimiento()));

            if (emisor.getContribuyenteEspecial() != null && !emisor.getContribuyenteEspecial().isBlank()) {
                celdaIzquierda.addElement(crearLineaTexto("Contribuyente Especial Nro: ", emisor.getContribuyenteEspecial()));
            }
            celdaIzquierda.addElement(crearLineaTexto("OBLIGADO A LLEVAR CONTABILIDAD: ", emisor.getObligadoContabilidad()));

            if (emisor.getAgenteRetencion() != null && !emisor.getAgenteRetencion().isBlank()) {
                celdaIzquierda.addElement(crearLineaTexto("Agente de Retención Resolución No.: ", emisor.getAgenteRetencion()));
            }
            if (emisor.getContribuyenteRimpe() != null && !emisor.getContribuyenteRimpe().isBlank()) {
                Paragraph pRimpe = new Paragraph(emisor.getContribuyenteRimpe(), FONT_SUBTITULO_BOLD);
                pRimpe.setSpacingBefore(4f);
                celdaIzquierda.addElement(pRimpe);
            }


            PdfPCell celdaDerecha = new PdfPCell();
            celdaDerecha.setBorder(Rectangle.BOX);
            celdaDerecha.setPadding(8f);

            celdaDerecha.addElement(crearLineaTexto("R.U.C.: ", emisor.getRuc()));
            Paragraph pTituloDoc = new Paragraph("FACTURA", FONT_TITULO_BOLD);
            pTituloDoc.setSpacingBefore(4f);
            pTituloDoc.setSpacingAfter(4f);
            celdaDerecha.addElement(pTituloDoc);

            celdaDerecha.addElement(crearLineaTexto("No. ", numeroFactura));
            celdaDerecha.addElement(new Paragraph("NÚMERO DE AUTORIZACIÓN", FONT_SUBTITULO_BOLD));
            celdaDerecha.addElement(new Paragraph(factura.getClaveAcceso(), FONT_PEQUENA));

            String fechaAutTexto = factura.getFechaAutorizacion() != null
                    ? factura.getFechaAutorizacion().format(FORMATO_FECHA_HORA)
                    : "PENDIENTE";
            celdaDerecha.addElement(crearLineaTexto("FECHA Y HORA DE AUTORIZACIÓN: ", fechaAutTexto));
            celdaDerecha.addElement(crearLineaTexto("AMBIENTE: ", "2".equals(emisor.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS"));
            celdaDerecha.addElement(crearLineaTexto("EMISIÓN: ", "NORMAL"));

            Paragraph pClaveLabel = new Paragraph("CLAVE DE ACCESO", FONT_SUBTITULO_BOLD);
            pClaveLabel.setSpacingBefore(4f);
            celdaDerecha.addElement(pClaveLabel);

            Barcode128 barcode = new Barcode128();
            barcode.setCode(factura.getClaveAcceso());
            barcode.setCodeType(Barcode.CODE128);
            barcode.setBarHeight(28f);
            barcode.setFont(null);
            Image imgBarcode = barcode.createImageWithBarcode(writer.getDirectContent(), Color.BLACK, Color.BLACK);
            imgBarcode.scalePercent(100f);
            imgBarcode.setSpacingBefore(3f);
            celdaDerecha.addElement(imgBarcode);

            Paragraph pClaveTexto = new Paragraph(factura.getClaveAcceso(), FONT_PEQUENA);
            pClaveTexto.setAlignment(Element.ALIGN_CENTER);
            celdaDerecha.addElement(pClaveTexto);

            tablaCabecera.addCell(celdaIzquierda);
            tablaCabecera.addCell(celdaDerecha);
            document.add(tablaCabecera);


            PdfPTable tablaComprador = new PdfPTable(2);
            tablaComprador.setWidthPercentage(100);
            tablaComprador.setWidths(new float[]{65f, 35f});
            tablaComprador.setSpacingAfter(10f);

            PdfPCell celdaCompIzq = new PdfPCell();
            celdaCompIzq.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
            celdaCompIzq.setPadding(6f);
            celdaCompIzq.addElement(crearLineaTexto("Razón Social / Nombres y Apellidos: ", factura.getRazonSocialComprador()));
            celdaCompIzq.addElement(crearLineaTexto("Fecha Emisión: ", factura.getFechaEmision().format(FORMATO_FECHA)));
            if (factura.getDireccionComprador() != null && !factura.getDireccionComprador().isBlank()) {
                celdaCompIzq.addElement(crearLineaTexto("Dirección: ", factura.getDireccionComprador()));
            }

            PdfPCell celdaCompDer = new PdfPCell();
            celdaCompDer.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
            celdaCompDer.setPadding(6f);
            celdaCompDer.addElement(crearLineaTexto("Identificación: ", factura.getIdentificacionComprador()));

            tablaComprador.addCell(celdaCompIzq);
            tablaComprador.addCell(celdaCompDer);
            document.add(tablaComprador);


            PdfPTable tablaDetalles = new PdfPTable(6);
            tablaDetalles.setWidthPercentage(100);
            tablaDetalles.setWidths(new float[]{14f, 12f, 38f, 12f, 12f, 12f});
            tablaDetalles.setSpacingAfter(10f);

            agregarHeaderCelda(tablaDetalles, "Cod. Principal");
            agregarHeaderCelda(tablaDetalles, "Cantidad");
            agregarHeaderCelda(tablaDetalles, "Descripción");
            agregarHeaderCelda(tablaDetalles, "Precio Unitario");
            agregarHeaderCelda(tablaDetalles, "Descuento");
            agregarHeaderCelda(tablaDetalles, "Precio Total");

            BigDecimal subtotal15 = BigDecimal.ZERO;
            BigDecimal subtotal5 = BigDecimal.ZERO;
            BigDecimal subtotal0 = BigDecimal.ZERO;
            BigDecimal subtotalNoObjeto = BigDecimal.ZERO;
            BigDecimal subtotalExento = BigDecimal.ZERO;
            BigDecimal iva15 = BigDecimal.ZERO;
            BigDecimal iva5 = BigDecimal.ZERO;

            for (FacturaDetalleEntity det : factura.getDetalles()) {
                agregarCeldaDetalle(tablaDetalles, det.getCodigoPrincipal(), Element.ALIGN_LEFT);
                agregarCeldaDetalle(tablaDetalles, det.getCantidad().setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT);
                agregarCeldaDetalle(tablaDetalles, det.getDescripcion(), Element.ALIGN_LEFT);
                agregarCeldaDetalle(tablaDetalles, det.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT);
                agregarCeldaDetalle(tablaDetalles, det.getDescuento().setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT);
                agregarCeldaDetalle(tablaDetalles, det.getPrecioTotalSinImpuesto().setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT);

                String codPorc = det.getCodigoPorcentajeIva();
                BigDecimal base = det.getPrecioTotalSinImpuesto();
                BigDecimal valIva = det.getValorIva();

                if ("4".equals(codPorc)) {
                    subtotal15 = subtotal15.add(base);
                    iva15 = iva15.add(valIva);
                } else if ("5".equals(codPorc)) {
                    subtotal5 = subtotal5.add(base);
                    iva5 = iva5.add(valIva);
                } else if ("0".equals(codPorc)) {
                    subtotal0 = subtotal0.add(base);
                } else if ("6".equals(codPorc)) {
                    subtotalNoObjeto = subtotalNoObjeto.add(base);
                } else if ("7".equals(codPorc)) {
                    subtotalExento = subtotalExento.add(base);
                }
            }
            document.add(tablaDetalles);


            PdfPTable tablaInferior = new PdfPTable(2);
            tablaInferior.setWidthPercentage(100);
            tablaInferior.setWidths(new float[]{58f, 42f});

            PdfPCell contenedorIzq = new PdfPCell();
            contenedorIzq.setBorder(Rectangle.NO_BORDER);
            contenedorIzq.setPaddingRight(10f);

            PdfPTable tablaInfoAdicional = new PdfPTable(1);
            tablaInfoAdicional.setWidthPercentage(100);
            tablaInfoAdicional.setSpacingAfter(8f);
            agregarHeaderCelda(tablaInfoAdicional, "Información Adicional");

            PdfPCell celdaInfo = new PdfPCell();
            celdaInfo.setPadding(6f);
            if (factura.getEmailComprador() != null && !factura.getEmailComprador().isBlank()) {
                celdaInfo.addElement(crearLineaTexto("Email: ", factura.getEmailComprador()));
            }
            celdaInfo.addElement(crearLineaTexto("Estado SRI: ", factura.getEstadoSri()));
            tablaInfoAdicional.addCell(celdaInfo);
            contenedorIzq.addElement(tablaInfoAdicional);

            PdfPTable tablaPagos = new PdfPTable(2);
            tablaPagos.setWidthPercentage(100);
            tablaPagos.setWidths(new float[]{75f, 25f});
            agregarHeaderCelda(tablaPagos, "Forma de Pago");
            agregarHeaderCelda(tablaPagos, "Valor");

            String descFormaPago = CATALOGO_FORMAS_PAGO.getOrDefault(
                    factura.getFormaPago(), factura.getFormaPago() + " - OTROS");
            agregarCeldaDetalle(tablaPagos, descFormaPago, Element.ALIGN_LEFT);
            agregarCeldaDetalle(tablaPagos, factura.getImporteTotal().setScale(2, RoundingMode.HALF_UP).toString(), Element.ALIGN_RIGHT);
            contenedorIzq.addElement(tablaPagos);

            PdfPCell contenedorDer = new PdfPCell();
            contenedorDer.setBorder(Rectangle.NO_BORDER);

            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(100);
            tablaTotales.setWidths(new float[]{68f, 32f});

            agregarFilaTotal(tablaTotales, "SUBTOTAL 15%", subtotal15);
            agregarFilaTotal(tablaTotales, "SUBTOTAL 5%", subtotal5);
            agregarFilaTotal(tablaTotales, "SUBTOTAL 0%", subtotal0);
            agregarFilaTotal(tablaTotales, "SUBTOTAL NO OBJETO DE IVA", subtotalNoObjeto);
            agregarFilaTotal(tablaTotales, "SUBTOTAL EXENTO DE IVA", subtotalExento);
            agregarFilaTotal(tablaTotales, "SUBTOTAL SIN IMPUESTOS", factura.getTotalSinImpuestos());
            agregarFilaTotal(tablaTotales, "TOTAL DESCUENTO", factura.getTotalDescuento());
            agregarFilaTotal(tablaTotales, "IVA 15%", iva15);
            agregarFilaTotal(tablaTotales, "IVA 5%", iva5);
            agregarFilaTotal(tablaTotales, "VALOR TOTAL", factura.getImporteTotal());

            contenedorDer.addElement(tablaTotales);

            tablaInferior.addCell(contenedorIzq);
            tablaInferior.addCell(contenedorDer);
            document.add(tablaInferior);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF RIDE del comprobante: " + e.getMessage(), e);
        }
    }

    private Paragraph crearLineaTexto(String etiqueta, String valor) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(etiqueta, FONT_SUBTITULO_BOLD));
        p.add(new Chunk(valor != null ? valor : "", FONT_NORMAL));
        p.setSpacingAfter(2f);
        return p;
    }

    private void agregarHeaderCelda(PdfPTable tabla, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_TABLA_HEADER));
        cell.setBackgroundColor(COLOR_HEADER_TABLA);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4f);
        tabla.addCell(cell);
    }

    private void agregarCeldaDetalle(PdfPTable tabla, String texto, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", FONT_PEQUENA));
        cell.setHorizontalAlignment(alineacion);
        cell.setPadding(4f);
        tabla.addCell(cell);
    }

    private void agregarFilaTotal(PdfPTable tabla, String etiqueta, BigDecimal valor) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(etiqueta, FONT_SUBTITULO_BOLD));
        cellLabel.setPadding(3.5f);
        tabla.addCell(cellLabel);

        PdfPCell cellVal = new PdfPCell(new Phrase(
                (valor != null ? valor : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP).toString(), FONT_NORMAL));
        cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellVal.setPadding(3.5f);
        tabla.addCell(cellVal);
    }
}
