package org.fhtw.mytourapi.dto;

import java.util.List;

public record ImportResultDto(
        Integer importedTours,
        Integer importedLogs,
        List<Long> createdTourIds
) {
}
