package com.injent.miscalls.ui.protocoltemp;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.HttpManager;
import com.injent.miscalls.data.patientlist.FailedDownloadDb;
import com.injent.miscalls.data.patientlist.ListEmptyException;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.data.templates.ProtocolTempDatabase;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.domain.ProtocolFletcher;
import com.injent.miscalls.domain.repositories.ProtocolTempRepository;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProtocolTempViewModel extends ViewModel {

    private final ProtocolTempRepository repository;
    private final MutableLiveData<List<ProtocolTemp>> protocolsTemps = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    public ProtocolTempViewModel() {
        repository = new ProtocolTempRepository();
    }

    public LiveData<List<ProtocolTemp>> getProtocolTempsLiveDate() {
        return protocolsTemps;
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    public void loadProtocolTemps() {
        repository.getProtocolTemps(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, list -> {
            if (list.isEmpty())
                error.postValue(new ListEmptyException());
            else
                protocolsTemps.postValue(list);
        });
    }

    public void downloadProtocolTemps(QueryToken token) {
        if (!HttpManager.isInternetAvailable()) {
            error.setValue(new NetworkErrorException());
            return;
        }
        repository.getProtocolsFromServer(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ProtocolTemp>> call, @NonNull Response<List<ProtocolTemp>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    repository.insertProtocolTempWithDropDb(response.body());
                    protocolsTemps.postValue(response.body());
                } else
                    error.postValue(new UnknownError());
            }

            @Override
            public void onFailure(@NonNull Call<List<ProtocolTemp>> call, @NonNull Throwable t) {
                error.postValue(new FailedDownloadDb(ProtocolTempDatabase.DB_NAME));
            }
        });
    }

}