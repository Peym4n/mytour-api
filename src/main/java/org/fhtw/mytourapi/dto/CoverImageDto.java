package org.fhtw.mytourapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadata for a tour cover image stored on the filesystem.")
public record CoverImageDto(
        @Schema(example = "users/1/tours/42/cover.jpg")
        String path,

        @Schema(example = "cover.jpg")
        String originalFilename,

        @Schema(example = "image/jpeg")
        String contentType,

        @Schema(example = "245760")
        Long sizeBytes
) {
}
