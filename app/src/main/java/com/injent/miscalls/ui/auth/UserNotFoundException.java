package com.injent.miscalls.ui.auth;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("UserNotFound");
    }
}
