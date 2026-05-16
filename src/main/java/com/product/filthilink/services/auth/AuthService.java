package com.product.filthilink.services.auth;

import com.product.filthilink.entities.dtos.AuthResponseDto;
import com.product.filthilink.entities.dtos.LoginRequestDto;
import com.product.filthilink.entities.dtos.RefreshRequestDto;
import com.product.filthilink.entities.dtos.RegisterRequestDto;
import com.product.filthilink.entities.models.RefreshToken;
import com.product.filthilink.entities.models.User;
import com.product.filthilink.exceptions.BadRequest;
import com.product.filthilink.exceptions.ResourceConflict;
import com.product.filthilink.exceptions.ResourceNotFound;
import com.product.filthilink.repositories.auth.RefreshTokenRepository;
import com.product.filthilink.repositories.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.refresh-token.expiry}")
    private String refreshTokenExpiry;

    public AuthResponseDto register(RegisterRequestDto dto){
        if( userRepository.existsByEmail(dto.email()) ){
            throw new ResourceConflict("Email already exists");
        }

        User user = new User();

        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(user);

        return generateAuthResponse(user);
    }


    public AuthResponseDto login(LoginRequestDto dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        User user = userRepository.findByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException(dto.email())
        );

        //revoke all existing refresh tokens on login

        refreshTokenRepository.deleteTokenByUser(user);

        return generateAuthResponse(user);
    }

    public AuthResponseDto refresh(RefreshRequestDto dto){
        String tokenHash = hashToken(dto.refreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByRefreshTokenHash(tokenHash)
                .orElseThrow(()-> new ResourceNotFound("Refresh token not found"));


        if( storedToken.isRevoked() ){
            throw new BadRequest("Refresh token has been revoked");
        }

        if( storedToken.getExpiresAt().isBefore(LocalDateTime.now()) ){
            throw new BadRequest("Refresh token has expired");
        }

        //rotate, revoke or issue new pair

        storedToken.setRevoked(true);

        refreshTokenRepository.save(storedToken);

        return generateAuthResponse(storedToken.getUser());
    }

    public void logout(String email){
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException(email)
        );

        refreshTokenRepository.deleteTokenByUser(user);
    }

    private AuthResponseDto generateAuthResponse(User user){
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());

        String rawRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setRefreshTokenHash(hashToken(rawRefreshToken));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(Long.parseLong(refreshTokenExpiry)));
        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDto(accessToken, rawRefreshToken, user.getEmail());

    }

    private String hashToken(String token){

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] hash = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().encodeToString(hash);


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available ", e);
        }

    }

}
