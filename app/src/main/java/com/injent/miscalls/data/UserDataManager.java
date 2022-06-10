package com.injent.miscalls.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.database.registry.RegistryAndObjectively;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UserDataManager {

    private final Resources res;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences sp;

    public UserDataManager(Resources res, SharedPreferences sharedPreferences) {
        this.res = res;
        this.editor = sharedPreferences.edit();
        this.sp = sharedPreferences;
    }

    public void init(Context context) {
        if (isInit()) return;
        File file = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        this
                .setData(R.string.keyMode, 0)
                .setData(R.string.keyAnonCall, false)
                .setData(R.string.keyPdfFilePath, file.getAbsolutePath())
                .setData(R.string.keyDbDate, "-")
                .write();
        editor.putBoolean(res.getString(R.string.keyInit), true);
    }

    public boolean isInit() {
        return sp.getBoolean(res.getString(R.string.keyInit), false);
    }

    /**
     * @param keyResId value from {@link R.string} represents the key that will be used to write 
     * data
     * @param value value of {@link String}, {@link Integer} or {@link Boolean} to write to Shared
     * Preferences
     *
     * @return {@link UserDataManager} which can be used again in the future
     */
    public UserDataManager setData(int keyResId, Object value) {
        String key = res.getString(keyResId);
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }

        return this;
    }

    public String getString(int keyResId) {
        return sp.getString(res.getString(keyResId), null);
    }

    public int getInt(int keyResId) {
        return sp.getInt(res.getString(keyResId), 0);
    }

    public boolean getBoolean(int keyResId) {
        return sp.getBoolean(res.getString(keyResId), false);
    }
    
    public void write() {
        editor.apply();
    }

    public void getUser() {

    }
}
