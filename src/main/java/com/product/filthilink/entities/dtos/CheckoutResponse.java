package com.product.filthilink.entities.dtos;

public record CheckoutResponse(
        String checkoutUrl,
        String sessionId
){
}
