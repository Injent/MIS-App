package com.injent.miscalls.data.database.medcall;

import static com.injent.miscalls.util.DateConverter.DATE_PATTERN;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.injent.miscalls.R;
import com.injent.miscalls.network.dto.CallDto;
import com.injent.miscalls.util.DateConverter;
import com.injent.miscalls.ui.overview.Field;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "calls")
@TypeConverters(DateConverter.class)
public class MedCall {

    public MedCall() {
        // Empty body
    }

    /**
     * Used only in {@link CallDto#toDomainObject(CallDto)}
     */
    @Ignore
    public MedCall(
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
            long snils,
            long polis,
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
    private long snils;
    @ColumnInfo(name = "polis")
    private long polis;
    @ColumnInfo(name = "organization")
    private String organization;
    @ColumnInfo(name = "passport")
    private String passport;
    @ColumnInfo(name = "age")
    private int age;
    @Ignore
    private Geo geo;

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

    public boolean isSex() {
        return sex;
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
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(bornDate);
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

    public long getSnils() {
        return snils;
    }

    public void setSnils(long snils) {
        this.snils = snils;
    }

    public long getPolis() {
        return polis;
    }

    public void setPolis(long polis) {
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

    public void setAge(int age) {
        this.age = age;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
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

    public String getSexString() {
        if (isSex()) return "Муж.";
        return "Жен.";
    }

    public String getFullName() {
        return lastname + " " + firstname + " " + getMiddleName();
    }

    public List<Field> getData() {
        List<Field> list = new ArrayList<>();
        list.add(new Field(R.string.callTime, new SimpleDateFormat(DATE_PATTERN +" hh:mm", Locale.getDefault()).format(callTime)));
        list.add(new Field(R.string.dateOfMedCardEdit, new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(editCardDate)));
        list.add(new Field(R.string.fullName, getFullName()));
        list.add(new Field(R.string.sex, getSexString()));
        list.add(new Field(R.string.dateOfBirth, new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(bornDate)));
        list.add(new Field(R.string.residence, residence));
        list.add(new Field(R.string.bcc, benefitCategoryCode));
        list.add(new Field(R.string.snils, String.valueOf(snils)));
        list.add(new Field(R.string.snils, String.valueOf(polis)));
        list.add(new Field(R.string.medOrg, organization));
        list.add(new Field(R.string.document, passport));
        list.add(new Field(R.string.call, phoneNumber));
        return list;
    }
}
