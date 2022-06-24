package com.injent.miscalls;

import static android.util.Log.VERBOSE;

import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.injent.miscalls.data.UserDataManager;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.user.User;

import org.osmdroid.config.Configuration;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

public class App extends Application {

    private static WeakReference<App> instance;

    public static final String PREFERENCES_NAME = "settings";
    public static final String ENCRYPTED_PREFERENCES_NAME = "security-data";
    public static final String CHANNEL_ID = "service-v1";

    private static UserDataManager userDataManager;
    private static UserDataManager encryptedUserDataManager;
    private static User user;
    private AppDatabase database;

    public static App getInstance() {
        return instance.get();
    }

    private static void setInstance(App referent) {
        instance = new WeakReference<>(referent);
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
    }

    public static User getUser() { return user; }

    public static void setUser(User user) { App.user = user; }

    public static UserDataManager getUserDataManager() {
        return userDataManager;
    }

    static void setUserSettings(UserDataManager userDataManager) {
        App.userDataManager = userDataManager;
    }

    public static UserDataManager getEncryptedUserDataManager() {
        return encryptedUserDataManager;
    }

    static void setEncryptedUserDataManager(UserDataManager encryptedUserDataManager) {
        App.encryptedUserDataManager = encryptedUserDataManager;
    }
    
    public static SharedPreferences getEncryptedPreferences() {
        SharedPreferences esp = null;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            esp = EncryptedSharedPreferences.create(
                    ENCRYPTED_PREFERENCES_NAME,
                    masterKeyAlias,
                    App.getInstance().getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return esp;
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }

    public void initData() {
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences());
        setEncryptedUserDataManager(new UserDataManager(getResources(), getEncryptedPreferences()));
        setUserSettings(new UserDataManager(getResources(), getSharedPreferences()));

        if (!getSharedPreferences().contains(getString(R.string.keyInit))) {
            getUserDataManager().init();

            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            String passPhrase = new String(bytes);
            getEncryptedUserDataManager()
                    .setData(R.string.keyAuthed, false)
                    .setData(R.string.keyDb, passPhrase)
                    .write();
            Toast.makeText(getApplicationContext(), passPhrase, Toast.LENGTH_LONG).show();
        }

        AppDatabase.getInstance(
                getApplicationContext(),
                String.valueOf(getEncryptedUserDataManager().getInt(R.string.keyDb)).toCharArray()
        );
    }
}
