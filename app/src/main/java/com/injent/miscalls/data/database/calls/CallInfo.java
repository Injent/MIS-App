package com.injent.miscalls.data.database.calls;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.injent.miscalls.data.database.DateConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "calls")
@TypeConverters(DateConverter.class)
public class CallInfo {

    public static final String pattern = "dd-MM-yyyy";

    public CallInfo() {
    }

    @Ignore
    public CallInfo(
            int id,
            int userId,
            Date editCardDate,
            Date callTime,
            String complaints,
            String benefitCategoryCode,
            boolean inspected,
            String firstname,
            String middleName,
            String lastname,
            boolean sex,
            String residence,
            String phoneNumber,
            Date bornDate,
            String snils,
            String polis,
            String organization,
            String passport,
            int age,
            Geo geo
    ) {
        this.id = id;
        this.userId = userId;
        this.editCardDate = editCardDate;
        this.callTime = callTime;
        this.complaints = complaints;
        this.benefitCategoryCode = benefitCategoryCode;
        this.inspected = inspected;
        this.firstname = firstname;
        this.middleName = middleName;
        this.lastname = lastname;
        this.sex = sex;
        this.residence = residence;
        this.phoneNumber = phoneNumber;
        this.bornDate = bornDate;
        this.snils = snils;
        this.polis = polis;
        this.organization = organization;
        this.passport = passport;
        this.age = age;
        this.geo = geo;
    }

    @Ignore
    private Geo geo;

    @ColumnInfo(name = "id")
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "user_id")
    private int userId;
    @ColumnInfo(name = "edit_card_date")
    private Date editCardDate;
    @ColumnInfo(name = "call_time")
    private Date callTime;
    @ColumnInfo(name = "complaints")
    private String complaints;
    @ColumnInfo(name = "benefit_category_code")
    private String benefitCategoryCode;
    @ColumnInfo(name = "inspected")
    private boolean inspected;
    @ColumnInfo(name = "first_name")
    private String firstname;
    @ColumnInfo(name = "middle_name")
    private String middleName;
    @ColumnInfo(name = "last_name")
    private String lastname;
    @ColumnInfo(name = "sex")
    private boolean sex;
    @ColumnInfo(name = "residence")
    private String residence;
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    @ColumnInfo(name = "born_date")
    private Date bornDate;
    @ColumnInfo(name = "snils")
    private String snils;
    @ColumnInfo(name = "polis")
    private String polis;
    @ColumnInfo(name = "organization")
    private String organization;
    @ColumnInfo(name = "passport")
    private String passport;
    @ColumnInfo(name = "age")
    private int age;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        return bornDate;
    }

    public String getBornDateString() {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(bornDate);
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

    public void setAge(int age) {
        this.age = age;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add(new SimpleDateFormat(pattern +" hh:mm", Locale.getDefault()).format(callTime));
        list.add(new SimpleDateFormat(pattern, Locale.getDefault()).format(editCardDate));
        list.add(lastname + " " + firstname + " " + getMiddleName());
        if (sex) list.add("Муж.");
        else list.add("Жен.");
        list.add(new SimpleDateFormat(pattern, Locale.getDefault()).format(bornDate));
        list.add(residence);
        list.add(benefitCategoryCode);
        list.add(snils);
        list.add(polis);
        list.add(organization);
        list.add(passport);
        list.add(phoneNumber);
        return list;
    }

    public String getMiddleName() {
        if (middleName == null) {
            middleName = " ";
        }
        return middleName;
    }

    public int getAge() {
        return age;
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
        result = 31 * result + getUserId();
        result = 31 * result + (getEditCardDate() != null ? getEditCardDate().hashCode() : 0);
        result = 31 * result + (getCallTime() != null ? getCallTime().hashCode() : 0);
        result = 31 * result + (getComplaints() != null ? getComplaints().hashCode() : 0);
        result = 31 * result + (getBenefitCategoryCode() != null ? getBenefitCategoryCode().hashCode() : 0);
        result = 31 * result + (isInspected() ? 1 : 0);
        result = 31 * result + (getFirstname() != null ? getFirstname().hashCode() : 0);
        result = 31 * result + (getMiddleName() != null ? getMiddleName().hashCode() : 0);
        result = 31 * result + (getLastname() != null ? getLastname().hashCode() : 0);
        result = 31 * result + (getSex() ? 1 : 0);
        result = 31 * result + (getResidence() != null ? getResidence().hashCode() : 0);
        result = 31 * result + (getPhoneNumber() != null ? getPhoneNumber().hashCode() : 0);
        result = 31 * result + (getBornDate() != null ? getBornDate().hashCode() : 0);
        result = 31 * result + (getSnils() != null ? getSnils().hashCode() : 0);
        result = 31 * result + (getPolis() != null ? getPolis().hashCode() : 0);
        result = 31 * result + (getOrganization() != null ? getOrganization().hashCode() : 0);
        result = 31 * result + (getPassport() != null ? getPassport().hashCode() : 0);
        result = 31 * result + getAge();
        return result;
    }
}
