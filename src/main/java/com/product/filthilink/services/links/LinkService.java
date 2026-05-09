package com.product.filthilink.services.links;

import com.product.filthilink.entities.dtos.CreateLinkRequestDto;
import com.product.filthilink.entities.dtos.LinkResponseDto;
import com.product.filthilink.entities.mappers.LinkMapper;
import com.product.filthilink.entities.models.Link;
import com.product.filthilink.repositories.links.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    private static final String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    @Value("${app.base-url}")
    private String baseUrl;

    public LinkResponseDto createLink(CreateLinkRequestDto dto) {
        System.out.println("Received originalUrl: " + dto.getOriginalUrl());

        String shortCode = generateUniqueShortCode();

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        Link link = linkMapper.toEntity(dto, shortCode, 0, expiresAt);

        Link savedLink = linkRepository.save(link);

        return linkMapper.toResponseDto(savedLink, baseUrl);

    }

    private String generateShortCode() {
        Random random = new SecureRandom();

        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);

        for( int i = 0; i < SHORT_CODE_LENGTH; i++ ) {
            sb.append(base62.charAt(random.nextInt(base62.length())));
        }

        return sb.toString();
    }


    private String generateUniqueShortCode() {

        String shortCode;

        do {
            shortCode = generateShortCode();

        } while (linkRepository.existsByShortCode(shortCode));
            return shortCode;
    }







}
