package org.fhtw.mytourapi.exception;

import java.util.List;

public class ImportValidationException extends RuntimeException {

    private final List<String> validationErrors;

    public ImportValidationException(List<String> validationErrors) {
        super("Import validation failed");
        this.validationErrors = List.copyOf(validationErrors);
    }

    public List<String> validationErrors() {
        return validationErrors;
    }
}
