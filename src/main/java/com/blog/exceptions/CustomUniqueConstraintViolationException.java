package com.blog.exceptions;

public class CustomUniqueConstraintViolationException extends RuntimeException {
    public CustomUniqueConstraintViolationException(String message) {
        super(message);
    }
}
