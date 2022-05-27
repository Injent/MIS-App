package com.injent.miscalls.ui.adapters;

public class Field implements ViewType {

    private int nameStringResId;
    private int hintStringResId;

    public Field(int nameStringResId, int hintStringResId) {
        this.nameStringResId = nameStringResId;
        this.hintStringResId = hintStringResId;
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

    @Override
    public int getViewType() {
        return ViewType.FIELD_EDIT;
    }
}
