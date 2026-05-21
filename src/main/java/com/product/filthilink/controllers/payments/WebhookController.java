package com.product.filthilink.controllers.payments;

import com.product.filthilink.services.payments.WebhookService;
import com.product.filthilink.services.payments.WebhookSignatureVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
@RestController
@RequestMapping("/api/v1/webhooks/dodo")
@RequiredArgsConstructor


public class WebhookController {

    private final WebhookService webhookService;
    private final WebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(value = "X-Dodo-Signature", required = false) String signature,
            @RequestBody String rawPayload) {
        try {
            signatureVerifier.verify(rawPayload, signature);

            JsonNode root = objectMapper.readTree(rawPayload);
            String eventType = root.path("event_type").asString();
            String eventId = root.path("payment_id").asString(root.path("subscription_id").asString());

            webhookService.process(eventType, root, eventId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            if (e.getClass().getSimpleName().contains("Signature")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }
}