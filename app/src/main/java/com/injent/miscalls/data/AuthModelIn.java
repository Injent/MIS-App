package com.injent.miscalls.data;

import com.google.gson.annotations.SerializedName;

public class AuthModelIn {

    public AuthModelIn(String token, User user) {
        this.token = token;
        this.user = user;
    }

    @SerializedName("token")
    private String token;

    private User user;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
