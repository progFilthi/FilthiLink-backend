package com.product.filthilink.controllers.payments;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.filthilink.entities.dtos.WebhookPayload;
import com.product.filthilink.services.payments.WebhookRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/webhooks/dodo")
@RequiredArgsConstructor
public class WebhookController {
    private final WebhookRouter webhookRouter;
    private final ObjectMapper objectMapper; // Autoconfigured by Spring Boot
    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(value = "X-Dodo-Signature", required = false) String signature,
            @RequestBody String rawPayload) {
        try {
            WebhookPayload payload = parsePayload(rawPayload);
            webhookRouter.process(rawPayload, signature, payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            if (e.getClass().getSimpleName().contains("Signature")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }
    private WebhookPayload parsePayload(String rawPayload) throws Exception {
        JsonNode root = objectMapper.readTree(rawPayload);
        return new WebhookPayload(
                root.path("payment_id").asText(root.path("subscription_id").asText()),
                root.path("event_type").asText(),
                root.path("subscription_id").asText(null),
                root.path("customer").path("customer_id").asText(null),
                root.path("payment_id").asText(null),
                root.path("next_billing_date").asText(null),
                root.path("product_id").asText(null),
                objectMapper.convertValue(root.path("metadata"), new TypeReference<Map<String, Object>>() {}),
                rawPayload
        );
    }
}