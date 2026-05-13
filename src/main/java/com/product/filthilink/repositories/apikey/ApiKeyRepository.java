package com.product.filthilink.repositories.apikey;

import com.product.filthilink.entities.models.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {


     List<ApiKey> findUserById(Long userId);


     Optional<ApiKey> findByKeyPrefix(String prefix);
}
