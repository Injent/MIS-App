package com.injent.miscalls.ui.settings;

public class SpinnerLayout implements SettingLayout {

    private int drawableResId;
    private int drawableColorResId;
    private int stringResId;
    private int selectedItemPosition;
    private int stringArrayResId;
    private OnItemClickListener listener;

    public SpinnerLayout(int drawableResId, int drawableColorResId, int stringResId, int selectedItemPosition, int stringArrayResId, OnItemClickListener listener) {
        this.drawableResId = drawableResId;
        this.drawableColorResId = drawableColorResId;
        this.stringResId = stringResId;
        this.selectedItemPosition = selectedItemPosition;
        this.stringArrayResId = stringArrayResId;
        this.listener = listener;
    }

    @Override
    public int getViewType() {
        return 0;
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

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
    }

    public int getStringArrayResId() {
        return stringArrayResId;
    }

    public void setStringArrayResId(int stringArrayResId) {
        this.stringArrayResId = stringArrayResId;
    }

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onOpen();
        void onSelect(int position);
    }
}
