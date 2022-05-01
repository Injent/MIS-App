package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.PatientDatabase;
import com.injent.miscalls.domain.repositories.HomeRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final HomeRepository homeRepository;

    private final MutableLiveData<List<Patient>> patientList = new MutableLiveData<>();
    private final MutableLiveData<Throwable> patientListError = new MutableLiveData<>();

    private final MutableLiveData<String> dbDate = new MutableLiveData<>();

    public HomeViewModel() {
        super();
        homeRepository = new HomeRepository();
    }

    public void setNewDbDate() {
        dbDate.setValue(homeRepository.setNewPatientDbDate());
    }

    public String getDbDate() {
        if (dbDate.getValue() == null) {
            String date = homeRepository.getPatientDbDate();
            dbDate.setValue(date);
        }
        return dbDate.getValue();
    }

    public LiveData<String> getDbDateLiveData() {
        return dbDate;
    }

    public LiveData<List<Patient>> getPatientListLiveData() {
        return patientList;
    }

    public LiveData<Throwable> getPatientListError(){
        return patientListError;
    }

    public List<Patient> getPatientList() {
        if (patientList.getValue() == null) {
            patientList.setValue(homeRepository.getAll());
        }
        return patientList.getValue();
    }

    public void downloadPatientsDb() {
        if (!HttpManager.isInternetAvailable()) {
            patientListError.postValue(new NetworkErrorException());
            return;
        }
        homeRepository.getPatientList(App.getUser().getToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Patient>> call, @NonNull Response<List<Patient>> response) {
                if (response.isSuccessful()) {
                    List<Patient> list = response.body();
                    homeRepository.insertWithDropDb(list != null ? list.toArray(new Patient[0]) : new Patient[0]);
                    patientList.postValue(list);
                    setNewDbDate();
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

    public void backgroundDownloadDb() {
        if (!HttpManager.isInternetAvailable()) {
            return;
        }
        homeRepository.getPatientList(App.getUser().getToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Patient>> call, @NonNull Response<List<Patient>> response) {
                if (response.isSuccessful()) {
                    homeRepository.insertWithDropDb((Patient) response.body());
                    setNewDbDate();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Patient>> call, @NonNull Throwable t) {
                //Nothing to do
            }
        });
    }
}
