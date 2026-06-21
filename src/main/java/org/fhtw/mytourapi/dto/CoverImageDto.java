package org.fhtw.mytourapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadata for a tour cover image stored on the filesystem.")
public record CoverImageDto(
        @Schema(example = "covers/9e856ad6-8f76-4422-9e81-08c8f98a8d40-cover.jpg")
        String path,

        @Schema(example = "cover.jpg")
        String originalFilename,

        @Schema(example = "image/jpeg")
        String contentType,

        @Schema(example = "245760")
        Long sizeBytes
) {
}
