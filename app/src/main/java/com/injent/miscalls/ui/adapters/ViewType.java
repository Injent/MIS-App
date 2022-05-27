package com.injent.miscalls.ui.adapters;

import com.injent.miscalls.R;

public interface ViewType {

    int SETTING_SPINNER = R.layout.item_setting_spinner;
    int SETTING_SWITCH = R.layout.item_setting_switch;
    int FIELD_ADDITIONAL = R.layout.item_field_additional;
    int FIELD_EDIT = R.layout.item_field_edit;
    int SETTINGS_SECTION = R.layout.item_setting_section;

    /**
     * To compare the value obtained from class that extends {@link ViewType}, use the statics of
     * the {@link ViewType} class
     * @return an int that represents the type of layout used in the
     * {@link androidx.recyclerview.widget.RecyclerView} adapter
     */
    int getViewType();
}
