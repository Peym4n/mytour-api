package org.fhtw.mytourapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.fhtw.mytourapi.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ApiErrorResponseFactory {

    public ResponseEntity<ApiErrorResponse> create(
            HttpStatusCode statusCode,
            String message,
            HttpServletRequest request,
            List<String> validationErrors
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                statusCode.value(),
                reasonPhrase(statusCode),
                message,
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(statusCode).body(response);
    }

    String reasonPhrase(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status == null ? "HTTP " + statusCode.value() : status.getReasonPhrase();
    }
}
