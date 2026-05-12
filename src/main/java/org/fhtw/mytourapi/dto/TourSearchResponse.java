package org.fhtw.mytourapi.dto;

import java.util.List;

public record TourSearchResponse(
        List<TourSummaryDto> tours,
        Integer totalCount
) {
}
