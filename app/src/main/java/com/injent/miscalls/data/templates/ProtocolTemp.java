package com.injent.miscalls.data.templates;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProtocolTemp {

    public ProtocolTemp() {
    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "treatment")
    private String treatment;

    @ColumnInfo(name = "conclusion")
    private String conclusion;

    @ColumnInfo(name = "inspection")
    private String inspection;

    public String getInspection() { return inspection; }

    public void setInspection(String inspection) { this.inspection = inspection; }

    public void setId(int id) { this.id = id; }

    public String getTreatment() { return treatment; }

    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getConclusion() { return conclusion; }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ProtocolTemp)
            return ((ProtocolTemp) obj).getId() == id;
        return false;
    }
}
