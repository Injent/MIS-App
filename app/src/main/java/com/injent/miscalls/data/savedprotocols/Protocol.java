package com.injent.miscalls.data.savedprotocols;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Protocol {

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

    public void setId(int id) { this.id = id; }

    public void setName(String name) { this.name = name; }

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
