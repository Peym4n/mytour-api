package org.fhtw.mytourapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TourLogDto(
        Long id,
        Long tourId,
        Instant performedAt,
        String comment,
        Short difficulty,
        BigDecimal totalDistanceM,
        Integer totalTimeS,
        Short rating,
        TourLogWeatherDto weather,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
}
