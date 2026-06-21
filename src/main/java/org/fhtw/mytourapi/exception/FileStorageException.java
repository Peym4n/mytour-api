package org.fhtw.mytourapi.exception;

import org.springframework.http.HttpStatus;

public class FileStorageException extends RuntimeException {

    private final HttpStatus status;

    public FileStorageException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public FileStorageException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }

    public static FileStorageException badRequest(String message) {
        return new FileStorageException(HttpStatus.BAD_REQUEST, message);
    }

    public static FileStorageException internal(String message, Throwable cause) {
        return new FileStorageException(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}
