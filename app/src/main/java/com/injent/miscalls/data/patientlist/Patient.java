package com.injent.miscalls.data.patientlist;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Patient {

    public Patient() {
    }

    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("card_number")
    @ColumnInfo(name = "card_number")
    private String cardNumber;

    @SerializedName("edit_card_date")
    @ColumnInfo(name = "edit_card_date")
    private String editCardDate;

    @SerializedName("complaints")
    @ColumnInfo(name = "complaints")
    private String complaints;

    @SerializedName("benefit_category_code")
    @ColumnInfo(name = "benefit_category_code")
    private String benefitCategoryCode;

    @SerializedName("insurance_company")
    @ColumnInfo(name = "insurance_company")
    private String insuranceCompany;

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
    private boolean sex;

    @SerializedName("reg_address")
    @ColumnInfo(name = "reg_address")
    private String regAddress;

    @SerializedName("terrain")
    @ColumnInfo(name = "terrain")
    private boolean terrain;

    @SerializedName("phone_number")
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @SerializedName("born_date")
    @ColumnInfo(name = "born_date")
    private String bornDate;

    @SerializedName("document")
    @ColumnInfo(name = "document")
    private String document;

    @SerializedName("snils")
    @ColumnInfo(name = "snils")
    private String snils;

    @SerializedName("polis")
    @ColumnInfo(name = "polis")
    private String polis;

    public int getId() { return id; }

    public String getCardNumber() { return cardNumber; }

    public String getComplaints() { return complaints; }

    public boolean isInspected() { return inspected; }

    public String getRegAddress() { return regAddress; }

    public String getEditCardDate() { return editCardDate; }

    public String getBenefitCategoryCode() { return benefitCategoryCode; }

    public String getInsuranceCompany() { return insuranceCompany; }

    public String getFirstname() { return firstname; }

    public String getLastname() { return lastname; }

    public boolean isSex() { return sex; }

    public boolean isTerrain() { return terrain; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getBornDate() { return bornDate; }

    public String getDocument() { return document; }

    public String getSnils() { return snils; }

    public String getPolis() { return polis; }

    public void setId(int id) { this.id = id; }

    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public void setEditCardDate(String editCardDate) { this.editCardDate = editCardDate; }

    public void setComplaints(String complaints) { this.complaints = complaints; }

    public void setBenefitCategoryCode(String benefitCategoryCode) { this.benefitCategoryCode = benefitCategoryCode; }

    public void setInsuranceCompany(String insuranceCompany) { this.insuranceCompany = insuranceCompany; }

    public void setInspected(boolean inspected) { this.inspected = inspected; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setSex(boolean sex) { this.sex = sex; }

    public void setRegAddress(String regAddress) { this.regAddress = regAddress; }

    public void setTerrain(boolean terrain) { this.terrain = terrain; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setBornDate(String bornDate) { this.bornDate = bornDate; }

    public void setDocument(String document) { this.document = document; }

    public void setSnils(String snils) { this.snils = snils; }

    public void setPolis(String polis) { this.polis = polis; }

    public List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add(editCardDate);
        list.add(lastname + " " + firstname + " " + getMiddleName());
        if (sex) list.add("Муж.");
        else list.add("Жен.");
        list.add(bornDate);
        list.add(regAddress);
        if (terrain) list.add("Городская");
        else list.add("Сельская");
        list.add(polis);
        list.add(snils);
        list.add(insuranceCompany);
        list.add(benefitCategoryCode);
        list.add(document);
        list.add(phoneNumber);

        return list;
    }

    public String getMiddleName() {
        if (middleName == null) {
            middleName = " ";
        }
        return middleName;
    }

    public String getShortInfo() {
        return lastname + " " + firstname;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Patient)
            return ((Patient) obj).getId() == id;
        return false;
    }
}
