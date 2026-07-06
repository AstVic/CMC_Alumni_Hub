package ru.msu.cmc.alumnihub.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base application exception carrying an HTTP status. Concrete subclasses map
 * to specific status codes and are translated by the global handler.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
