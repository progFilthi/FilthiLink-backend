package com.product.filthilink.services.links;

import com.product.filthilink.entities.dtos.CreateLinkRequestDto;
import com.product.filthilink.entities.dtos.LinkResponseDto;
import com.product.filthilink.entities.mappers.LinkMapper;
import com.product.filthilink.entities.models.Link;
import com.product.filthilink.entities.models.User;
import com.product.filthilink.exceptions.LinkExpiredException;
import com.product.filthilink.exceptions.ResourceNotFound;
import com.product.filthilink.repositories.auth.UserRepository;
import com.product.filthilink.repositories.links.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;
    private final ClickTrackingService clickTrackingService;
    private final UserRepository userRepository;

    private static final String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public LinkResponseDto createLink(CreateLinkRequestDto dto, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));


        String shortCode = generateUniqueShortCode();

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        Link link = linkMapper.toEntity(dto, shortCode, 0, expiresAt);
        link.setUser(user);

        Link savedLink = linkRepository.save(link);

        return linkMapper.toResponseDto(savedLink, baseUrl);

    }

    public Page<LinkResponseDto> getLinksByUser(String email, Pageable pageable) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        return linkRepository.findByUserId(user.getId(), pageable)
                .map(link -> linkMapper.toResponseDto(link, baseUrl));

    }


    public String resolveLink(String shortCode ) {

        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(()-> new ResourceNotFound(shortCode));

        if( link.getExpiresAt().isBefore(LocalDateTime.now()) ) {
            throw new LinkExpiredException(shortCode);
        }

        clickTrackingService.increment(link.getId());


        return link.getOriginalUrl();

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
