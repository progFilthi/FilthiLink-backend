package com.product.filthilink.exceptions;

public class DuplicateEventException extends ResourceConflict {
    public DuplicateEventException(String message) {
        super(message);
    }
}
