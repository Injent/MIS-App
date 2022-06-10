package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.FailedDownloadDb;
import com.injent.miscalls.data.database.calls.ListEmptyException;
import com.injent.miscalls.domain.repositories.HomeRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final HomeRepository homeRepository;

    private MutableLiveData<List<CallInfo>> callList = new MutableLiveData<>();
    private MutableLiveData<Throwable> callListError = new MutableLiveData<>();

    private final MutableLiveData<String> dbDate = new MutableLiveData<>();

    public HomeViewModel() {
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

    public LiveData<List<CallInfo>> getCallListLiveData() {
        return callList;
    }

    public LiveData<Throwable> getCallListError(){
        return callListError;
    }

    public void loadCallList() {
        homeRepository.loadAllCallsInfo(throwable -> {
            callListError.postValue(throwable);
            return Collections.emptyList();
        }, list -> {
            if (!list.isEmpty()) {
                callList.postValue(list);
            } else {
                callListError.postValue(new ListEmptyException());
            }
        });
    }

    public void downloadCallsDb() {
        if (!NetworkManager.isInternetAvailable()) {
            callListError.postValue(new NetworkErrorException());
            return;
        }
        homeRepository.getPatientList(App.getUser().getToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CallInfo>> call, @NonNull Response<List<CallInfo>> response) {
                if (response.code() == 200) {
                    Log.e("A", response.body().toString());
                    if (response.body() == null) {
                        return;
                    }
                    homeRepository.insertCallsWithDropTable(throwable -> {
                        callListError.postValue(throwable);
                        return null;
                    }, response.body());
                    callList.postValue(response.body());
                    setNewDbDate();

                } else {
                    callListError.postValue(new FailedDownloadDb(AppDatabase.DB_NAME));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CallInfo>> call, @NonNull Throwable t) {
                callListError.postValue(t);
            }
        });
    }

    @Override
    protected void onCleared() {
        callList = new MutableLiveData<>();
        callListError = new MutableLiveData<>();

        homeRepository.cancelFutures();
        super.onCleared();
    }
}
