package org.fhtw.mytourapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTourRequest(
        @NotBlank
        @Size(max = 120)
        String name,

        @Size(max = 5000)
        String description,

        @NotBlank
        @Size(max = 255)
        String startLocation,

        @NotBlank
        @Size(max = 255)
        String endLocation,

        @NotNull
        TransportType transportType,

        @NotBlank
        @Size(max = 64)
        String timezoneId,

        @NotNull
        @Valid
        CoordinateDto startCoordinate,

        @NotNull
        @Valid
        CoordinateDto endCoordinate,

        @NotNull
        Long version
) {
}
