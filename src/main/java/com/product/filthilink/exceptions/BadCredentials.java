package com.product.filthilink.exceptions;

public class BadCredentials extends RuntimeException {
    public BadCredentials(String message) {
        super(message);
    }
}
