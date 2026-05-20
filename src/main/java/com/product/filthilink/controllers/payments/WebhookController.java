package com.product.filthilink.controllers.payments;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;



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
    private WebhookPayload parsePayload(String rawPayload) {
        JsonNode root = objectMapper.readTree(rawPayload);
        JsonNode metadataNode = root.path("metadata");
        Map<String, Object> metadata = metadataNode.isMissingNode() || metadataNode.isNull()
                ? Map.of()
                : objectMapper.treeToValue(metadataNode, new TypeReference<>() {
        });
        return new WebhookPayload(
                root.path("payment_id").asString(root.path("subscription_id").asString()),
                root.path("event_type").asString(),
                root.path("subscription_id").asString(null),
                root.path("customer").path("customer_id").asString(null),
                root.path("payment_id").asString(null),
                root.path("next_billing_date").asString(null),
                root.path("product_id").asString(null),
                metadata,
                rawPayload
        );
    }
}