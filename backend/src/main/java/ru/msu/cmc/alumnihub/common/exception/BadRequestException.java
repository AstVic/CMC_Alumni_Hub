package ru.msu.cmc.alumnihub.common.exception;

import org.springframework.http.HttpStatus;

/** Invalid input or violated business rule -> 400. */
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
