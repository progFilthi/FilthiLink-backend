package com.product.filthilink.entities.dtos;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String email
) {
}
