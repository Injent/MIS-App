package com.injent.miscalls.ui.test;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.domain.repositories.CallRepository;

public class TestViewModel extends ViewModel {

    private CallRepository repository;
    private LiveData<Throwable> error;

    public void init() {
        repository = new CallRepository();
        error = repository.getErrorLiveData();
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void addPatient(Context context, String name, int userId) {
        repository.addPatient(context, name, userId);
    }

    public void deletePatient(Context context, int id) {
        repository.deletePatient(context, id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        error = null;
    }
}
