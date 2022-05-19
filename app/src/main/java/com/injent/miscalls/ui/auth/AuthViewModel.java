package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.network.UserNotFoundException;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.User;
import com.injent.miscalls.domain.repositories.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<Boolean> authorized = new MutableLiveData<>();
    private final MutableLiveData<Throwable> errorUser = new MutableLiveData<>();

    public AuthViewModel() {
        super();
        repository = new AuthRepository();
    }

    public void auth(String login, String password) {
        if (!NetworkManager.isInternetAvailable()) {
            errorUser.postValue(new NetworkErrorException());
            return;
        }
        Call<User> call = repository.auth(login, password);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                switch (response.code()) {
                    case 200: {
                        User authModelIn = response.body();
                        if (authModelIn == null) return;
                        authModelIn.setLogin(login);
                        authModelIn.setPassword(password);
                        App.getInstance().writeEncryptedData(authModelIn);
                        authorized.postValue(true);
                    } break;
                    case 403: {
                        errorUser.postValue(new UserNotFoundException());
                    }
                    break;
                    default: errorUser.postValue(new UnknownError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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
}
