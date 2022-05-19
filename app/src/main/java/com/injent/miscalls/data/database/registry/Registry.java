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
    }

    @Ignore
    private CallInfo callInfo;

    @Ignore
    private List<Diagnosis> diagnoses;

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "inspection")
    private String inspection;

    @ColumnInfo(name = "recommendation")
    private String recommendation;

    @ColumnInfo(name = "diagnoses")
    private String diagnosesId;

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

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getInspection() {
        return inspection;
    }

    public void setInspection(String inspection) {
        this.inspection = inspection;
    }

    public String getRecommendation() {
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
}
