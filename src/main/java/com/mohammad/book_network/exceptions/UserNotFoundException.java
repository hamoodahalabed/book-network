package com.mohammad.book_network.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super();
    }
    public UserNotFoundException(String message){
        super(message);
    }
}
