package com.product.filthilink.services.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiry}")
    private String accessTokenExpiry; // expire after 15 mins -> short-lived

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(String email){

        Instant now = Instant.now();



        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(Long.parseLong(accessTokenExpiry))))
                .signWith(getKey())
                .compact();

    }

    //A refresh token is a secure random string not jwt
    public String generateRefreshToken(String email){

        byte[] bytes = new byte[32];

        Random random = new SecureRandom();

        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);




    }

    public String  extractEmail(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public boolean isValid(String token){

        try{
            extractEmail(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
