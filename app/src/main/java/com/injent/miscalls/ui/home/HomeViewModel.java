package com.injent.miscalls.ui.home;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.domain.repositories.AuthRepository;
import com.injent.miscalls.network.rest.dto.CallDto;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.FailedDownloadDb;
import com.injent.miscalls.data.database.calls.ListEmptyException;
import com.injent.miscalls.domain.repositories.CallRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final CallRepository homeRepository;
    private final AuthRepository authRepository;

    private MutableLiveData<List<CallInfo>> callList = new MutableLiveData<>();
    private MutableLiveData<Throwable> callListError = new MutableLiveData<>();

    private final MutableLiveData<String> dbDate = new MutableLiveData<>();

    public HomeViewModel() {
        homeRepository = new CallRepository();
        authRepository = new AuthRepository();
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

    public void loadCallList(int userId) {
        homeRepository.loadAllCallsInfo(throwable -> {
            callListError.postValue(throwable);
            throwable.printStackTrace();
            return Collections.emptyList();
        }, list -> {
            if (!list.isEmpty()) {
                callList.postValue(list);
            } else {
                callListError.postValue(new ListEmptyException());
            }
        }, userId);
    }

    public void logout() {
        App.getEncryptedUserDataManager().setData(R.string.keyAuthed, false).write();
        App.getUser().setAuthed(false);
        authRepository.updateUser(App.getUser());
    }

    public void downloadCallsDb() {
        if (!NetworkManager.isInternetAvailable()) {
            callListError.postValue(new NetworkErrorException());
            return;
        }
        homeRepository.getPatientList(App.getUser().getToken()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.isSuccessful()) {
                    List<CallInfo> list = response.body().getCalls().stream().map(CallDto::toDomainObject).collect(Collectors.toList());
                    if (response.body() == null) {
                        return;
                    }
                    homeRepository.insertCallsWithDropTable(throwable -> {
                        callListError.postValue(throwable);
                        throwable.printStackTrace();
                        return null;
                    }, list);
                    callList.postValue(list);
                    setNewDbDate();

                } else {
                    callListError.postValue(new FailedDownloadDb(AppDatabase.DB_NAME));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JResponse> call, @NonNull Throwable t) {
                callListError.postValue(t);
            }
        });
    }

    @Override
    protected void onCleared() {
        callList = new MutableLiveData<>();
        callListError = new MutableLiveData<>();

        authRepository.cancelFutures();
        homeRepository.cancelFutures();
        super.onCleared();
    }
}
