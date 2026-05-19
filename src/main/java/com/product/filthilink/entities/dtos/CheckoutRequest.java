package com.product.filthilink.entities.dtos;

import com.product.filthilink.entities.enums.BillingCycle;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "Billing cycle is required.")
        BillingCycle billingCycle
) {
}
