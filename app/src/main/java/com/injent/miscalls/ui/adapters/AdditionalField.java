package com.injent.miscalls.ui.adapters;

public class AdditionalField implements ViewType {

    private int nameResId;
    private int index;
    private int viewType;
    private int extraResId;

    public AdditionalField(int nameResId, int index, int viewType) {
        this.nameResId = nameResId;
        this.index = index;
        this.viewType = viewType;
    }

    public AdditionalField(int nameResId, int index, int viewType, int extraResId) {
        this.nameResId = nameResId;
        this.index = index;
        this.viewType = viewType;
        this.extraResId = extraResId;
    }

    public int getNameResId() {
        return nameResId;
    }

    public void setNameResId(int nameResId) {
        this.nameResId = nameResId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getExtraResId() {
        return extraResId;
    }

    public void setExtraResId(int extraResId) {
        this.extraResId = extraResId;
    }

    @Override
    public int getViewType() {
        return viewType;
    }
}
