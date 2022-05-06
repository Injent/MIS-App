package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.domain.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.calllist.CallDatabase;
import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.calllist.FailedDownloadDb;
import com.injent.miscalls.data.calllist.ListEmptyException;
import com.injent.miscalls.domain.repositories.HomeRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final HomeRepository homeRepository;

    private final MutableLiveData<List<CallInfo>> callList = new MutableLiveData<>();
    private final MutableLiveData<Throwable> callListError = new MutableLiveData<>();

    private final MutableLiveData<String> dbDate = new MutableLiveData<>();

    public HomeViewModel() {
        super();
        homeRepository = new HomeRepository();
    }

    public void setNewDbDate() {
        dbDate.setValue(homeRepository.setNewPatientDbDate());
    }

    public void loadDbDate() {
        dbDate.setValue(homeRepository.getPatientDbDate());
    }

    public LiveData<String> getDbDateLiveData() {
        return dbDate;
    }

    public LiveData<List<CallInfo>> getPatientListLiveData() {
        return callList;
    }

    public LiveData<Throwable> getCallListError(){
        return callListError;
    }

    public void loadCallList() {
        homeRepository.getAll(throwable -> {
            callListError.postValue(throwable);
            return Collections.emptyList();
        }, list -> {
            if (list == null || list.isEmpty()) {
                callListError.postValue(new ListEmptyException());
            } else {
                callList.postValue(list);
            }
        });
    }

    public void downloadPatientsDb() {
        if (!HttpManager.isInternetAvailable()) {
            callListError.postValue(new NetworkErrorException());
            return;
        }
        homeRepository.getPatientList(App.getUser().getQueryToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CallInfo>> call, @NonNull Response<List<CallInfo>> response) {
                if (response.isSuccessful()) {
                    List<CallInfo> list = response.body();
                    homeRepository.insertWithDropDb(list != null ? list.toArray(new CallInfo[0]) : new CallInfo[0]);
                    callList.postValue(list);
                    setNewDbDate();
                } else {
                    callListError.postValue(new FailedDownloadDb(CallDatabase.DB_NAME));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CallInfo>> call, @NonNull Throwable t) {
                callListError.postValue(t);
            }
        });
    }

    public void backgroundDownloadDb() {
        if (!HttpManager.isInternetAvailable()) {
            return;
        }
        homeRepository.getPatientList(App.getUser().getQueryToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CallInfo>> call, @NonNull Response<List<CallInfo>> response) {
                if (response.isSuccessful()) {
                    homeRepository.insertWithDropDb((CallInfo) response.body());
                    setNewDbDate();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CallInfo>> call, @NonNull Throwable t) {
                //Nothing to do
            }
        });
    }
}
