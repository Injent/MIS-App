package com.injent.miscalls.data.database;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.calls.Geo;
import com.injent.miscalls.data.database.calls.GeoDao;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.database.user.Organization;
import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.data.database.user.UserDao;
import com.injent.miscalls.data.recommendation.Medication;
import com.injent.miscalls.data.recommendation.RecommendationDao;
import com.injent.miscalls.data.database.registry.RegistryDao;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;

@Database(entities = {Geo.class, Diagnosis.class, Registry.class, CallInfo.class, Medication.class, Objectively.class, User.class, Organization.class, Token.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {


    public static final String DB_NAME = "database.db";
    private static final String MKB10_FILE_PATH = "databases/mkb10.csv";
    private static final String RECOMMENDATIONS_FILE_PATH = "databases/recommendations.csv";

    private static AppDatabase instance;
    private static UserDao userDao;
    private static CallInfoDao callInfoDao;
    private static RegistryDao registryDao;
    private static DiagnosisDao diagnosisDao;
    private static GeoDao geoDao;

    public static UserDao getUserDao() {
        return userDao;
    }

    public static CallInfoDao getCallInfoDao() {
        return callInfoDao;
    }

    public static RegistryDao getRegistryDao() {
        return registryDao;
    }

    public static DiagnosisDao getDiagnosisDao() {
        return diagnosisDao;
    }

    public static GeoDao getGeoDao() {
        return geoDao;
    }

    public abstract DiagnosisDao diagnosisDao();
    public abstract CallInfoDao callInfoDao();
    public abstract RegistryDao registryDao();
    public abstract RecommendationDao recommendationDao();
    public abstract UserDao userDao();
    public abstract GeoDao geoDao();

    public static AppDatabase getInstance(Context context, char[] passphrase) {
        if (instance != null || passphrase == null) {
            return instance;
        }

        final byte[] passphraseBytes = SQLiteDatabase.getBytes(passphrase);
        final SupportFactory factory = new SupportFactory(passphraseBytes);

        instance = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME)
                .openHelperFactory(factory)
                .addCallback(new RoomPreloadCallback(context))
                .build();

        userDao = instance.userDao();
        diagnosisDao = instance.diagnosisDao();
        registryDao = instance.registryDao();
        callInfoDao = instance.callInfoDao();
        geoDao = instance.geoDao();
        return instance;
    }

    private static class RoomPreloadCallback extends Callback {

        private final AssetManager assetManager;

        public RoomPreloadCallback(Context context) {
            assetManager = context.getAssets();
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                try {

                    DiagnosisDao dao = instance.diagnosisDao();
                    dao.insertAll(getListDiagnoses());
                    RecommendationDao recommendationDao = instance.recommendationDao();
                    recommendationDao.insertAll(getListRecommendations());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        private List<Diagnosis> getListDiagnoses() throws IOException {
            List<Diagnosis> list = new ArrayList<>();
            InputStream inputStream = assetManager.open(MKB10_FILE_PATH);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] strings = line.split(";");
                Diagnosis diagnosis = new Diagnosis(strings);
                list.add(diagnosis);
            }

            scanner.close();

            return list;
        }

        private List<Medication> getListRecommendations() throws IOException {
            List<Medication> list = new ArrayList<>();
            InputStream inputStream = assetManager.open(RECOMMENDATIONS_FILE_PATH);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] strings = line.split(";");
                Medication medication = new Medication(strings);
                list.add(medication);
            }

            scanner.close();

            return list;
        }
    }
}
