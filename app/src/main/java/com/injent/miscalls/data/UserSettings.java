package com.injent.miscalls.data;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.injent.miscalls.R;

public class UserSettings {

    private final Resources res;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences sp;

    public UserSettings(Resources res, SharedPreferences sharedPreferences) {
        this.res = res;
        this.editor = sharedPreferences.edit();
        this.sp = sharedPreferences;
    }

    public int getMode() {
        return sp.getInt(res.getString(R.string.keyMode), 0);
    }

    public UserSettings setMode(int mode) {
        editor.putInt(res.getString(R.string.keyMode), mode);
        return this;
    }

    public boolean isAnonCall() {
        return sp.getBoolean(res.getString(R.string.keyAnonCall), false);
    }

    public UserSettings setAnonCall(boolean anonCall) {
        editor.putBoolean(res.getString(R.string.keyAnonCall), anonCall);
        return this;
    }

    public boolean isAuthed() {
        return sp.getBoolean(res.getString(R.string.keyAuthed), false);
    }

    public UserSettings setAuthed(boolean authed) {
        editor.putBoolean(res.getString(R.string.keyAuthed), authed);
        return this;
    }

    public UserSettings setInit(boolean init) {
        editor.putBoolean(res.getString(R.string.keyInit), init);
        return this;
    }

    public boolean isInit() {
        return sp.getBoolean(res.getString(R.string.keyInit), false);
    }

    public UserSettings setDbDate(String date) {
        editor.putString(res.getString(R.string.keyDbDate), date);
        return this;
    }

    public String getDbDate() {
        return sp.getString(res.getString(R.string.keyDbDate), "");
    }
    
    public void write() {
        editor.apply();
    }
}
