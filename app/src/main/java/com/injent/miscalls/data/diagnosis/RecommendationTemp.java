package com.injent.miscalls.data.diagnosis;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecommendationTemp {

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
        if (obj instanceof RecommendationTemp)
            return ((RecommendationTemp) obj).getId() == id;
        return false;
    }
}
