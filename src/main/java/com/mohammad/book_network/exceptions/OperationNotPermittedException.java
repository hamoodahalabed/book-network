package com.mohammad.book_network.exceptions;

public class OperationNotPermittedException extends RuntimeException {

    public OperationNotPermittedException() {
        super();
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }
}
