package com.injent.miscalls.ui.registry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.util.Collections;
import java.util.List;

public class RegistryViewModel extends ViewModel {

    private final RegistryRepository repository;
    private MutableLiveData<List<Registry>> registryItems = new MutableLiveData<>();
    private MutableLiveData<Throwable> error = new MutableLiveData<>();

    public RegistryViewModel() {
        this.repository = new RegistryRepository();
    }

    public LiveData<List<Registry>> getRegistryItemsLiveData() {
        return registryItems;
    }

    public void loadRegistryItems() {
        repository.getRegistries(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, list -> {
            registryItems.postValue(list);
        });
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    @Override
    protected void onCleared() {
        registryItems = new MutableLiveData<>();
        error = new MutableLiveData<>();

        repository.cancelFutures();
        super.onCleared();
    }
}