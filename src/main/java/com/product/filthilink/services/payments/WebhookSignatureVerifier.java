package com.product.filthilink.services.payments;

import com.product.filthilink.configs.DodoProperties;
import com.product.filthilink.exceptions.InvalidWebhookSignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class WebhookSignatureVerifier {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HEADER_PREFIX = "sha256=";
    private final Mac mac;
    public WebhookSignatureVerifier(DodoProperties properties) {
        try {
            this.mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(
                    properties.webhookKey().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to initialize webhook verifier", e);
        }
    }
    public void verify(String payload, String signatureHeader) {
        String expectedSignature = signatureHeader.startsWith(HEADER_PREFIX)
                ? signatureHeader.substring(HEADER_PREFIX.length())
                : signatureHeader;
        byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String computedSignature = Base64.getEncoder().encodeToString(hmacBytes);
        if (!constantTimeEquals(computedSignature, expectedSignature)) {
            throw new InvalidWebhookSignatureException("Invalid webhook signature");
        }
    }
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}