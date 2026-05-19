package com.product.filthilink.repositories.payments;

import com.product.filthilink.entities.models.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String > {

    boolean existsByEventId(String  eventId);
}
