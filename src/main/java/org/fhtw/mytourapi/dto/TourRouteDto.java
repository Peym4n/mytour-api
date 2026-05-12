package org.fhtw.mytourapi.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Route data generated from OpenRouteService and displayed by Leaflet.")
public record TourRouteDto(
        @Schema(example = "OPENROUTESERVICE")
        String routeSource,

        @Schema(example = "cycling-regular")
        String routeProfile,

        CoordinateDto startCoordinate,
        CoordinateDto endCoordinate,
        CoordinateDto midpointCoordinate,

        @Schema(description = "OpenRouteService GeoJSON stored as PostgreSQL jsonb.")
        JsonNode routeGeometry,

        Instant routeFetchedAt
) {
}
