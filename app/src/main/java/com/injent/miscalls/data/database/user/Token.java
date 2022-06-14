package com.injent.miscalls.data.database.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "token")
public class Token {

    @Ignore
    public Token(String value, long expirationDate, int userId) {
        this.value = value;
        this.expirationDate = expirationDate;
        this.userId = userId;
    }

    public Token() {
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "value")
    private String value;

    @ColumnInfo(name = "exp_date")
    private long expirationDate;

    @ColumnInfo(name = "user_id")
    private int userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
