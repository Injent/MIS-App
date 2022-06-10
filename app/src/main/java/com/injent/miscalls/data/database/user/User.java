package com.injent.miscalls.data.database.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "user")
public class User {

    public User() {
        // Empty
    }

    public User(String login, String password, String firstName, String lastName, String middleName, String workingPosition, Token token) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.workingPosition = workingPosition;
        this.token = token;
    }

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("login")
    @ColumnInfo(name = "login")
    private String login;

    @SerializedName("password")
    @ColumnInfo(name = "password")
    private String password;

    @SerializedName("firstName")
    @ColumnInfo(name = "first_name")
    private String firstName;

    @SerializedName("lastName")
    @ColumnInfo(name = "last_name")
    private String lastName;

    @SerializedName("middleName")
    @ColumnInfo(name = "middle_name")
    private String middleName;

    @SerializedName("workingPosition")
    @ColumnInfo(name = "working_position")
    private String workingPosition;

    @SerializedName("token")
    @Ignore
    private Token token;

    @ColumnInfo(name = "token_id")
    private int tokenId;

    @ColumnInfo(name = "authed")
    private boolean authed;

    @ColumnInfo(name = "org_id")
    private String organizationId;

    @Ignore
    @SerializedName("organization")
    private Organization organization;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getWorkingPosition() {
        if (workingPosition == null)
            return "";
        return workingPosition;
    }

    public Token getToken() {
        return token;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void setAuthed(boolean authed) {
        this.authed = authed;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setWorkingPosition(String workingPosition) {
        this.workingPosition = workingPosition;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }
}
