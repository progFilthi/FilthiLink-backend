package com.product.filthilink.entities.dtos;

import java.time.LocalDateTime;

public record LinkResponseDto(
        String shortCode,
        String originalUrl,
        Integer clickCount,
        LocalDateTime expiresAt
){}
