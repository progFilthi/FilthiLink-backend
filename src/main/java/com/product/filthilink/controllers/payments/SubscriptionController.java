package com.product.filthilink.controllers.payments;

import com.product.filthilink.entities.dtos.CheckoutRequest;
import com.product.filthilink.entities.dtos.CheckoutResponse;
import com.product.filthilink.entities.dtos.SubscriptionResponseDto;
import com.product.filthilink.services.payments.CheckoutService;
import com.product.filthilink.services.payments.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final CheckoutService checkoutService;
    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponseDto> getMySubscription(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        subscriptionService.findOrInitialiseSubscription(userId);
        return ResponseEntity.ok(subscriptionService.getSubscription(userId));
    }
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createCheckout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String email,
            @Valid @RequestBody CheckoutRequest request) {
        String userId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkoutService.createCheckout(userId, email, null, request.billingCycle()));
    }
    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancelSubscription(@AuthenticationPrincipal UserDetails userDetails) {
        subscriptionService.cancelSubscription(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}