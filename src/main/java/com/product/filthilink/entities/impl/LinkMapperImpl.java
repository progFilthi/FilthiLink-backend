package com.product.filthilink.entities.impl;

import com.product.filthilink.entities.dtos.CreateLinkRequestDto;
import com.product.filthilink.entities.dtos.LinkResponseDto;
import com.product.filthilink.entities.mappers.LinkMapper;
import com.product.filthilink.entities.models.Link;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LinkMapperImpl implements LinkMapper {



    @Override
    public Link toEntity(CreateLinkRequestDto dto, String shortCode, Integer clickCount, LocalDateTime expiresAt) {
        Link link = new Link();
        link.setOriginalUrl(dto.originalUrl());
        link.setShortCode(shortCode);
        link.setClickCount(clickCount);
        link.setExpiresAt(expiresAt);

        return link;
    }

    @Override
    public LinkResponseDto toResponseDto(Link link, String baseUrl) {
        return new LinkResponseDto(
                link.getId(),
                baseUrl + "/" +  link.getShortCode(),
                link.getOriginalUrl(),
                link.getClickCount(),
                link.getExpiresAt()
        );
    }
}
