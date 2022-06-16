package com.injent.miscalls.data.database.diagnoses;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;

@Entity
public class Diagnosis {

    public Diagnosis(String[] array) {
        this.id = Integer.parseInt(array[0]);
        if (!array[2].isEmpty())
            this.code = array[2];
        else
            this.code = null;
        if (!array[3].isEmpty())
            this.name = array[3];
        else
            this.name = null;
        if (!array[4].isEmpty())
            this.parentId = Integer.parseInt(array[4]);
        else
            this.parentId = -1;
        if (Integer.parseInt(array[8]) == 1)
            this.notParent = true;
        else
            this.notParent = false;
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
        if (list == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (list.size() > 1) {
                if (i == 0) {
                    sb.append(s);
                }
                sb.append(i + 1).append(". ");
            }
            sb.append(list.get(i).getName());
            if (list.size() > 1) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    @PrimaryKey
    private int id;
    @ColumnInfo(name = "code")
    private String code;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "parent_id")
    private int parentId;
    @ColumnInfo(name = "not_parent")
    private boolean notParent;

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

    public boolean isNotParent() {
        return notParent;
    }

    public void setNotParent(boolean notParent) {
        this.notParent = notParent;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Diagnosis)
            return ((Diagnosis) obj).id == id;
        return false;
    }

    @Override
    public int hashCode() {
        return getCode().hashCode();
    }
}
