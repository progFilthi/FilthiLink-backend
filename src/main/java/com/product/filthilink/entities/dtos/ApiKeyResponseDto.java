package com.product.filthilink.entities.dtos;

import java.time.LocalDateTime;

public record ApiKeyResponseDto(
        Long id,
        String name,
        String keyPrefix,
        String rawKey, // this is returned once: on creation.
        LocalDateTime createdAt

) {
}
