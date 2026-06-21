package org.fhtw.mytourapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ImportedTourDto(
        @NotNull
        @Valid
        CreateTourRequest tour,

        @NotNull
        @Valid
        TourRouteDto route,

        @Valid
        CoverImageDto coverImage,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal plannedDistanceM,

        @NotNull
        @Positive
        Integer estimatedDurationS,

        @NotNull
        List<@Valid ImportedTourLogDto> logs
) {
}
