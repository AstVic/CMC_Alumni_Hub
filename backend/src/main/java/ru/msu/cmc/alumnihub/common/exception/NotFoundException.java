package ru.msu.cmc.alumnihub.common.exception;

import org.springframework.http.HttpStatus;

/** Resource does not exist -> 404. */
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
