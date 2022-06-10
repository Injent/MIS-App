package com.injent.miscalls.data.database.calls;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.database.DateConverter;

import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity(tableName = "calls")
@TypeConverters(DateConverter.class)
public class CallInfo {

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    private int id;

    @SerializedName("editCardDate")
    @ColumnInfo(name = "edit_card_date")
    private Date editCardDate;

    @SerializedName("callTime")
    @ColumnInfo(name = "call_time")
    private Date callTime;

    @SerializedName("reason")
    @ColumnInfo(name = "complaints")
    private String complaints;

    @SerializedName("bcc")
    @ColumnInfo(name = "benefit_category_code")
    private String benefitCategoryCode;

    @ColumnInfo(name = "inspected")
    private boolean inspected;

    @SerializedName("firstName")
    @ColumnInfo(name = "first_name")
    private String firstname;

    @SerializedName("middleName")
    @ColumnInfo(name = "middle_name")
    private String middleName;

    @SerializedName("lastName")
    @ColumnInfo(name = "last_name")
    private String lastname;

    @SerializedName("sex")
    @ColumnInfo(name = "sex")
    private boolean sex;

    @SerializedName("residence")
    @ColumnInfo(name = "residence")
    private String residence;

    @SerializedName("phoneNumber")
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @SerializedName("bornDate")
    @ColumnInfo(name = "born_date")
    private Date bornDate;

    @SerializedName("snils")
    @ColumnInfo(name = "snils")
    private String snils;

    @SerializedName("polis")
    @ColumnInfo(name = "polis")
    private String polis;

    @SerializedName("organization")
    @ColumnInfo(name = "organization")
    private String organization;

    @SerializedName("passport")
    @ColumnInfo(name = "passport")
    private String passport;

    public int getId() {
        return id;
    }

    public String getComplaints() {
        return complaints;
    }

    public boolean isInspected() {
        return inspected;
    }

    public String getResidence() {
        if (residence == null)
            return "";
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public Date getEditCardDate() {
        return editCardDate;
    }

    public String getBenefitCategoryCode() {
        return benefitCategoryCode;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getBornDate() {
        if (bornDate == null)
            return new Date(0);
        return bornDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEditCardDate(Date editCardDate) {
        this.editCardDate = editCardDate;
    }

    public void setComplaints(String complaints) {
        this.complaints = complaints;
    }

    public void setBenefitCategoryCode(String benefitCategoryCode) {
        this.benefitCategoryCode = benefitCategoryCode;
    }

    public void setInspected(boolean inspected) {
        this.inspected = inspected;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getPolis() {
        return polis;
    }

    public void setPolis(String polis) {
        this.polis = polis;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public boolean getSex() {
        return sex;
    }

    public List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add(callTime.toString());
        list.add(editCardDate.toString());
        list.add(lastname + " " + firstname + " " + getMiddleName());
        if (sex) list.add("Муж.");
        else list.add("Жен.");
        list.add(bornDate.toString());
        list.add(residence);
        list.add(benefitCategoryCode);
        list.add(snils);
        list.add(polis);
        list.add(organization);
        list.add(passport);
        list.add(phoneNumber);
        return list;
    }

    public static String autoFillString(CallInfo callInfo, String s) {
        return s.replace("@фио", callInfo.getFullName())
                .replace("@др", callInfo.getBornDate().toString())
                .replace("@полис", callInfo.getPolis())
                .replace("@снилс", callInfo.getSnils())
                .replace("@адрес", callInfo.getResidence())
                .replace("@имя", callInfo.getFirstname())
                .replace("@фамилия", callInfo.getLastname())
                .replace("@отчество", callInfo.getMiddleName());
    }

    public String getMiddleName() {
        if (middleName == null) {
            middleName = " ";
        }
        return middleName;
    }

    public int getAge() {
        return Calendar.getInstance().get(Calendar.YEAR) - bornDate.toInstant().get(ChronoField.YEAR);
    }

    public String getFullName() {
        return lastname + " " + firstname + " " + getMiddleName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CallInfo)
            return ((CallInfo) obj).getId() == id;
        return false;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getEditCardDate().hashCode();
        result = 31 * result + getComplaints().hashCode();
        result = 31 * result + getBenefitCategoryCode().hashCode();
        result = 31 * result + (isInspected() ? 1 : 0);
        result = 31 * result + getFirstname().hashCode();
        result = 31 * result + getMiddleName().hashCode();
        result = 31 * result + getLastname().hashCode();
        result = 31 * result + getResidence().hashCode();
        result = 31 * result + getPhoneNumber().hashCode();
        result = 31 * result + getBornDate().hashCode();
        result = 31 * result + getSnils().hashCode();
        result = 31 * result + getPolis().hashCode();
        result = 31 * result + getOrganization().hashCode();
        result = 31 * result + getPassport().hashCode();
        return result;
    }
}
