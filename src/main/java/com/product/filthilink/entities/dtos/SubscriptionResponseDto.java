package com.product.filthilink.entities.dtos;

import com.product.filthilink.entities.enums.Plan;
import com.product.filthilink.entities.enums.SubscriptionStatus;

import java.time.Instant;

public record SubscriptionResponseDto(
        Plan plan,
        SubscriptionStatus status,
        Instant currentPeriodEnd,
        boolean isPremium
) {
}
