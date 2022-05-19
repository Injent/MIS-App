package com.injent.miscalls.ui.settings;

public class SectionLayout implements SettingLayout {

    private int stringResId;

    public SectionLayout(int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringResId() {
        return stringResId;
    }

    public void setStringResId(int stringResId) {
        this.stringResId = stringResId;
    }

    @Override
    public int getViewType() {
        return -1;
    }
}
