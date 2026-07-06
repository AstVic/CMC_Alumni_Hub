package ru.msu.cmc.alumnihub.common.exception;

import org.springframework.http.HttpStatus;

/** Authenticated but not allowed -> 403. */
public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
