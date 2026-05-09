package com.product.filthilink.entities.mappers;

import com.product.filthilink.entities.dtos.CreateLinkRequestDto;
import com.product.filthilink.entities.dtos.LinkResponseDto;
import com.product.filthilink.entities.models.Link;

import java.time.LocalDateTime;

public interface LinkMapper {

    Link toEntity(CreateLinkRequestDto dto, String shortCode, Integer clickCount, LocalDateTime expiresAt);
    LinkResponseDto toResponseDto(Link link, String baseUrl);


}
