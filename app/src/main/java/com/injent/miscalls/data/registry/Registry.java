package com.injent.miscalls.data.registry;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Registry {

    public Registry() {
    }

    @Ignore
    public Registry(String name, String inspection, String recommendations, int diagnosisId) {
        this.name = name;
        this.inspection = inspection;
        this.recommendations = recommendations;
        this.diagnosisId = diagnosisId;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "diagnosis_name")
    private String diagnosisCode;
    @ColumnInfo(name = "inspection")
    private String inspection;
    @ColumnInfo(name = "recommendations")
    private String recommendations;
    @ColumnInfo(name = "diagnosis_id")
    private int diagnosisId;
    @ColumnInfo(name = "call_id")
    private int callId;

    public int getCallId() {
        return callId;
    }

    public void setCallId(int id) {
        this.callId = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(int diagnosis) {
        this.diagnosisId = diagnosis;
    }

    public void setInspection(String inspection) {
        this.inspection = inspection;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInspection() {
        return inspection;
    }

    public String getRecommendations() {
        return recommendations;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Registry) {
            return ((Registry) obj).id == id;
        }
        return false;
    }
}
