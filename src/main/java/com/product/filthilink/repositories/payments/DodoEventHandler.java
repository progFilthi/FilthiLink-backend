package com.product.filthilink.repositories.payments;

import com.product.filthilink.entities.dtos.WebhookPayload;

public interface DodoEventHandler {

    boolean supports(String eventType);
    void handle(WebhookPayload payload);
}
