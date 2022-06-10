package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.R;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.AuthorizationException;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.domain.repositories.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private AuthRepository repository;
    private MutableLiveData<Boolean> authorized = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorUser = new MutableLiveData<>();

    public AuthViewModel() {
        super();
        repository = new AuthRepository();
    }

    public void auth(String login, String password) {
        if (repository == null) {
            repository = new AuthRepository();
        }
        if (!NetworkManager.isInternetAvailable()) {
            errorUser.postValue(new NetworkErrorException());
            return;
        }
        Call<JResponse> call = repository.auth(login, password);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.body() == null) return;

                JResponse resp = response.body();
                if (response.isSuccessful() && resp.getUser() != null) {
                    User user = response.body().getUser();
                    user.setLogin(login);
                    user.setPassword(password);
                    user.setOrganizationId(user.getOrganizationId());
                    user.setTokenId(user.getTokenId());
                    user.setAuthed(true);
                    App.getInstance().regUser(user);
                    repository.insertUser(user);
                    authorized.postValue(true);
                }
                if (!resp.isSuccessful())
                    errorUser.postValue(new AuthorizationException(resp.getMessage()));
            }

            @Override
            public void onFailure(@NonNull Call<JResponse> call, @NonNull Throwable t) {
                errorUser.postValue(t);
            }
        });
    }

    public LiveData<Boolean> getAuthorized() {
        return authorized;
    }

    public LiveData<Throwable> getErrorUser() {
        return errorUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        errorUser = new MutableLiveData<>();
        authorized = new MutableLiveData<>();

        repository = null;
    }
}
