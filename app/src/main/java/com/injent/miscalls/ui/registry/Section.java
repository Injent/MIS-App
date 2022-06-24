package com.injent.miscalls.ui.registry;

import androidx.annotation.Nullable;

public class Section {

    private String sectionName;
    private int sectionType;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getSectionType() {
        return sectionType;
    }

    public void setSectionType(int sectionType) {
        this.sectionType = sectionType;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Section) {
            return ((Section) obj).sectionType == sectionType;
        } return false;
    }

    public static class Type {

        private Type() {
            throw new IllegalStateException("Utility class");
        }

        public static final int
                SUBMIT_ALL = 0,
                DELETE_ALL = 1;
    }
}
