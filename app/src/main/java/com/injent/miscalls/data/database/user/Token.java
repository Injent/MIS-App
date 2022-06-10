package com.injent.miscalls.data.database.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "token")
public class Token {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("value")
    @ColumnInfo(name = "value")
    private String value;

    @SerializedName("expirationDate")
    @ColumnInfo(name = "exp_date")
    private long expirationDate;

    @SerializedName("userId")
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
