package org.fhtw.mytourapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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

        @NotEmpty
        List<@Valid ImportedTourLogDto> logs
) {
}
