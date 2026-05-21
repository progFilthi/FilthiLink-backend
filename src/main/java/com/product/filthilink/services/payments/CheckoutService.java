package com.product.filthilink.services.payments;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.core.JsonValue;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionCreateParams;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionRequest;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionResponse;
import com.dodopayments.api.models.checkoutsessions.ProductItemReq;
import com.dodopayments.api.models.customers.Customer;
import com.dodopayments.api.models.customers.CustomerCreateParams;
import com.dodopayments.api.models.payments.AttachExistingCustomer;
import com.product.filthilink.configs.DodoProperties;
import com.product.filthilink.entities.dtos.CheckoutRequest;
import com.product.filthilink.entities.dtos.CheckoutResponse;
import com.product.filthilink.entities.enums.BillingCycle;
import com.product.filthilink.entities.models.Subscription;
import com.product.filthilink.exceptions.SubscriptionNotFoundException;
import com.product.filthilink.repositories.payments.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.product.filthilink.entities.enums.BillingCycle.MONTHLY;
import static com.product.filthilink.entities.enums.BillingCycle.YEARLY;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final DodoPaymentsClient dodoClient;
    private final DodoProperties properties;
    private final SubscriptionRepository subscriptionRepository;
    @Transactional
    public CheckoutResponse createCheckout(String userId, String email, String name, BillingCycle cycle) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found"));
        String customerId = resolveOrCreateCustomer(userId, email, name, subscription);
        String productId = resolveProductId(cycle);
        CheckoutSessionRequest requestBody = CheckoutSessionRequest.builder()
                .addProductCart(ProductItemReq.builder()
                        .productId(productId)
                        .quantity(1)
                        .build())
                .customer(AttachExistingCustomer.builder()
                        .customerId(customerId)
                        .build())
                .metadata(CheckoutSessionRequest.Metadata.builder()
                        .putAdditionalProperty("user_id", JsonValue.from(userId))
                        .build())
                .build();
        CheckoutSessionCreateParams params = CheckoutSessionCreateParams.builder()
                .checkoutSessionRequest(requestBody)
                .build();
        CheckoutSessionResponse response = dodoClient.checkoutSessions().create(params);
        return new CheckoutResponse(
                response.checkoutUrl().orElse(null),
                response.sessionId()
        );
    }
    private String resolveOrCreateCustomer(String userId, String email, String name, Subscription subscription) {
        if (subscription.getDodoCustomerId() != null) {
            return subscription.getDodoCustomerId();
        }
        Customer customer = dodoClient.customers().create(
                CustomerCreateParams.builder()
                        .email(email)
                        .name(name != null ? name : "User " + userId)
                        .metadata(CustomerCreateParams.Metadata.builder()
                                .putAdditionalProperty("user_id", JsonValue.from(userId))
                                .build())
                        .build()
        );
        String customerId = customer.customerId();
        subscription.setDodoCustomerId(customerId);
        subscriptionRepository.save(subscription);
        return customerId;
    }
    private String resolveProductId(BillingCycle cycle) {
        return switch (cycle) {
            case MONTHLY -> properties.premiumMonthly();
            case YEARLY -> properties.premiumYearly();
            default -> throw new IllegalStateException("Unexpected value: " + cycle);
        };
    }
}