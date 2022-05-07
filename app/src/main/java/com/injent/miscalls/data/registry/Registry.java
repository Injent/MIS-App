package com.injent.miscalls.data.registry;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.injent.miscalls.data.calllist.Patient;

@Entity
public class Registry {

    public Registry() {
    }

    @Ignore
    public Registry(String name, String inspectionContent, String treatment, String recommendations, String diagnosis) {
        this.name = name;
        this.inspectionContent = inspectionContent;
        this.treatment = treatment;
        this.recommendations = recommendations;
        this.diagnosis = diagnosis;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "inspection")
    private String inspectionContent;
    @ColumnInfo(name = "treatment")
    private String treatment;
    @ColumnInfo(name = "recommendations")
    private String recommendations;
    @ColumnInfo(name = "diagnosis")
    private String diagnosis;
    @ColumnInfo(name = "call_id")
    private int callId;

    public int getCallId() { return callId; }

    public void setCallId(int id) { this.callId = id; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public String getDiagnosis() { return diagnosis; }

    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public void setInspectionContent(String inspectionContent) { this.inspectionContent = inspectionContent; }

    public void setTreatment(String treatment) { this.treatment = treatment; }

    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInspectionContent() {
        return inspectionContent;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public Registry build(Patient patient) {
        callId = patient.getId();
        return this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Registry) {
            return ((Registry) obj).id == id;
        }
        return false;
    }
}
