package com.product.filthilink.repositories.auth;

import com.product.filthilink.entities.models.RefreshToken;
import com.product.filthilink.entities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshTokenHash(String refreshTokenHash);

    void deleteTokenByUser(User user);



}
