package com.hits.user.Exceptions;

public class AccountNotConfirmedException extends RuntimeException{
    public AccountNotConfirmedException(String message){
        super(message);
    }
}
