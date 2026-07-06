package ru.msu.cmc.alumnihub.common.exception;

import org.springframework.http.HttpStatus;

/** Request conflicts with an existing resource or its current state. */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
