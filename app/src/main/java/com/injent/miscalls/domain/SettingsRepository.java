package com.injent.miscalls.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.ui.settings.SettingsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsRepository {

    public void setMode(int mode) {
        SharedPreferences sp = App.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("mode",mode);
        editor.apply();

        App.getInstance().setMode(mode);
    }

    public SettingsAdapter getAdapter(Context context) {
        int currentMode = App.getInstance().getMode();
        String[] raw = context.getResources().getStringArray(R.array.modes);
        List<String> sorted = new ArrayList<>();
        sorted.add(raw[currentMode]);
        for (String s : raw) {
            if (!s.equals(sorted.get(0))) {
                sorted.add(s);
            }
        }
        return new SettingsAdapter(context, sorted);
    }
}
