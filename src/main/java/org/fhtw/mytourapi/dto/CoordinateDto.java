package org.fhtw.mytourapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Geographic coordinate in WGS84 decimal degrees.")
public record CoordinateDto(
        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        @Schema(example = "48.2082")
        BigDecimal latitude,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        @Schema(example = "16.3738")
        BigDecimal longitude
) {
}
