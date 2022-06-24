package com.injent.miscalls.network.dto;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.database.user.Organization;
import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.data.database.user.User;

public class UserDto {

    @SerializedName("id")
    private int id;

    @SerializedName("login")
    private String login;

    @SerializedName("password")
    private String password;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("middleName")
    private String middleName;

    @SerializedName("workingPosition")
    private String workingPosition;

    @SerializedName("token")
    private TokenDto token;

    private int tokenId;

    private boolean authed;

    private int organizationId;

    @SerializedName("organization")
    private OrganizationDto organization;

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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getWorkingPosition() {
        return workingPosition;
    }

    public void setWorkingPosition(String workingPosition) {
        this.workingPosition = workingPosition;
    }

    public TokenDto getToken() {
        return token;
    }

    public void setToken(TokenDto token) {
        this.token = token;
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

    public OrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDto organization) {
        this.organization = organization;
    }

    public static User toDomainObject(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getLogin(),
                userDto.getPassword(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getMiddleName(),
                userDto.getWorkingPosition(),
                new Token(userDto.getToken().getValue(), userDto.getToken().getExpirationDate(), 0),
                new Organization(userDto.getOrganization().getName(), userDto.getId(), userDto.getOrganization().getAddress()));
    }
}
