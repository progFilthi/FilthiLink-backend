package com.product.filthilink.controllers.links;

import com.product.filthilink.entities.dtos.CreateLinkRequestDto;
import com.product.filthilink.entities.dtos.LinkResponseDto;
import com.product.filthilink.services.links.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping("/shorten")
    public ResponseEntity<LinkResponseDto> createLink(@RequestBody CreateLinkRequestDto dto, @AuthenticationPrincipal UserDetails userDetails) {


        LinkResponseDto response = linkService.createLink(dto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


    @GetMapping
    public ResponseEntity<Page<LinkResponseDto>> getMyLinks(@PageableDefault(sort = "createdAt", size = 5) Pageable pageable,
                                                            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(linkService.getLinksByUser(userDetails.getUsername(), pageable));

    }


}
