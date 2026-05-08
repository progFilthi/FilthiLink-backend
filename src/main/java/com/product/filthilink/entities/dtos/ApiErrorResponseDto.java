package com.product.filthilink.entities.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponseDto(
        String message,
        String path,
        int statusCode,
        Map<String, String> errors,
        LocalDateTime timestamp
) {
}
