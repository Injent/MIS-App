package com.injent.miscalls.data;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

public class User {

    public User(String login, String password, String name, String lastName, String middleName, String workingPosition, String token) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.middleName = middleName;
        this.workingPosition = workingPosition;
        this.token = token;
    }

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("login")
    private String login;

    @SerializedName("password")
    private String password;

    @SerializedName("name")
    private String name;

    @SerializedName("lastname")
    private String lastName;

    @SerializedName("middle_name")
    private String middleName;

    @SerializedName("working_position")
    private String workingPosition;

    @SerializedName("token")
    private String token;

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getFullName() {
        return lastName + " " + name + " " + middleName;
    }

    public String getWorkingPosition() {
        return workingPosition;
    }

    public String getToken() { return token; }
}
