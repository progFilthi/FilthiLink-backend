package com.product.filthilink.entities.dtos;

import com.product.filthilink.entities.enums.AuthProvider;
import com.product.filthilink.entities.enums.Plan;
import com.product.filthilink.entities.enums.Role;
import com.product.filthilink.entities.enums.SubscriptionStatus;

import java.time.LocalDateTime;

public record AdminUserResponseDto(
        Long id,
        String email,
        AuthProvider authProvider,
        Role role,
        LocalDateTime createdAt,
        Plan subscriptionPlan,
        SubscriptionStatus subscriptionStatus,
        long linkCount,
        long totalClicks
) {
}
