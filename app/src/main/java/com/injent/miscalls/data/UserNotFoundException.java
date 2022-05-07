package com.injent.miscalls.data;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("UserNotFound");
    }
}
