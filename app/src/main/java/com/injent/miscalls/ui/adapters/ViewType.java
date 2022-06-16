package com.injent.miscalls.ui.adapters;

import com.injent.miscalls.R;

public interface ViewType {

    int SETTING_SPINNER = R.layout.item_setting_spinner;
    int SETTING_SWITCH = R.layout.item_setting_switch;
    int FIELD_ADDITIONAL_TEXT = R.layout.item_field_additional;
    int FIELD_ADDITIONAL_DECIMAL = R.layout.item_field_decimal;
    int FIELD_ADDITIONAL_DOUBLE_DECIMAL = R.layout.item_field_double_dec;
    int FIELD_ADDITIONAL_SPINNER = R.layout.item_field_spinner;
    int FIELD_ADDITIONAL_CHECKBOX = R.layout.item_field_checkbox;
    int FIELD_ADDITIONAL_SPACE = R.layout.item_field_space;

    int FIELD_EDIT = R.layout.item_field_edit;
    int SETTINGS_SECTION = R.layout.item_setting_section;
    int SETTINGS_BUTTON = R.layout.item_settings_button;

    int VIEW_PAGER_CALL = 0;
    int VIEW_PAGER_OVERVIEW = 1;

    /**
     * To compare the value obtained from class that extends {@link ViewType}, use the statics of
     * the {@link ViewType} class
     * @return an int that represents id of layout used in the
     * {@link androidx.recyclerview.widget.RecyclerView} custom adapter
     */
    int getViewType();
}
