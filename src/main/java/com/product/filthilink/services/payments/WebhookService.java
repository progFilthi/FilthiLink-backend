package com.product.filthilink.services.payments;

import com.product.filthilink.entities.enums.Plan;
import com.product.filthilink.entities.enums.SubscriptionStatus;
import com.product.filthilink.entities.models.ProcessedEvent;
import com.product.filthilink.entities.models.Subscription;
import com.product.filthilink.repositories.payments.ProcessedEventRepository;
import com.product.filthilink.repositories.payments.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WebhookService {
    private final SubscriptionRepository subscriptionRepository;
    private final ProcessedEventRepository processedEventRepository;
    @Transactional
    public void process(String eventType, JsonNode data, String eventId) {
        if (processedEventRepository.existsByEventId(eventId)) {
            return;
        }
        switch (eventType) {
            case "subscription.active" -> handleActive(data);
            case "subscription.canceled" -> handleCanceled(data);
            case "subscription.failed" -> handleFailed(data);
            case "subscription.on_hold" -> handleOnHold(data);
            default -> { return; }
        }
        processedEventRepository.save(new ProcessedEvent(eventId, eventType));
    }
    private void handleActive(JsonNode data) {
        String subscriptionId = data.path("subscription_id").asString(null);
        String customerId = data.path("customer").path("customer_id").asString(null);
        String nextBilling = data.path("next_billing_date").asString(null);
        Subscription sub = subscriptionRepository
                .findByDodoSubscriptionId(subscriptionId)
                .or(() -> subscriptionRepository.findByDodoCustomerId(customerId))
                .orElse(null);
        if (sub == null) return;
        sub.setDodoSubscriptionId(subscriptionId);
        sub.setPlans(Plan.FREE);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        if (nextBilling != null) {
            sub.setCurrentPeriodEnd(Instant.parse(nextBilling));
        }
        subscriptionRepository.save(sub);
    }
    private void handleCanceled(JsonNode data) {
        updateStatus(data.path("subscription_id").asString(null), SubscriptionStatus.CANCELLED);
    }
    private void handleFailed(JsonNode data) {
        Subscription sub = subscriptionRepository.findByDodoSubscriptionId(data.path("subscription_id").asString(null)).orElse(null);
        if (sub != null) {
            sub.setStatus(SubscriptionStatus.FAILED);
            sub.setPlans(Plan.FREE);
            subscriptionRepository.save(sub);
        }
    }
    private void handleOnHold(JsonNode data) {
        updateStatus(data.path("subscription_id").asString(null), SubscriptionStatus.ON_HOLD);
    }
    private void updateStatus(String subscriptionId, SubscriptionStatus status) {
        subscriptionRepository.findByDodoSubscriptionId(subscriptionId)
                .ifPresent(sub -> {
                    sub.setStatus(status);
                    subscriptionRepository.save(sub);
                });
    }
}