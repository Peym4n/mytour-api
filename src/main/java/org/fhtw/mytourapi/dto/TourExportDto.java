package org.fhtw.mytourapi.dto;

import java.time.Instant;
import java.util.List;

public record TourExportDto(
        Integer schemaVersion,
        Instant exportedAt,
        List<ExportedTourDto> tours
) {
}
