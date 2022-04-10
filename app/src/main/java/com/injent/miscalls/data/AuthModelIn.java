package com.injent.miscalls.data;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.templates.User;

public class AuthModelIn {

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
