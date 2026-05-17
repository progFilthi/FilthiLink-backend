package com.product.filthilink.services.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.product.filthilink.entities.dtos.*;
import com.product.filthilink.entities.enums.AuthProvider;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final GoogleTokenVerifier googleTokenVerifier;

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


    @Transactional
    public AuthResponseDto login(LoginRequestDto dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        User user = userRepository.findByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException(dto.email())
        );

        //This guard prevents password login for Google o-oauth users
        if( user.getAuthProvider() == AuthProvider.GOOGLE ){
            throw new BadRequest("This account uses google login only. Please continue with google");
        }

        //revoke all existing refresh tokens on login

        refreshTokenRepository.deleteTokenByUser(user);

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponseDto googleLogin(GoogleAuthRequestDto dto){
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(dto.idToken());

        String email = payload.getEmail();
        String googleId = payload.getSubject();

        User user = userRepository.findByEmail(email)
                .map(exitingUser -> linkGoogleIfNeeded(exitingUser, googleId))
                .orElseGet(()-> createGoogleUser(email, googleId));

        refreshTokenRepository.deleteTokenByUser(user);

        return generateAuthResponse(user);

    }

    private User linkGoogleIfNeeded(User user, String googleId){

        if (user.getGoogleId() == null) {

            user.setGoogleId(googleId);
            user.setAuthProvider(AuthProvider.BOTH);
            userRepository.save(user);
        }

        return user;

    }

    private User createGoogleUser(String  email, String googleId){
        User user = new User();

        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setAuthProvider(AuthProvider.GOOGLE);

        userRepository.save(user);

        return user;
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

    @Transactional
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
