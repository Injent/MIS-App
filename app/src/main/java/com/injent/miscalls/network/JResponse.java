package com.injent.miscalls.network;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.network.dto.CallDto;
import com.injent.miscalls.network.dto.UserDto;

import java.util.List;

public class JResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private int code;

    @SerializedName("user")
    private UserDto user;

    @SerializedName("calls")
    private List<CallDto> calls;

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public UserDto getUser() {
        return user;
    }

    public List<CallDto> getCalls() {
        return calls;
    }

    public boolean isSuccessful() {
        return code == 100;
    }
}
