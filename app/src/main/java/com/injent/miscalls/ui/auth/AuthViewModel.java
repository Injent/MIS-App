package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.data.User;
import com.injent.miscalls.domain.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;
    private MutableLiveData<User> authorized = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorUser = new MutableLiveData<>();

    public AuthViewModel() {
        super();
        repository = new AuthRepository();
    }

    public void auth(String login, String password) {
        if (!HttpManager.isInternetAvailable()) {
            Log.e("AuthViewModel", "NO INTERNET CONNECTION");
            errorUser.postValue(new NetworkErrorException());
            return;
        }
        Call<User> call = repository.auth(login, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                switch (response.code()) {
                    case 200: {
                        User authModelIn = response.body();
                        authorized.postValue(authModelIn);
                    } break;
                    case 403: {
                        errorUser.postValue(new UserNotFoundException());
                    } break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                errorUser.postValue(t);
            }
        });
    }

    public boolean isAuthed() {
        return repository.isAuthed();
    }

    public void setAuthed(boolean authed) {
        repository.setAuthed(authed);
    }

    public LiveData<User> getAuthorized() {
        return authorized;
    }

    public LiveData<Throwable> getErrorUser() {
        return errorUser;
    }
}
