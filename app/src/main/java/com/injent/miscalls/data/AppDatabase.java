package com.injent.miscalls.data;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.injent.miscalls.App;
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
import java.util.Scanner;

@Database(entities = {Diagnosis.class, Registry.class, CallInfo.class, Recommendation.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {


    public static final String DB_NAME = "database.db";
    private static final String MKB = "mkb10.csv";

    public static AppDatabase INSTANCE;

    public abstract DiagnosisDao diagnosisDao();

    public abstract CallInfoDao callInfoDao();

    public abstract RegistryDao registryDao();

    public abstract RecommendationDao recommendationDao();

    public static final Callback roomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE != null) return INSTANCE;

        final char[] userEnteredPassphrase = new char[]{'a', 'b'};
        final byte[] passphrase = SQLiteDatabase.getBytes(userEnteredPassphrase);
        final SupportFactory factory = new SupportFactory(passphrase);

        INSTANCE = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME)
                .addCallback(new CallbackQQQ(context))
                .openHelperFactory(factory)
                .build();
        return INSTANCE;
    }

    private static class CallbackQQQ extends Callback {

        private final Context context;

        public CallbackQQQ(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(MKB);
                Scanner scanner = new Scanner(inputStream);

                ArrayList<Diagnosis> arrayList = new ArrayList<>();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] strings = line.split(",");
                    Diagnosis diagnosis = new Diagnosis(strings[])
                    arrayList.add(diagnosis);
                }
                scanner.close();

                DiagnosisDao dao = INSTANCE.diagnosisDao();
                dao.insert(new Diagnosis());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
