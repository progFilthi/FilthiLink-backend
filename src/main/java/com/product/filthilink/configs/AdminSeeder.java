package com.product.filthilink.configs;

import com.product.filthilink.entities.enums.Role;
import com.product.filthilink.repositories.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;

    @Value("${app.admin-emails:}")
    private String adminEmails;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (adminEmails == null || adminEmails.isBlank()) return;

        for (String raw : adminEmails.split(",")) {
            String email = raw.trim();
            if (email.isEmpty()) continue;

            userRepository.findByEmail(email).ifPresentOrElse(
                    user -> {
                        if (user.getRole() != Role.ADMIN) {
                            user.setRole(Role.ADMIN);
                            userRepository.save(user);
                            log.info("Promoted user {} to ADMIN", email);
                        }
                    },
                    () -> log.warn("Admin email {} not found in database. User must register first.", email)
            );
        }
    }
}
