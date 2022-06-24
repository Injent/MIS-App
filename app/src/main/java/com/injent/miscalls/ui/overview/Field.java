package com.injent.miscalls.ui.overview;

import com.injent.miscalls.util.ViewType;

public class Field implements ViewType {

    public static final int COMPLAINTS = 0;
    public static final int ANAMNESIS = 1;
    public static final int GENERAL_STATE = 2;
    public static final int BODY_BUILD = 3;
    public static final int SKIN = 4;
    public static final int NODES = 5;
    public static final int PHARYNX = 6;
    public static final int BREATHING = 7;
    public static final int ARTERIAL_PRESSURE = 8;
    public static final int PULSE = 9;
    public static final int PENSIONER = 10;
    public static final int SICK = 11;
    public static final int GLANDS = 12;
    public static final int TEMPERATURE = 13;
    public static final int ABDOMEN = 14;
    public static final int LIVER = 15;
    public static final int SURVEYS = 16;
    public static final int MEDICAL_THERAPY = 17;

    private int nameStringResId;
    private int hintStringResId;
    private String value;
    private int index;

    public Field(int nameStringResId, String value) {
        this.nameStringResId = nameStringResId;
        this.value = value;
    }

    public Field(int nameStringResId, int hintStringResId, int index) {
        this.nameStringResId = nameStringResId;
        this.hintStringResId = hintStringResId;
        this.index = index;
    }

    public int getNameStringResId() {
        return nameStringResId;
    }

    public void setNameStringResId(int nameStringResId) {
        this.nameStringResId = nameStringResId;
    }

    public int getHintStringResId() {
        return hintStringResId;
    }

    public void setHintStringResId(int hintStringResId) {
        this.hintStringResId = hintStringResId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int getViewType() {
        return ViewType.FIELD_EDIT;
    }
}
