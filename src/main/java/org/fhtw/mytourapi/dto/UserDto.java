package org.fhtw.mytourapi.dto;

import java.time.Instant;

public record UserDto(
        Long id,
        String username,
        Instant createdAt
) {
}
