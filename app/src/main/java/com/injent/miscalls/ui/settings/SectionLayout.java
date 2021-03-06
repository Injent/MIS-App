package com.injent.miscalls.ui.settings;

import com.injent.miscalls.util.ViewType;

public class SectionLayout implements ViewType {

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
        return ViewType.SETTINGS_SECTION;
    }
}
