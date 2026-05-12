package org.fhtw.mytourapi.dto;

import java.util.List;

public record ExportedTourDto(
        TourDetailDto tour,
        List<TourLogDto> logs
) {
}
