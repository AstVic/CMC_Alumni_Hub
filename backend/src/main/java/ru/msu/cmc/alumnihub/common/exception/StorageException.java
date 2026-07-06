package ru.msu.cmc.alumnihub.common.exception;

import org.springframework.http.HttpStatus;

/** File storage operation failed without exposing filesystem details. */
public class StorageException extends ApiException {

    public StorageException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        initCause(cause);
    }
}
