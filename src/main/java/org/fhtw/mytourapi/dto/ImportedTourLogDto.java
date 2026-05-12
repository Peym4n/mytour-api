package org.fhtw.mytourapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ImportedTourLogDto(
        @NotNull
        @Valid
        CreateTourLogRequest log,

        @Valid
        ImportedWeatherSnapshotDto weather
) {
}
