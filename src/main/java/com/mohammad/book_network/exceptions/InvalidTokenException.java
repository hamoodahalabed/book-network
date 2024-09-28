package com.mohammad.book_network.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super();
    }
    public InvalidTokenException(String message) {
        super(message);
    }
}
