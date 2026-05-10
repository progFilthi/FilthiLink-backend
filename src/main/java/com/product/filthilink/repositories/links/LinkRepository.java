package com.product.filthilink.repositories.links;

import com.product.filthilink.entities.models.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    boolean existsByShortCode(String shortCode);
    Optional<Link> findByShortCode(String shortCode);
}
