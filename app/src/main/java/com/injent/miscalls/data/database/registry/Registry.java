package com.injent.miscalls.data.database.registry;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.medcall.MedCall;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.ui.overview.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "registry")
public class Registry {

    public Registry() {
        // Empty body
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "complaints")
    private String complaints;
    @ColumnInfo(name = "anamnesis")
    private String anamnesis;
    @ColumnInfo(name = "recommendation")
    private String recommendation;
    @ColumnInfo(name = "diagnoses")
    private String diagnosesId;
    @ColumnInfo(name = "create_date")
    private String createDate;
    @ColumnInfo(name = "call_id")
    private int callId;
    @ColumnInfo(name = "surveys")
    private String surveys;
    @ColumnInfo(name = "medical_therapy")
    private String medicationTherapy;
    @ColumnInfo(name = "user_id")
    private int userId;
    @Ignore
    private boolean delete;
    @Ignore
    private Objectively objectively;
    @Ignore
    private MedCall medCall;
    @Ignore
    private List<Diagnosis> diagnoses;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MedCall getCallInfo() {
        return medCall;
    }

    public void setCallInfo(MedCall medCall) {
        this.medCall = medCall;
    }

    public Objectively getObjectively() {
        return objectively;
    }

    public void setObjectively(Objectively objectively) {
        this.objectively = objectively;
    }

    public List<Diagnosis> getDiagnoses() {
        if (diagnoses == null) {
            return Collections.emptyList();
        }
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getAnamnesis() {
        if (anamnesis == null) {
            return "";
        }
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public String getRecommendation() {
        if (recommendation == null)
            return "";
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getDiagnosesId() {
        return diagnosesId;
    }

    public void setDiagnosesId(String diagnosesId) {
        this.diagnosesId = diagnosesId;
    }

    public String getCreateDate() {
        if (createDate == null)
            return "";
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getComplaints() {
        if (complaints == null)
            return "";
        return complaints;
    }

    public void setComplaints(String complaints) {
        this.complaints = complaints;
    }

    public String getSurveys() {
        if (surveys == null)
            return "";
        return surveys;
    }

    public void setSurveys(String surveys) {
        this.surveys = surveys;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getMedicationTherapy() {
        if (medicationTherapy == null) {
            return "";
        }
        return medicationTherapy;
    }

    public void setMedicationTherapy(String medicalTherapy) {
        this.medicationTherapy = medicalTherapy;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Field> getData() {
        List<Field> list = new ArrayList<>();
        list.add(new Field(R.string.createDate, getCreateDate()));
        list.add(new Field(R.string.patientFullName, getCallInfo().getFullName()));
        return list;
    }
}
