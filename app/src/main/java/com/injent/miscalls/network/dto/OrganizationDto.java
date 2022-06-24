package com.injent.miscalls.network.dto;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.database.user.Organization;

public class OrganizationDto {

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static Organization toDomainObject(OrganizationDto orgDto, int userId) {
        return new Organization(orgDto.getName(), userId, orgDto.getAddress());
    }
}
