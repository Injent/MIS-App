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
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.domain.HomeRepository;

import java.util.List;

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

    public void setList(List<Patient> list) {
        patientList.setValue(list);
    }

    public LiveData<Throwable> getPatientListError(){
        return patientListError;
    }

    public List<Patient> getPatientList() {
        if (patientList.getValue() == null) {
            patientList.setValue(App.getInstance().getPatientDao().getAll());
        }
        return patientList.getValue();
    }

    public void downloadPatientsDb() {
        if (!HttpManager.isInternetAvailable()) {
            patientListError.postValue(new NetworkErrorException());
            return;
        }
        homeRepository.getPatientList(App.getInstance().getUser().getToken()).enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<List<Patient>> call, @NonNull Response<List<Patient>> response) {
                if (response.isSuccessful()) {
                    List<Patient> list = response.body();
                    homeRepository.insertWithDropDb(list != null ? list.toArray(new Patient[0]) : new Patient[0]);
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
}
