package com.injent.miscalls.ui;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.domain.repositories.AuthRepository;

public class MainViewModel extends ViewModel {

    private AuthRepository repository;

    private LiveData<User> activeUser = new MutableLiveData<>();

    public void init(Context context) {
        repository = new AuthRepository(context);
        activeUser = repository.getActiveSession();
    }

    public LiveData<User> getActiveSession() {
        return activeUser;
    }

    public void findActiveUser() {
        if (App.getUser() != null) return;
        repository.findActiveSession();
    }

    @Override
    public void onCleared() {
        repository.clear();
        activeUser = null;

        super.onCleared();
    }
}
