package com.product.filthilink.services.payments;

import com.product.filthilink.entities.dtos.WebhookPayload;
import com.product.filthilink.entities.models.ProcessedEvent;
import com.product.filthilink.repositories.payments.DodoEventHandler;
import com.product.filthilink.repositories.payments.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebhookRouter {
    private final WebhookSignatureVerifier signatureVerifier;
    private final ProcessedEventRepository processedEventRepository;
    private final List<DodoEventHandler> eventHandlers;


    @Transactional
    public void process(String rawPayload, String signatureHeader, WebhookPayload payload) {
        signatureVerifier.verify(rawPayload, signatureHeader);
        if (processedEventRepository.existsByEventId(payload.eventId())) {
            return;
        }
        DodoEventHandler handler = eventHandlers.stream()
                .filter(h -> h.supports(payload.eventType()))
                .findFirst()
                .orElse(null);
        if (handler == null) {
            markAsProcessed(payload);
            return;
        }
        handler.handle(payload);
        markAsProcessed(payload);
    }
    private void markAsProcessed(WebhookPayload payload) {
        try {
            processedEventRepository.save(new ProcessedEvent(payload.eventId(), payload.eventType()));
        } catch (Exception ignored) {
        }
    }
}
