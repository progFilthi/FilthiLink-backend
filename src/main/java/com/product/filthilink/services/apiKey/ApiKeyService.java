package com.product.filthilink.services.apiKey;

import com.product.filthilink.entities.dtos.ApiKeyListItemDto;
import com.product.filthilink.entities.dtos.ApiKeyResponseDto;
import com.product.filthilink.entities.dtos.CreateApiKeyRequestDto;
import com.product.filthilink.entities.models.ApiKey;
import com.product.filthilink.entities.models.User;
import com.product.filthilink.exceptions.ResourceNotFound;
import com.product.filthilink.exceptions.Unauthorized;
import com.product.filthilink.repositories.apikey.ApiKeyRepository;
import com.product.filthilink.repositories.auth.UserRepository;
import com.product.filthilink.services.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyRepository apiKeyRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public ApiKeyResponseDto createApiKey(String email, CreateApiKeyRequestDto dto){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFound(email));


        String rawKey = "sk_live" +  generateSecureKey();
        String keyHash = passwordEncoder.encode(rawKey);
        String keyPrefix = rawKey.substring(0, 12);

        ApiKey apiKey = new ApiKey();

        apiKey.setUser(user);
        apiKey.setName(dto.name());
        apiKey.setKeyHash(keyHash);
        apiKey.setKeyPrefix(keyPrefix);

        ApiKey savedKey = apiKeyRepository.save(apiKey);

        return new ApiKeyResponseDto(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                rawKey, // return once and never stored
                savedKey.getCreatedAt()

        );




    }

    public List<ApiKeyListItemDto> getUserApiKeys(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFound(email));

        return apiKeyRepository.findUserById(user.getId())
                .stream()
                .map(key -> new ApiKeyListItemDto(
                        key.getId(),
                        key.getName(),
                        key.getKeyPrefix(),
                        key.getLastUsedAt(),
                        key.getCreatedAt()

                )).toList();
    }

    public void deleteApiKey( String email, Long apiKeyId ){

        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFound(email));

        ApiKey apiKey = apiKeyRepository.findById(apiKeyId).orElseThrow(()-> new ResourceNotFound("Api key is missing"));

        if( !apiKey.getUser().getId().equals(user.getId()) ){
            throw new Unauthorized("This api key does not belong to you.");
        }

        apiKeyRepository.delete(apiKey);

    }


    public Optional<UserDetails> validateApiKey(String rawKey ){
        return apiKeyRepository.findAll()
                .stream()
                .filter(key -> passwordEncoder.matches(rawKey, key.getKeyHash()))
                .findFirst()
                .map(key -> {
                    key.setLastUsedAt(LocalDateTime.now());
                    apiKeyRepository.save(key);
                    return customUserDetailsService.loadUserByUsername(key.getUser().getEmail());
                });
    }

    private String generateSecureKey(){

        Random random = new SecureRandom();

        byte[] bytes = new byte[24];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    }



}
