package com.hits.user.Exceptions;

public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException(String message){
        super(message);
    }
}
