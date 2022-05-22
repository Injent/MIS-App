package com.injent.miscalls.data.recommendation;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recommendation {

    public Recommendation() {
    }

    public Recommendation(String[] array) {
        this.id = Integer.parseInt(array[0]);
        this.name = array[1];
        this.content = array[2];
        this.description = array[3];
    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "content")
    private String content;

    public void setId(int id) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Recommendation)
            return ((Recommendation) obj).getId() == id;
        return false;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getContent().hashCode();
        return result;
    }
}
