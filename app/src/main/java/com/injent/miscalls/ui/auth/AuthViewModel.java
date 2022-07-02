package com.injent.miscalls.ui.auth;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.domain.repositories.AuthRepository;

public class AuthViewModel extends ViewModel {

    private AuthRepository repository;

    private LiveData<User> activeUser;
    private LiveData<Throwable> errorUser;

    public void init(Context context) {
        repository = new AuthRepository(context);
        activeUser = repository.getActiveUser();
        errorUser = repository.getError();
    }

    public LiveData<User> getActiveUser() {
        return activeUser;
    }

    public void authWithBiometric() {
        repository.authWithBiometric();
    }

    public LiveData<Throwable> getErrorUser() {
        return errorUser;
    }

    public void auth(String login, String password) {
        repository.authFromDb(login, password);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        activeUser = null;
        errorUser = null;

        repository.clear();
    }
}
