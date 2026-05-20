package com.product.filthilink.exceptions;

public class InvalidWebhookSignatureException extends BadRequest {
    public InvalidWebhookSignatureException(String message) {
        super(message);
    }
}
