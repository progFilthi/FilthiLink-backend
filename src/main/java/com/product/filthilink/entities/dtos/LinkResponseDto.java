package com.product.filthilink.entities.dtos;

import com.product.filthilink.entities.models.User;

import java.time.LocalDateTime;

public record LinkResponseDto(
        User user,

        String shortUrl,

        String originalUrl,

        LocalDateTime expiresAt
) {
}
