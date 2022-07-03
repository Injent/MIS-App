package com.injent.miscalls.data.database.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.injent.miscalls.network.dto.UserDto;
import com.injent.miscalls.util.DateConverter;

import java.util.Date;

@Entity(tableName = "user")
@TypeConverters(DateConverter.class)
public class User {

    public User() {
        // Empty body
    }

    /**
     * Used only in {@link UserDto#toDomainObject(UserDto)}
     */
    @Ignore
    public User(int id,
                String login,
                String password,
                String firstName,
                String lastName,
                String middleName,
                String workingPosition,
                Token token,
                Organization organization
    ) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.workingPosition = workingPosition;
        this.token = token;
        this.organization = organization;
    }

    @PrimaryKey
    private int id;
    @ColumnInfo(name = "login")
    private String login;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "middle_name")
    private String middleName;
    @ColumnInfo(name = "working_position")
    private String workingPosition;
    @Ignore
    private Token token;
    @ColumnInfo(name = "token_id")
    private int tokenId;
    @ColumnInfo(name = "authed")
    private boolean authed;
    @ColumnInfo(name = "org_id")
    private int organizationId;
    @Ignore
    private Organization organization;
    @ColumnInfo(name = "last_active")
    private boolean lastActive;
    @ColumnInfo(name = "update_time")
    private Date dbUpdateTime;

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

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
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

    public boolean isLastActive() {
        return lastActive;
    }

    public void setLastActive(boolean lastActive) {
        this.lastActive = lastActive;
    }

    public Date getDbUpdateTime() {
        return dbUpdateTime;
    }

    public void setDbUpdateTime(Date dbUpdateTime) {
        this.dbUpdateTime = dbUpdateTime;
    }

    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }
}
