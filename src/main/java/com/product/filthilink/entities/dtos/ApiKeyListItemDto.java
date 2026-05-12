package com.product.filthilink.entities.dtos;

import java.time.LocalDateTime;

public record ApiKeyListItemDto(
        Long id,
        String name,
        String keyPrefix,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt
) {
}
