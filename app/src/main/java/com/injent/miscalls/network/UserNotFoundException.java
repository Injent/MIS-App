package com.injent.miscalls.network;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("UserNotFound");
    }
}
