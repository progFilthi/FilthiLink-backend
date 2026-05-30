package com.product.filthilink.repositories.auth;

import com.product.filthilink.entities.enums.AuthProvider;
import com.product.filthilink.entities.enums.Role;
import com.product.filthilink.entities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    long countByAuthProvider(AuthProvider authProvider);

    long countByRole(Role role);

    long countByCreatedAtAfter(LocalDateTime since);
}
