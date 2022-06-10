package com.injent.miscalls.data.database.user;

import androidx.room.Embedded;
import androidx.room.Relation;

public class UserWithTokenAndOrg {

    @Embedded
    private User user;

    @Embedded
    private Token token;

    @Embedded
    private Organization organization;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
