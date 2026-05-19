package com.product.filthilink.entities.dtos;

import java.util.Map;

public record WebhookPayload(
        String eventId,
        String eventType,
        String subscriptionId,
        String customerId,
        String paymentId,
        String nextBillingDate,
        String productId,
        Map<String , Object> metadata,
        String rawPayload
) {
}
