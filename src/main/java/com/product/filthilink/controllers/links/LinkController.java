package com.product.filthilink.controllers.links;

import com.product.filthilink.entities.dtos.CreateLinkRequestDto;
import com.product.filthilink.entities.dtos.LinkResponseDto;
import com.product.filthilink.services.links.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping("/shorten")
    public ResponseEntity<LinkResponseDto> createLink(@RequestBody CreateLinkRequestDto dto){

        LinkResponseDto response = linkService.createLink(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect( @PathVariable String shortCode ) {

        String originalUrl = linkService.resolveLink(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();

    }


}
