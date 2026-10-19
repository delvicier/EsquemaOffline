package com.facturacion.sri.exception;

public class SriGatewayException extends RuntimeException {

    private final int statusCode;

    public SriGatewayException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public SriGatewayException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
