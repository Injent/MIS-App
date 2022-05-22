package com.injent.miscalls.data.database.diagnoses;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Diagnosis {

    public Diagnosis(String[] array) {
        this.id = Integer.parseInt(array[0]);
        if (!array[1].isEmpty())
            this.recCode = array[1];
        else
            this.recCode = "";
        if (!array[2].isEmpty())
            this.code = array[2];
        else
            this.code = "";
        if (!array[3].isEmpty())
            this.name = array[3];
        else
            this.name = "";
        if (!array[4].isEmpty())
            this.parentId = Integer.parseInt(array[4]);
        else
            this.parentId = -1;
        if (!array[5].isEmpty())
            this.addlCode = Integer.parseInt(array[5]);
        else
            this.addlCode = -1;
        if (!array[6].isEmpty())
            this.actual = Integer.parseInt(array[6]);
        else
            this.actual = -1;
        if (!array[7].isEmpty())
            this.date = array[7];
        else
            this.date = "";
    }

    public Diagnosis() {
    }

    public static String listToStringCodes(List<Diagnosis> list, char separator) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getCode());
            if (i != list.size() - 1) {
                sb.append(separator);
                if (separator == ',') {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }

    public static String listToStringIds(List<Diagnosis> list, char separator) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getId());
            if (i != list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static String listToStringNames(List<Diagnosis> list, String s) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (list.size() != 1) {
                sb.append(i).append(1).append(". ");
            }
            sb.append(list.get(i).getName());
            if (i != list.size() - 1) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

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
    @ColumnInfo(name = "call_info_id")
    private int callInfoId;

    @Ignore
    private boolean parent;

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

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

    public int getCallInfoId() {
        return callInfoId;
    }

    public void setCallInfoId(int callInfoId) {
        this.callInfoId = callInfoId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Diagnosis)
            return ((Diagnosis) obj).id == id;
        return false;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getRecCode().hashCode();
        result = 31 * result + getCode().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getParentId();
        result = 31 * result + getAddlCode();
        result = 31 * result + getActual();
        result = 31 * result + getDate().hashCode();
        result = 31 * result + getCallInfoId();
        result = 31 * result + (isParent() ? 1 : 0);
        return result;
    }
}
