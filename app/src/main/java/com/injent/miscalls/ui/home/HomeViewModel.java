package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.PatientDatabase;
import com.injent.miscalls.domain.HomeRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final HomeRepository homeRepository;

    private MutableLiveData<List<Patient>> patientList = new MutableLiveData<>();
    private MutableLiveData<Throwable> patientListError = new MutableLiveData<>();
    private MutableLiveData<String> dbDate = new MutableLiveData<>();

    public HomeViewModel() {
        super();
        homeRepository = new HomeRepository();
    }

    public void setDbDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dbDate.setValue(currentDate);
        HomeFragment.getInstance().setDbDate();
    }

    public String getDbDate() {
        if (dbDate.getValue() == null || dbDate.getValue().isEmpty()) {
            dbDate.setValue(HomeFragment.getInstance().getDbDate());
        }
        return dbDate.getValue();
    }

    public LiveData<List<Patient>> getPatientListLiveData() {
        return patientList;
    }

    public LiveData<Throwable> getPatientListError(){
        return patientListError;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList.setValue(patientList);
    }

    public List<Patient> getPatientList() {
        if (patientList.getValue() == null) {
            patientList.setValue(App.getInstance().getPatientDao().getAll());
        }
        return patientList.getValue();
    }

    public void downloadDb() {
        if (!HttpManager.isInternetAvailable()) {
            patientListError.postValue(new NetworkErrorException());
            return;
        }
        homeRepository.getPatientList().enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<List<Patient>> call, @NonNull Response<List<Patient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Patient> list = response.body();
                    insertPatient(list.toArray(new Patient[0]));
                    setDbDate();
                } else {
                    patientListError.postValue(new FailedDownloadDb(PatientDatabase.DB_NAME));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Patient>> call, @NonNull Throwable t) {
                patientListError.postValue(t);
            }
        });
    }

    public void dropDb() {
        homeRepository.deleteAll();
    }

    public void deletePatient(Patient... patients) {
        homeRepository.deletePatient(patients);
    }

    public void insertPatient(Patient... patients) {
        homeRepository.insertPatient(patients);
    }
}
