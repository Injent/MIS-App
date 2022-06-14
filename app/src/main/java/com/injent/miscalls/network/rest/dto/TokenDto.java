package com.injent.miscalls.network.rest.dto;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.database.user.Token;

public class TokenDto {

    @SerializedName("value")
    private String value;

    @SerializedName("expirationDate")
    private long expirationDate;

    @SerializedName("userId")
    private int userId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static Token toDomainObject(TokenDto tokenDto, int userId) {
        return new Token(
                tokenDto.getValue(),
                tokenDto.getExpirationDate(),
                userId
        );
    }
}
