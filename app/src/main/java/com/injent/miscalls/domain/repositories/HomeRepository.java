package com.injent.miscalls.domain.repositories;

import android.content.SharedPreferences;

import com.injent.miscalls.api.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.data.patientlist.QueryToken;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;

public class HomeRepository {

    private PatientDao dao;
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
        editor.putString("dbDate",currentDate);
        editor.apply();
        return currentDate;
    }

    public String getPatientDbDate() {
        return App.getInstance().getSharedPreferences().getString("dbDate", "");
    }

    public Call<List<Patient>> getPatientList(QueryToken token) {
        return HttpManager.getMisAPI().patients(token);
    }

    public void deleteAll() {
        es.submit(() -> App.getInstance().getPdb().clearAllTables());
    }

    public Patient getPatientById(int id) {
        Future<Patient> patientFuture = es.submit(() -> dao.getPatientById(id));
        try {
            return patientFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public List<Patient> getAll() {
        Future<List<Patient>> listFuture = es.submit(() -> dao.getAll());
        try {
            return listFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return Collections.emptyList();
    }

    public Boolean insertWithDropDb(Patient...patients) {
        Future<Boolean> booleanFuture = es.submit(() -> {
            App.getInstance().getPdb().clearAllTables();
            for (Patient patient : patients) {
                dao.insert(patient);
            }
            return true;
        });
        try {
            return booleanFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
