package org.fhtw.mytourapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TourSummaryDto(
        Long id,
        Long userId,
        String name,
        String startLocation,
        String endLocation,
        TransportType transportType,
        String timezoneId,
        BigDecimal plannedDistanceM,
        Integer estimatedDurationS,
        CoverImageDto coverImage,
        ComputedTourAttributesDto computedAttributes,
        Instant createdAt,
        Instant updatedAt
) {
}
