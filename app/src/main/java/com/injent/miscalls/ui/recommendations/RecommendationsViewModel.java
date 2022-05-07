package com.injent.miscalls.ui.recommendations;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.AppDatabase;
import com.injent.miscalls.domain.HttpManager;
import com.injent.miscalls.data.calllist.FailedDownloadDb;
import com.injent.miscalls.data.calllist.ListEmptyException;
import com.injent.miscalls.data.calllist.QueryToken;
import com.injent.miscalls.data.recommendation.Recommendation;
import com.injent.miscalls.domain.repositories.RecommendationsRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationsViewModel extends ViewModel {

    private final RecommendationsRepository repository;
    private final MutableLiveData<List<Recommendation>> protocolsTemps = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    public RecommendationsViewModel() {
        repository = new RecommendationsRepository();
    }

    public LiveData<List<Recommendation>> getProtocolTempsLiveDate() {
        return protocolsTemps;
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    public void loadProtocolTemps() {
        repository.getAll(throwable -> {
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
            public void onResponse(@NonNull Call<List<Recommendation>> call, @NonNull Response<List<Recommendation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    repository.insertWithDropTable(response.body());
                    protocolsTemps.postValue(response.body());
                } else
                    error.postValue(new UnknownError());
            }

            @Override
            public void onFailure(@NonNull Call<List<Recommendation>> call, @NonNull Throwable t) {
                error.postValue(new FailedDownloadDb(AppDatabase.DB_NAME));
            }
        });
    }

}