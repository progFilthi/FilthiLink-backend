package com.product.filthilink.repositories.links;

import com.product.filthilink.entities.models.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    boolean existsByShortCode(String shortCode);
    Optional<Link> findByShortCode(String shortCode);

    Page<Link> findByUserId(Long id, Pageable pageable);

    long countByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(l.clickCount), 0) FROM Link l WHERE l.user.id = :userId")
    long sumClickCountByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(l.clickCount), 0) FROM Link l")
    long totalClickCount();
}
