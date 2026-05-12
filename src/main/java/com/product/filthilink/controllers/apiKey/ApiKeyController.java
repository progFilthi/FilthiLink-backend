package com.product.filthilink.controllers.apiKey;

import com.product.filthilink.entities.dtos.ApiKeyListItemDto;
import com.product.filthilink.entities.dtos.ApiKeyResponseDto;
import com.product.filthilink.entities.dtos.CreateApiKeyRequestDto;
import com.product.filthilink.services.apiKey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyResponseDto> createKey(
            @RequestBody CreateApiKeyRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.createApiKey(userDetails.getUsername(), dto));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyListItemDto>> getKeys(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(apiKeyService.getUserApiKeys(userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKey(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        apiKeyService.deleteApiKey(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
