package com.injent.miscalls;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.data.UserDataManager;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;
import com.injent.miscalls.data.database.registry.RegistryDao;
import com.injent.miscalls.data.database.user.UserDao;
import com.injent.miscalls.data.recommendation.RecommendationDao;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;

public class App extends Application {

    private static WeakReference<App> instance;

    public static final String PREFERENCES_NAME = "settings";
    public static final String ENCRYPTED_PREFERENCES_NAME = "security-data";
    public static final String CHANNEL_ID = "service-v1";

    private DiagnosisDao diagnosisDao;
    private RegistryDao registryDao;
    private RecommendationDao recommendationDao;
    private CallInfoDao callInfoDao;
    private UserDao userDao;

    private static UserDataManager userDataManager;
    private static UserDataManager encryptedUserDataManager;
    private static User user;

    public static App getInstance() {
        return instance.get();
    }

    private static void setInstance(App referent) {
        instance = new WeakReference<>(referent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setInstance(this);
        setUserSettings(new UserDataManager(getResources(), getSharedPreferences()));
        setEncryptedUserDataManager(new UserDataManager(getResources(), getEncryptedPreferences()));
        initSettings();
        connectDatabase();
    }

    public void connectDatabase() {
        if (encryptedUserDataManager.getBoolean(R.string.keyAuthed)) {
            SharedPreferences esp = getEncryptedPreferences();
            String key = esp.getString(getString(R.string.keyToken),null);

            AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext(), key.toCharArray());

            diagnosisDao = appDatabase.diagnosisDao();
            registryDao = appDatabase.registryDao();
            recommendationDao = appDatabase.recommendationDao();
            callInfoDao = appDatabase.callInfoDao();
            userDao = appDatabase.userDao();
        }
    }

    public void regUser(User user) {
        App.setUser(user);
        encryptedUserDataManager
                .setData(R.string.keyAuthed,true)
                .setData(R.string.keyToken, user.getToken().getValue())
                .write();
        connectDatabase();
    }

    public static UserDataManager getUserDataManager() {
        return userDataManager;
    }

    private static void setUserSettings(UserDataManager userDataManager) {
        App.userDataManager = userDataManager;
    }

    public static UserDataManager getEncryptedUserDataManager() {
        return encryptedUserDataManager;
    }

    private static void setEncryptedUserDataManager(UserDataManager encryptedUserDataManager) {
        App.encryptedUserDataManager = encryptedUserDataManager;
    }

    public DiagnosisDao getDiagnosisDao() {
        return diagnosisDao;
    }

    public RegistryDao getRegistryDao() {
        return registryDao;
    }

    public RecommendationDao getRecommendationDao() {
        return recommendationDao;
    }

    public CallInfoDao getCallInfoDao() {
        return callInfoDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public static User getUser() { return user; }

    public static void setUser(User user) { App.user = user; }

    public SharedPreferences getSharedPreferences() { return getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE); }
    
    public SharedPreferences getEncryptedPreferences() {
        SharedPreferences esp = null;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            esp = EncryptedSharedPreferences.create(
                    ENCRYPTED_PREFERENCES_NAME,
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return esp;
    }

    private void initSettings() {
        if (!userDataManager.isInit()) {
            userDataManager.init(getApplicationContext());
            encryptedUserDataManager.setData(R.string.keyAuthed, false).write();
        }
    }

    public static void hideKeyBoard(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
