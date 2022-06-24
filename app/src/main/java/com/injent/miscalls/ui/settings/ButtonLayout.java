package com.injent.miscalls.ui.settings;

import android.view.View;

import com.injent.miscalls.util.ViewType;

public class ButtonLayout implements ViewType {

    private int drawableResId;
    private int drawableColorResId;
    private int nameResId;
    private int buttonTextResId;

    public ButtonLayout(int drawableResId, int drawableColorResId, int nameResId, int buttonTextResId, View.OnClickListener listener) {
        this.drawableResId = drawableResId;
        this.drawableColorResId = drawableColorResId;
        this.nameResId = nameResId;
        this.buttonTextResId = buttonTextResId;
        this.listener = listener;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public int getDrawableColorResId() {
        return drawableColorResId;
    }

    public void setDrawableColorResId(int drawableColorResId) {
        this.drawableColorResId = drawableColorResId;
    }

    public int getButtonTextResId() {
        return buttonTextResId;
    }

    public void setButtonTextResId(int buttonTextResId) {
        this.buttonTextResId = buttonTextResId;
    }

    private View.OnClickListener listener;

    public int getNameResId() {
        return nameResId;
    }

    public void setNameResId(int nameResId) {
        this.nameResId = nameResId;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getViewType() {
        return ViewType.SETTINGS_BUTTON;
    }
}
