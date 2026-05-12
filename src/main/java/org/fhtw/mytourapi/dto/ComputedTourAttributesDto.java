package org.fhtw.mytourapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Stored computed tour attributes used for display, sorting, filtering, and search.")
public record ComputedTourAttributesDto(
        @Schema(example = "4")
        Integer logCount,

        @Schema(example = "4")
        Integer popularityScore,

        PopularityCategory popularityCategory,

        @Schema(example = "popular")
        String popularityLabel,

        @Schema(example = "82")
        Integer childFriendlinessScore,

        ChildFriendlinessCategory childFriendlinessCategory,

        @Schema(example = "family friendly")
        String childFriendlinessLabel
) {
}
