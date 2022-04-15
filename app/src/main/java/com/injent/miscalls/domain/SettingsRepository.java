package com.injent.miscalls.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.ui.settings.SettingsAdapter;

public class SettingsRepository {

    public void setMode(int mode) {
        SharedPreferences sp = App.getInstance().getSharedPreferences(App.PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("mode",mode);
        editor.apply();

        App.getInstance().setMode(mode);
    }

    public SettingsAdapter getAdapter(Context context) {
        int currentMode = App.getInstance().getMode();
        String[] raw = context.getResources().getStringArray(R.array.modes);
        String[] sorted = new String[raw.length];
        sorted[0] = raw[currentMode];
        for (int i = 0; raw.length > i; i++) {
            if (!raw[i].equals(sorted[i])) {
                sorted[i] = raw[i];
            }
        }
        SettingsAdapter adapter = new SettingsAdapter(context,sorted);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
