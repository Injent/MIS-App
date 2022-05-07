package com.injent.miscalls.data.diagnosis;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity
public class Diagnosis {

    @PrimaryKey
    private int id;
    @ColumnInfo(name = "rec_code")
    private String recCode;
    @ColumnInfo(name = "code")
    private String code;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "parent_id")
    private int parentId;
    @ColumnInfo(name = "addl_code")
    private int addlCode;
    @ColumnInfo(name = "actual")
    private int actual;
    @ColumnInfo(name = "date")
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecCode() {
        return recCode;
    }

    public void setRecCode(String recCode) {
        this.recCode = recCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getAddlCode() {
        return addlCode;
    }

    public void setAddlCode(int addlCode) {
        this.addlCode = addlCode;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Diagnosis)
            return ((Diagnosis) obj).id == id;
        return false;
    }
}
