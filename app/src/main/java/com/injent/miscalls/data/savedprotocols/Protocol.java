package com.injent.miscalls.data.savedprotocols;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Protocol {

    public Protocol() {
    }

    @Ignore
    public Protocol(String name, String inspection, String treatment, String conclusion, String description) {
        this.name = name;
        this.inspection = inspection;
        this.treatment = treatment;
        this.conclusion = conclusion;
        this.description = description;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "inspection")
    private String inspection;
    @ColumnInfo(name = "treatment")
    private String treatment;
    @ColumnInfo(name = "conclusion")
    private String conclusion;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "patient_id")
    private int patientId;

    public int getPatientId() { return patientId; }

    public void setPatientId(int patientId) { this.patientId = patientId; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public void setInspection(String inspection) { this.inspection = inspection; }

    public void setTreatment(String treatment) { this.treatment = treatment; }

    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInspection() {
        return inspection;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getConclusion() {
        return conclusion;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Protocol) {
            return ((Protocol) obj).id == id;
        }
        return false;
    }
}
