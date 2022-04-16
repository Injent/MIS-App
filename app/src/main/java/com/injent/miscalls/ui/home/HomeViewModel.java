package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.PatientDatabase;
import com.injent.miscalls.data.patientlist.QueryPatients;
import com.injent.miscalls.domain.HomeRepository;

import java.text.SimpleDateFormat;
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

    public MaterialDividerItemDecoration getDivider(Context context) {
        MaterialDividerItemDecoration divider
                = new MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL);
        divider.setDividerInsetStartResource(context,R.dimen.dividerInset);
        divider.setDividerInsetEndResource(context, R.dimen.dividerInset);
        divider.setDividerColorResource(context, R.color.line);
        return divider;
    }

    public void setNewDbDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dbDate.setValue(currentDate);

        SharedPreferences sp = HomeFragment.getInstance().requireActivity()
                .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("dbDate",dbDate.getValue());
        editor.apply();
    }

    public String getDbDate() {
        if (dbDate.getValue() == null) {
            SharedPreferences sp = HomeFragment.getInstance().requireActivity()
                    .getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE);
            dbDate.setValue(sp.getString("dbDate", App.getInstance()
                    .getString(R.string.listNotUpdated)));
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
        QueryPatients queryPatients = new QueryPatients();
        queryPatients.setToken(App.getInstance().getUser().getToken());
        homeRepository.getPatientList(queryPatients).enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<List<Patient>> call, @NonNull Response<List<Patient>> response) {
                if (response.isSuccessful()) {
                    List<Patient> list = response.body();
                    insertWithDropDb(list.toArray(new Patient[0]));
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

    public void downloadProtocolsDb() {
        if (!HttpManager.isInternetAvailable()) {

        }
    }

    public void moveToSite(Context context, CharSequence link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) link));
        context.startActivity(intent);
    }

    public void dropDb() {
        homeRepository.deleteAll();
    }

    public void insertPatient(Patient... patients) {
        homeRepository.insertPatient(patients);
    }

    public void updatePatient(Patient... patients) {
        homeRepository.updatePatient(patients);
    }

    public void insertWithDropDb(Patient... patients) {
        homeRepository.insertWithDropDb(patients);
    }
}
