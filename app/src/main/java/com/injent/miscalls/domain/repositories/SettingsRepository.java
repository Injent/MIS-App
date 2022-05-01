package com.injent.miscalls.domain.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.ui.settings.SpinnerAdapter;

import java.util.Arrays;
import java.util.List;

public class SettingsRepository {

    public void setMode(int mode) {
        SharedPreferences sp = App.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("mode",mode).apply();
        App.setMode(mode);
    }

    public SpinnerAdapter getAdapter(Context context) {
        List<String> modeList = Arrays.asList(context.getResources().getStringArray(R.array.modes));
        return new SpinnerAdapter(context, modeList);
    }
}
