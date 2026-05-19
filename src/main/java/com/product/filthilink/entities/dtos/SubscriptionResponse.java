package com.product.filthilink.entities.dtos;

import java.time.Instant;

public record SubscriptionResponse(
        String plan,
        String status,
        Instant currentPeriodEnd,
        boolean isPremium
) {
}
