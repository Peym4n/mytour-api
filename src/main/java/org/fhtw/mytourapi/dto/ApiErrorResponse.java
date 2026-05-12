package org.fhtw.mytourapi.dto;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path,
        List<String> validationErrors
) {
}
