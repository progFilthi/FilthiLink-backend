package com.product.filthilink.entities.dtos;

public record AdminStatsResponseDto(
        long totalUsers,
        long emailUsers,
        long googleUsers,
        long bothUsers,
        long adminUsers,
        long newUsersLast7Days,
        long newUsersLast30Days,
        long totalLinks,
        long totalClicks,
        long freeSubscriptions,
        long premiumSubscriptions,
        long activeSubscriptions,
        long cancelledSubscriptions
) {
}
