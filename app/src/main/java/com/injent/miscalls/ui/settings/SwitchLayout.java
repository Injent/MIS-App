package com.injent.miscalls.ui.settings;

public class SwitchLayout implements SettingLayout {

    private int drawableResId;
    private int drawableColorResId;
    private int stringResId;
    private int stringHintResId;
    private boolean state;
    private int thumbResId;
    private int trackResId;
    private OnFlickListener listener;

    public SwitchLayout(int drawableResId, int drawableColorResId, int stringResId, int stringHintResId, boolean state, int thumbResId, int trackResId, OnFlickListener listener) {
        this.drawableResId = drawableResId;
        this.drawableColorResId = drawableColorResId;
        this.stringResId = stringResId;
        this.stringHintResId = stringHintResId;
        this.state = state;
        this.thumbResId = thumbResId;
        this.trackResId = trackResId;
        this.listener = listener;
    }

    @Override
    public int getViewType() {
        return 1;
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

    public int getStringResId() {
        return stringResId;
    }

    public void setStringResId(int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringHintResId() {
        return stringHintResId;
    }

    public void setStringHintResId(int stringHintResId) {
        this.stringHintResId = stringHintResId;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public OnFlickListener getListener() {
        return listener;
    }

    public void setListener(OnFlickListener listener) {
        this.listener = listener;
    }

    public int getThumbResId() {
        return thumbResId;
    }

    public void setThumbResId(int thumbResId) {
        this.thumbResId = thumbResId;
    }

    public int getTrackResId() {
        return trackResId;
    }

    public void setTrackResId(int trackResId) {
        this.trackResId = trackResId;
    }

    public interface OnFlickListener {
        void onFlick(boolean state);
    }
}
