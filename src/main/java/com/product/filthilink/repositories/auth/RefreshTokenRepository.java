package com.product.filthilink.repositories.auth;

import com.product.filthilink.entities.models.RefreshToken;
import com.product.filthilink.entities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshTokenHash(String refreshTokenHash);

    @Transactional
    @Modifying
    void deleteTokenByUser(User user);



}
