package com.injent.miscalls.data.database.calls;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
public class CallInfo {

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    private int id;

    @SerializedName("edit_card_date")
    @ColumnInfo(name = "edit_card_date")
    private String editCardDate;

    @SerializedName("complaints")
    @ColumnInfo(name = "complaints")
    private String complaints;

    @SerializedName("benefit_category_code")
    @ColumnInfo(name = "benefit_category_code")
    private String benefitCategoryCode;

    @SerializedName("inspected")
    @ColumnInfo(name = "inspected")
    private boolean inspected;

    //Personal Info
    @SerializedName("firstname")
    @ColumnInfo(name = "firstname")
    private String firstname;

    @SerializedName("middle_name")
    @ColumnInfo(name = "middle_name")
    private String middleName;

    @SerializedName("lastname")
    @ColumnInfo(name = "lastname")
    private String lastname;

    @SerializedName("sex")
    @ColumnInfo(name = "sex")
    private int sex;

    @SerializedName("residence")
    @ColumnInfo(name = "residence")
    private String residence;

    @SerializedName("phone_number")
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @SerializedName("born_date")
    @ColumnInfo(name = "born_date")
    private String bornDate;

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
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getEditCardDate() {
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBornDate() {
        return bornDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEditCardDate(String editCardDate) {
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

    public void setBornDate(String bornDate) {
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

    public List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add(editCardDate);
        list.add(lastname + " " + firstname + " " + getMiddleName());
        if (sex == 1) list.add("Муж.");
        else list.add("Жен.");
        list.add(bornDate);
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
                .replace("@др", callInfo.getBornDate())
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
        int bornYear = 0;
        if (bornDate.length() == 10) {
            bornYear = Integer.parseInt(bornDate.substring(6));
        }
        return Calendar.getInstance().get(Calendar.YEAR) - bornYear;
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
        result = 31 * result + getSex();
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
