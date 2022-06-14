package com.injent.miscalls.ui.auth;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.domain.repositories.AuthRepository;
import com.injent.miscalls.network.AuthorizationException;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.NetworkManager;

import java.util.function.Consumer;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private AuthRepository repository;
    private MutableLiveData<Boolean> authorized = new MutableLiveData<>();
    private MutableLiveData<Boolean> activeUser = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> authOffline = new MutableLiveData<>();

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public void findActiveUser() {
        if (App.getUser() != null) return;
        repository.findActiveSession(throwable -> {
            errorUser.postValue(throwable);
            return null;
        }, user -> {
            if (user != null) {
                user.setAuthed(true);
                App.setUser(user);
                repository.updateUser(user);
                activeUser.postValue(true);
                return;
            }
            activeUser.postValue(false);
        });
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
                    user.setAuthed(true);
                    App.setUser(user);
                    repository.insertUser(throwable -> {
                        errorUser.postValue(throwable);
                        return false;
                    }, aBoolean -> authorized.postValue(aBoolean), user);
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

    public LiveData<Boolean> getActiveUser() {
        return activeUser;
    }

    public LiveData<Boolean> getAuthOfflineLiveData() {
        return authOffline;
    }

    public LiveData<Throwable> getErrorUser() {
        return errorUser;
    }

    public void authFromDb(String login, String password) {
        repository.authFromDb(throwable -> {
            errorUser.postValue(throwable);
            return null;
        }, user -> {
            if (user != null) {
                user.setAuthed(true);
                App.setUser(user);
                authOffline.postValue(true);
                repository.updateUser(user);
                return;
            }
            authOffline.postValue(false);
        }, login, password);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        errorUser = new MutableLiveData<>();
        authorized = new MutableLiveData<>();
        activeUser = new MutableLiveData<>();
        authOffline = new MutableLiveData<>();
    }
}
