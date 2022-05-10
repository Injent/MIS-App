package com.injent.miscalls.data;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.calllist.QueryToken;

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
    private final String name;

    @SerializedName("lastname")
    private final String lastName;

    @SerializedName("middle_name")
    private final String middleName;

    @SerializedName("working_position")
    private final String workingPosition;

    @SerializedName("token")
    private final String token;

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

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public QueryToken getQueryToken() {
        QueryToken queryToken = new QueryToken();
        queryToken.setToken(token);
        return queryToken;
    }
}
