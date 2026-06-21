package org.fhtw.mytourapi.exception;

import org.fhtw.mytourapi.dto.ApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorResponseFactoryTest {

    private final ApiErrorResponseFactory factory = new ApiErrorResponseFactory();

    @Test
    void createBuildsConsistentApiErrorResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/tours");
        List<String> validationErrors = List.of("name: must not be blank");

        ResponseEntity<ApiErrorResponse> response = factory.create(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                validationErrors
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().path()).isEqualTo("/api/tours");
        assertThat(response.getBody().validationErrors()).containsExactly("name: must not be blank");
    }
}
