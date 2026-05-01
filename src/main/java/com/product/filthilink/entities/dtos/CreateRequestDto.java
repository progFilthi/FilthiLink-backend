package com.product.filthilink.entities.dtos;

import com.product.filthilink.entities.models.User;

public record CreateRequestDto(
        User user,

        String customSlug,

        String originalUrl

) {
}
