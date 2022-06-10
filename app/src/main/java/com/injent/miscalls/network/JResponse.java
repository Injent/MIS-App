package com.injent.miscalls.network;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.Calls;
import com.injent.miscalls.data.database.user.User;

import java.util.Collections;
import java.util.List;

import retrofit2.http.FormUrlEncoded;

public class JResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private int code;

    @SerializedName("user")
    private User user;

    @SerializedName("calls")
    private Calls calls;

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public User getUser() {
        return user;
    }

    public List<CallInfo> getCalls() {
        return calls.calls();
    }

    public boolean isSuccessful() {
        return code == 100;
    }
}
