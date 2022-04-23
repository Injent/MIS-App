package com.injent.miscalls.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.ui.settings.SettingsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SettingsRepository {

    public void setMode(int mode) {
        SharedPreferences sp = App.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("mode",mode).apply();
        App.getInstance().setMode(mode);
    }

    public SettingsAdapter getAdapter(Context context) {
        List<String> modeList = Arrays.asList(context.getResources().getStringArray(R.array.modes));
        return new SettingsAdapter(context, modeList);
    }
}
