package com.product.filthilink.services.payments;

import com.product.filthilink.entities.dtos.SubscriptionResponseDto;
import com.product.filthilink.entities.enums.Plan;
import com.product.filthilink.entities.enums.SubscriptionStatus;
import com.product.filthilink.entities.models.Subscription;
import com.product.filthilink.exceptions.SubscriptionNotFoundException;
import com.product.filthilink.repositories.payments.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;


    @Transactional(readOnly = true)
    public SubscriptionResponseDto getSubscription(String userId){
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new SubscriptionNotFoundException("Subscription with id " + userId + " not found")
                );

        return new SubscriptionResponseDto(
                subscription.getPlans(),
                subscription.getStatus(),
                subscription.getCurrentPeriodEnd(),
                subscription.isPremium()
        );
    }


    @Transactional
    public Subscription findOrInitialiseSubscription(String userId){

        return subscriptionRepository.findByUserId(userId)
                .orElseGet(
                        ()-> {
                            Subscription sub = new Subscription();
                            sub.setUserId(userId);
                            sub.setPlans(Plan.FREE);
                            sub.setStatus(SubscriptionStatus.ACTIVE);
                           return subscriptionRepository.save(sub);
                        }
                );
    }

    @Transactional
    public void cancelSubscription(String userId){
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new SubscriptionNotFoundException("Subscription with id " + userId + " not found")
                );

        if(subscription.getPlans() == Plan.FREE) return;

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }







}
