package com.injent.miscalls.ui.adapters;

public class AdditionalField implements ViewType {

    private String name;

    public AdditionalField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return ViewType.FIELD_ADDITIONAL;
    }
}
