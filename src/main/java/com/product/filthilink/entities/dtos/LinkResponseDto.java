package com.product.filthilink.entities.dtos;

import java.time.LocalDateTime;

public record LinkResponseDto(
        long id,
        String shortUrl,
        String originalUrl,
        Integer clickCount,
        LocalDateTime expiresAt
){}
