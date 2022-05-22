package com.injent.miscalls.ui.editor;

import com.injent.miscalls.ui.ViewType;

public class EditFieldLayout implements ViewType {

    private int nameStringResId;
    private int hintStringResId;

    public EditFieldLayout(int nameStringResId, int hintStringResId) {
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
        return 0;
    }
}
