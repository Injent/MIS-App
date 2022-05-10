package com.injent.miscalls.data;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.calllist.CallInfoDao;
import com.injent.miscalls.data.diagnosis.Diagnosis;
import com.injent.miscalls.data.diagnosis.DiagnosisDao;
import com.injent.miscalls.data.recommendation.Recommendation;
import com.injent.miscalls.data.recommendation.RecommendationDao;
import com.injent.miscalls.data.registry.Registry;
import com.injent.miscalls.data.registry.RegistryDao;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;

@Database(entities = {Diagnosis.class, Registry.class, CallInfo.class, Recommendation.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {


    public static final String DB_NAME = "database.db";
    private static final String MKB10_FILE_PATH = "databases/mkb10.csv";

    private static AppDatabase instance;

    public abstract DiagnosisDao diagnosisDao();

    public abstract CallInfoDao callInfoDao();

    public abstract RegistryDao registryDao();

    public abstract RecommendationDao recommendationDao();

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
        return instance;
    }

    private static class RoomPreloadCallback extends Callback {

        private final Context context;

        public RoomPreloadCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                //Prepopulate database with mkb10 codes
                try {
                    AssetManager assetManager = context.getAssets();
                    InputStream inputStream = assetManager.open(MKB10_FILE_PATH);
                    Scanner scanner = new Scanner(inputStream);

                    List<Diagnosis> list = new ArrayList<>();

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] strings = line.split(";");
                        Diagnosis diagnosis = new Diagnosis(strings);
                        list.add(diagnosis);

                    }
                    scanner.close();

                    DiagnosisDao dao = instance.diagnosisDao();
                    dao.insertAll(list);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
