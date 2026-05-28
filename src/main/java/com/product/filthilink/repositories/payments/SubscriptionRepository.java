package com.product.filthilink.repositories.payments;

import com.product.filthilink.entities.enums.Plan;
import com.product.filthilink.entities.enums.SubscriptionStatus;
import com.product.filthilink.entities.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserId(String user);
    Optional<Subscription> findByDodoSubscriptionId( String subscriptionId);
    Optional<Subscription> findByDodoCustomerId(String dodoCustomerId);

    long countByPlans(Plan plans);

    long countByStatus(SubscriptionStatus status);
}
