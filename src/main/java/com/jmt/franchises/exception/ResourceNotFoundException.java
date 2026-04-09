package com.jmt.franchises.exception;

/**
 * Excepción que se lanza cuando no se encuentra un recurso solicitado.
 * Mapea al HTTP 404 Not Found en el manejador global de excepciones.
 *
 * Extends RuntimeException para no obligar al llamador a usar try-catch
 * (unchecked exception), lo cual es más idiomático en Spring.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String field, String value) {
        super(String.format("%s not found with %s: '%s'", resource, field, value));
    }
}
