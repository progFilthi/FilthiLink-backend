package com.product.filthilink.services.admin;

import com.product.filthilink.entities.dtos.AdminStatsResponseDto;
import com.product.filthilink.entities.dtos.AdminUserResponseDto;
import com.product.filthilink.entities.enums.AuthProvider;
import com.product.filthilink.entities.enums.Plan;
import com.product.filthilink.entities.enums.Role;
import com.product.filthilink.entities.enums.SubscriptionStatus;
import com.product.filthilink.entities.models.Subscription;
import com.product.filthilink.entities.models.User;
import com.product.filthilink.exceptions.ResourceNotFound;
import com.product.filthilink.repositories.auth.UserRepository;
import com.product.filthilink.repositories.links.LinkRepository;
import com.product.filthilink.repositories.payments.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final LinkRepository linkRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<AdminUserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .toList();
    }

    public AdminUserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        return toUserDto(user);
    }

    public AdminStatsResponseDto getStats() {
        LocalDateTime now = LocalDateTime.now();

        return new AdminStatsResponseDto(
                userRepository.count(),
                userRepository.countByAuthProvider(AuthProvider.EMAIL),
                userRepository.countByAuthProvider(AuthProvider.GOOGLE),
                userRepository.countByAuthProvider(AuthProvider.BOTH),
                userRepository.countByRole(Role.ADMIN),
                userRepository.countByCreatedAtAfter(now.minusDays(7)),
                userRepository.countByCreatedAtAfter(now.minusDays(30)),
                linkRepository.count(),
                linkRepository.totalClickCount(),
                subscriptionRepository.countByPlans(Plan.FREE),
                subscriptionRepository.countByPlans(Plan.PREMIUM),
                subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE),
                subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED)
        );
    }

    private AdminUserResponseDto toUserDto(User user) {
        Optional<Subscription> sub = subscriptionRepository.findByUserId(user.getEmail());

        return new AdminUserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getAuthProvider(),
                user.getRole(),
                user.getCreatedAt(),
                sub.map(Subscription::getPlans).orElse(Plan.FREE),
                sub.map(Subscription::getStatus).orElse(SubscriptionStatus.ACTIVE),
                linkRepository.countByUserId(user.getId()),
                linkRepository.sumClickCountByUserId(user.getId())
        );
    }
}
