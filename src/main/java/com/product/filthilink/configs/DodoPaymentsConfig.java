package com.product.filthilink.configs;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DodoPaymentsConfig {

    private static final String  SANDBOX_URL = "https://test.dodopayments.com";
    private static final String  LIVE_URL = "https://live.dodopayments.com";

    @Bean
    public DodoPaymentsClient dodoPaymentsClient(DodoProperties dodoProperties) {

        String baseUrl = "live".equalsIgnoreCase(dodoProperties.environment())
                ? LIVE_URL
                : SANDBOX_URL;

        return DodoPaymentsOkHttpClient.builder()
                .bearerToken(dodoProperties.apiKey())
                .baseUrl(baseUrl)
                .build();
    }
}
