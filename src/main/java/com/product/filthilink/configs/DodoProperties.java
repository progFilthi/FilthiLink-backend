package com.product.filthilink.configs;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "dodo.payments")
@Validated
public record DodoProperties(
        @NotBlank String premiumMonthly,
        @NotBlank String premiumYearly,
        @NotBlank String apiKey,
        @NotBlank String webhookKey
) {
}
