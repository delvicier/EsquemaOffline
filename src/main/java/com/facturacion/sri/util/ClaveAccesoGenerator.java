package com.facturacion.sri.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ClaveAccesoGenerator {

    private static final DateTimeFormatter FORMATO_FECHA_CLAVE = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera la clave de acceso de 49 dígitos
     *
     * @param fechaEmision       Fecha de emisión del comprobante
     * @param tipoComprobante    "01" para Factura (Tabla 3)
     * @param ruc                RUC del emisor de 13 dígitos
     * @param ambiente           "1" Pruebas, "2" Producción (Tabla 4)
     * @param establecimiento    Código de 3 dígitos (ej. "001")
     * @param puntoEmision       Código de 3 dígitos (ej. "001")
     * @param secuencial         Número secuencial de 9 dígitos (ej. "000000001")
     * @param tipoEmision        "1" para Emisión Normal (Tabla 2)
     * @return Clave de acceso de 49 dígitos numéricos con dígito verificador Módulo 11
     */
    public String generarClaveAcceso(
            LocalDate fechaEmision,
            String tipoComprobante,
            String ruc,
            String ambiente,
            String establecimiento,
            String puntoEmision,
            String secuencial,
            String tipoEmision) {

        String fechaFormateada = fechaEmision.format(FORMATO_FECHA_CLAVE); // 8 dígitos (ddmmaaaa)
        String serie = formatearConCeros(establecimiento, 3) + formatearConCeros(puntoEmision, 3); // 6 dígitos
        String secFormateado = formatearConCeros(secuencial, 9); // 9 dígitos
        String codigoNumerico = generarCodigoNumerico8Digitos(); // 8 dígitos autogenerados

        // Construir los primeros 48 dígitos
        StringBuilder clave48 = new StringBuilder(48);
        clave48.append(fechaFormateada)   // 8 dígitos
               .append(tipoComprobante)   // 2 dígitos
               .append(ruc)               // 13 dígitos
               .append(ambiente)          // 1 dígito
               .append(serie)             // 6 dígitos
               .append(secFormateado)     // 9 dígitos
               .append(codigoNumerico)    // 8 dígitos
               .append(tipoEmision);      // 1 dígito

        if (clave48.length() != 48 || !clave48.toString().matches("\\d{48}")) {
            throw new IllegalArgumentException("La estructura base de la clave de acceso debe tener exactamente 48 dígitos numéricos. Valor generado: " + clave48);
        }

        int digitoVerificador = calcularModulo11(clave48.toString());
        return clave48.append(digitoVerificador).toString();
    }

    /**
     * Calcula el dígito verificador usando el algoritmo Módulo 11 con factor ponderado de 2 a 7.
     * Regla SRI: Si el resultado es 11 -> retorna 0. Si el resultado es 10 -> retorna 1.
     */
    public int calcularModulo11(String cadena48Digitos) {
        int factor = 2;
        int suma = 0;

        // Se recorre de derecha a izquierda multiplicando por el factor (2 a 7)
        for (int i = cadena48Digitos.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(cadena48Digitos.charAt(i));
            suma += digito * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }

        int residuo = suma % 11;
        int resultado = 11 - residuo;

        if (resultado == 11) {
            return 0;
        } else if (resultado == 10) {
            return 1;
        }
        return resultado;
    }

    private String generarCodigoNumerico8Digitos() {
        int numero = 10000000 + RANDOM.nextInt(90000000);
        return String.valueOf(numero);
    }

    public static String formatearConCeros(String valor, int longitud) {
        if (valor == null) {
            throw new IllegalArgumentException("El valor a formatear no puede ser nulo");
        }
        return String.format("%" + longitud + "s", valor.trim()).replace(' ', '0');
    }
}