package com.injent.miscalls.data.patientlist;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Patient implements Parcelable {

    public Patient() {
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @SerializedName("card_number")
    @ColumnInfo(name = "card_number")
    public String cardNumber;

    @SerializedName("edit_card_date")
    @ColumnInfo(name = "edit_card_date")
    public String editCardDate;

    @SerializedName("complaints")
    @ColumnInfo(name = "complaints")
    public String complaints;

    @SerializedName("benefit_category_code")
    @ColumnInfo(name = "benefit_category_code")
    public String benefitCategoryCode;

    @SerializedName("insurance_company")
    @ColumnInfo(name = "insurance_company")
    public String insuranceCompany;

    @SerializedName("inspected")
    @ColumnInfo(name = "inspected")
    public boolean inspected;

    //Personal Info
    @SerializedName("firstname")
    @ColumnInfo(name = "firstname")
    public String firstname;

    @SerializedName("middle_name")
    @ColumnInfo(name = "middle_name")
    public String middleName;

    @SerializedName("lastname")
    @ColumnInfo(name = "lastname")
    public String lastname;

    @SerializedName("sex")
    @ColumnInfo(name = "sex")
    public boolean sex;

    @SerializedName("reg_address")
    @ColumnInfo(name = "reg_address")
    public String regAddress;

    @SerializedName("terrain")
    @ColumnInfo(name = "terrain")
    public boolean terrain;

    @SerializedName("phone_number")
    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    @SerializedName("born_date")
    @ColumnInfo(name = "born_date")
    public String bornDate;

    @SerializedName("document")
    @ColumnInfo(name = "document")
    public String document;

    @SerializedName("snils")
    @ColumnInfo(name = "snils")
    public String snils;

    @SerializedName("polis")
    @ColumnInfo(name = "polis")
    public String polis;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;

        Patient that = (Patient) o;

        if (id != that.id) return false;
        if (inspected != that.inspected) return false;
        if (sex != that.sex) return false;
        if (terrain != that.terrain) return false;
        if (!cardNumber.equals(that.cardNumber)) return false;
        if (!editCardDate.equals(that.editCardDate)) return false;
        if (complaints != null ? !complaints.equals(that.complaints) : that.complaints != null)
            return false;
        if (benefitCategoryCode != null ? !benefitCategoryCode.equals(that.benefitCategoryCode) : that.benefitCategoryCode != null)
            return false;
        if (insuranceCompany != null ? !insuranceCompany.equals(that.insuranceCompany) : that.insuranceCompany != null)
            return false;
        if (!firstname.equals(that.firstname)) return false;
        if (middleName != null && !middleName.equals(that.middleName)) return false;
        if (!lastname.equals(that.lastname)) return false;
        if (!regAddress.equals(that.regAddress)) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null)
            return false;
        if (!bornDate.equals(that.bornDate)) return false;
        if (!document.equals(that.document)) return false;
        if (!snils.equals(that.snils)) return false;
        return polis.equals(that.polis);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + cardNumber.hashCode();
        result = 31 * result + editCardDate.hashCode();
        result = 31 * result + (complaints != null ? complaints.hashCode() : 0);
        result = 31 * result + (benefitCategoryCode != null ? benefitCategoryCode.hashCode() : 0);
        result = 31 * result + (insuranceCompany != null ? insuranceCompany.hashCode() : 0);
        result = 31 * result + (inspected ? 1 : 0);
        result = 31 * result + firstname.hashCode();
        if (middleName != null)
            result = 31 * result + middleName.hashCode();
        result = 31 * result + lastname.hashCode();
        result = 31 * result + (sex ? 1 : 0);
        result = 31 * result + regAddress.hashCode();
        result = 31 * result + (terrain ? 1 : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + bornDate.hashCode();
        result = 31 * result + document.hashCode();
        result = 31 * result + snils.hashCode();
        result = 31 * result + polis.hashCode();
        return result;
    }

    protected Patient(Parcel in) {
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
