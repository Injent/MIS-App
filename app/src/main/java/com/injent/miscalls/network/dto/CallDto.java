package com.injent.miscalls.network.dto;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.database.medcall.MedCall;

import java.sql.Date;

public class CallDto {

    @SerializedName("id")
    private int id;
    @SerializedName("userId")
    private int userId;
    @SerializedName("callTime")
    private String callTime;
    @SerializedName("editCardDate")
    private String editCardDate;
    @SerializedName("reason")
    private String reason;
    @SerializedName("bcc")
    private String bcc;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("middleName")
    private String middleName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("bornDate")
    private String bornDate;
    @SerializedName("sex")
    private boolean sex;
    @SerializedName("residence")
    private String residence;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("snils")
    private long snils;
    @SerializedName("polis")
    private long polis;
    @SerializedName("passport")
    private String passport;
    @SerializedName("age")
    private int age;
    @SerializedName("orgName")
    private String orgName;
    @SerializedName("geoPointDto")
    private GeoPointDto geoPoint;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getCallTime() {
        return callTime;
    }

    public String getEditCardDate() {
        return editCardDate;
    }

    public String getReason() {
        return reason;
    }

    public String getBcc() {
        return bcc;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBornDate() {
        return bornDate;
    }

    public boolean getSex() {
        return sex;
    }

    public String getResidence() {
        return residence;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getSnils() {
        return snils;
    }

    public long getPolis() {
        return polis;
    }

    public String getPassport() {
        return passport;
    }

    public int getAge() {
        return age;
    }

    public String getOrgName() {
        return orgName;
    }

    public GeoPointDto getGeoPoint() {
        return geoPoint;
    }

    public static MedCall toDomainObject(CallDto callDto) {
        return new MedCall(
                callDto.getId(),
                callDto.getUserId(),
                Date.valueOf(callDto.getEditCardDate()),
                Date.valueOf(callDto.getEditCardDate()),
                callDto.getReason(),
                callDto.getBcc(),
                false,
                callDto.getFirstName(),
                callDto.getMiddleName(),
                callDto.getLastName(),
                callDto.getSex(),
                callDto.getResidence(),
                callDto.getPhoneNumber(),
                Date.valueOf(callDto.getBornDate()),
                callDto.getSnils(),
                callDto.getPolis(),
                callDto.getOrgName(),
                callDto.getPassport(),
                callDto.getAge(),
                GeoPointDto.toDomainObject(callDto.getGeoPoint())
        );
    }
}
