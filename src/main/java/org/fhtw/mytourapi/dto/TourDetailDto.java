package org.fhtw.mytourapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TourDetailDto(
        Long id,
        Long userId,
        String name,
        String description,
        String startLocation,
        String endLocation,
        TransportType transportType,
        String timezoneId,
        BigDecimal plannedDistanceM,
        Integer estimatedDurationS,
        CoverImageDto coverImage,
        TourRouteDto route,
        ComputedTourAttributesDto computedAttributes,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
}
