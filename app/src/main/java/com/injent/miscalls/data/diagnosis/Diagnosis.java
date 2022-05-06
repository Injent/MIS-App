package com.injent.miscalls.data.diagnosis;

import androidx.annotation.Nullable;

public class Diagnosis {

    public Diagnosis(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public Diagnosis(String content) {
        this.content = content;
    }

    private int id;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Diagnosis)
            return ((Diagnosis) obj).id == id;
        return false;
    }
}
