package com.injent.miscalls.data.database.registry;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;

import java.util.List;

@Entity
public class Registry {

    public Registry() {
        // Empty
    }

    @Ignore
    private Objectively objectively;

    @Ignore
    private CallInfo callInfo;

    @Ignore
    private List<Diagnosis> diagnoses;

    @PrimaryKey
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

    @ColumnInfo(name = "obj_id")
    private int objId;

    @ColumnInfo(name = "surveys")
    private String surveys;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CallInfo getCallInfo() {
        return callInfo;
    }

    public void setCallInfo(CallInfo callInfo) {
        this.callInfo = callInfo;
    }

    public Objectively getObjectively() {
        return objectively;
    }

    public void setObjectively(Objectively objectively) {
        this.objectively = objectively;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getAnamnesis() {
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

    public int getObjId() {
        return objId;
    }

    public void setObjId(int objId) {
        this.objId = objId;
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
}
