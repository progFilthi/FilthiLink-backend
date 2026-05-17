package com.product.filthilink.services.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;


    public GoogleTokenVerifier(@Value("${app.google.client-id}") String clientId ) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        )
                .setAudience(Collections.singleton(clientId))
                .build();
    }

    public GoogleIdToken.Payload verify(String idToken) {
        try{

            GoogleIdToken token = verifier.verify(idToken);

            if( token == null ){
                throw new RuntimeException("Invalid token");
            }

            return token.getPayload();

        } catch (Exception e) {
            log.error("Google token verification failed: {}", e.getMessage());
            throw new RuntimeException("Google token verification failed", e);
        }
    }
}
