package org.fhtw.mytourapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TourLogWeatherDto(
        Long tourLogId,
        String provider,
        String providerDataset,
        CoordinateDto lookupCoordinate,
        Instant weatherObservedAt,
        BigDecimal temperatureC,
        BigDecimal relativeHumidityPercent,
        BigDecimal precipitationMm,
        Integer weatherCode,
        String weatherDescription,
        BigDecimal windSpeedKmh,
        Instant fetchedAt
) {
}
