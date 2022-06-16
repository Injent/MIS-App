package com.injent.miscalls.ui.adapters;

public class AdditionalField implements ViewType {

    private int nameResId;
    private int index;
    private int viewType;
    private int extraResId;
    private String data;

    public AdditionalField(int extraResId) {
        viewType = ViewType.FIELD_ADDITIONAL_SPACE;
    }

    public AdditionalField(int nameResId, int index, int viewType, String data) {
        this.nameResId = nameResId;
        this.index = index;
        this.viewType = viewType;
        this.data = data;
    }

    public AdditionalField(int nameResId, int index, int viewType, int extraResId, String data) {
        this.nameResId = nameResId;
        this.index = index;
        this.viewType = viewType;
        this.extraResId = extraResId;
        this.data = data;
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

    public String getData() {
        if (data == null) {
            return "";
        }
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int getViewType() {
        return viewType;
    }
}
