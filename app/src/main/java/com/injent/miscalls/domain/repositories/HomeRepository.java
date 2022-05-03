package com.injent.miscalls.domain.repositories;

import android.content.SharedPreferences;
import android.util.Log;

import com.injent.miscalls.data.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.Keys;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.data.patientlist.QueryToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import retrofit2.Call;

public class HomeRepository {

    private final PatientDao dao;
    private final ExecutorService es;

    public HomeRepository() {
        dao = App.getInstance().getPatientDao();
        es = Executors.newSingleThreadExecutor();
    }

    public String setNewPatientDbDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        SharedPreferences sp = App.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Keys.DB_DATE,currentDate);
        editor.apply();
        return currentDate;
    }

    public String getPatientDbDate() {
        return App.getInstance().getSharedPreferences().getString(Keys.DB_DATE, "");
    }

    public Call<List<Patient>> getPatientList(QueryToken token) {
        return HttpManager.getMisAPI().patients(token);
    }

    public void deleteAll() {
        es.submit(() -> App.getInstance().getPatientDatabase().clearAllTables());
    }

    public void getPatientById(Function<Throwable, Patient> ex, Consumer<Patient> consumer, int id) {
        CompletableFuture<Patient> future = CompletableFuture
                .supplyAsync(() -> dao.getPatientById(id), es)
                .exceptionally(ex);

        future.thenAcceptAsync(consumer);
    }

    public void insertPatient(Patient patient) {
        es.submit(() -> dao.insert(patient));
    }

    public void getAll(Function<Throwable, List<Patient>> ex, Consumer<List<Patient>> consumer) {
        CompletableFuture<List<Patient>> future = CompletableFuture
                .supplyAsync(dao::getAll, es)
                .exceptionally(ex);

        future.thenAcceptAsync(consumer);
    }

    public void insertWithDropDb(Patient... patients) {
        es.submit(() -> {
            App.getInstance().getPatientDatabase().clearAllTables();
            for (Patient patient : patients) {
                dao.insert(patient);
            }
        });
    }
}
