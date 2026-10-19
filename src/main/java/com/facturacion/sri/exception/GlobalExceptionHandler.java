package com.facturacion.sri.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> camposConError = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err
                -> camposConError.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = crearEstructuraBase(
                HttpStatus.BAD_REQUEST,
                "Solicitud Incorrecta - Error de Validación",
                "Uno o más campos enviados en la petición no cumplen con el formato requerido por el SRI.",
                request.getRequestURI()
        );
        body.put("detalles", camposConError);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Formato JSON Inválido",
                "El cuerpo de la petición (Body) no tiene un formato JSON válido o contiene tipos de datos incorrectos (verifique comas, llaves y números decimales).",
                request.getRequestURI()
        );
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<Map<String, Object>> handleMissingParams(
            Exception ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Parámetro o Archivo Obligatorio Faltante",
                "Falta un parámetro o archivo requerido en la petición: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        if (ex.getMessage() != null
                && (ex.getMessage().toLowerCase().contains("no existe")
                || ex.getMessage().toLowerCase().contains("no se encontró"))) {
            return construirRespuesta(
                    HttpStatus.NOT_FOUND,
                    "Recurso No Encontrado",
                    ex.getMessage(),
                    request.getRequestURI()
            );
        }

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Regla de Negocio o Validación Incumplida",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.UNAUTHORIZED,
                "No Autorizado (Unauthorized)",
                "Credenciales de autenticación inválidas o expiradas.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.FORBIDDEN,
                "Acceso Prohibido (Forbidden)",
                "No tiene permisos suficientes para acceder a este recurso.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<Map<String, Object>> handleRouteNotFound(
            Exception ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "Ruta o Endpoint No Encontrado (404)",
                "La dirección URL solicitada no existe en esta API. Verifique la ruta (ej. /api/v1/facturas/emitir).",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "Registro No Encontrado en Base de Datos",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        String metodoUsado = ex.getMethod();
        String metodosPermitidos = ex.getSupportedHttpMethods() != null ? ex.getSupportedHttpMethods().toString() : "Desconocido";

        return construirRespuesta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP No Permitido (405)",
                "El método '" + metodoUsado + "' no está permitido para esta ruta. Métodos soportados: " + metodosPermitidos,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                "Conflicto de Estado o Configuración Faltante (409)",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseConflict(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                "Conflicto de Integridad en Base de Datos (409)",
                "El registro que intenta guardar duplica una clave única existente (RUC, Clave de Acceso o Secuencial ya registrado).",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Archivo Demasiado Grande (413)",
                "El archivo adjunto supera el tamaño máximo permitido por el servidor.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de Contenido No Soportado (415)",
                "El formato Content-Type enviado ('" + ex.getContentType() + "') no es válido para este endpoint. Use 'application/json' o 'multipart/form-data' según corresponda.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(SriGatewayException.class)
    public ResponseEntity<Map<String, Object>> handleSriGatewayException(
            SriGatewayException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }

        String titulo = (status == HttpStatus.GATEWAY_TIMEOUT)
                ? "Tiempo de Espera Agotado con el SRI (504 Gateway Timeout)"
                : "Error de Comunicación con Servidores del SRI (502 Bad Gateway)";

        return construirRespuesta(
                status,
                titulo,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseDown(
            DataAccessResourceFailureException ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio de Base de Datos No Disponible (503)",
                "No se pudo establecer conexión con el servidor PostgreSQL. Verifique que el contenedor de base de datos esté activo.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralErrors(
            Exception ex, HttpServletRequest request) {

        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error Interno del Servidor (500)",
                "Ocurrió un error interno al procesar la solicitud: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus status, String tituloError, String mensaje, String ruta) {

        Map<String, Object> body = crearEstructuraBase(status, tituloError, mensaje, ruta);
        return ResponseEntity.status(status).body(body);
    }

    private Map<String, Object> crearEstructuraBase(
            HttpStatus status, String tituloError, String mensaje, String ruta) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", tituloError);
        body.put("mensaje", mensaje);
        body.put("ruta", ruta);
        return body;
    }
}
