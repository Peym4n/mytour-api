package org.fhtw.mytourapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTourLogRequest(
        @NotNull
        Instant performedAt,

        @Size(max = 5000)
        String comment,

        @NotNull
        @Min(1)
        @Max(5)
        Short difficulty,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal totalDistanceM,

        @NotNull
        @Positive
        Integer totalTimeS,

        @NotNull
        @Min(1)
        @Max(5)
        Short rating
) {
}
