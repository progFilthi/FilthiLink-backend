package com.product.filthilink.entities.dtos;

import java.time.LocalDateTime;

public record LinkResponseDto(
        String shortUrl,
        String originalUrl,
        Integer clickCount,
        LocalDateTime expiresAt
){}
