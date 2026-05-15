package com.product.filthilink.entities.dtos;

public record AuthResponseDto(
        String accessToken,
        com.product.filthilink.entities.models.RefreshToken refreshToken,
        String email
) {
}
