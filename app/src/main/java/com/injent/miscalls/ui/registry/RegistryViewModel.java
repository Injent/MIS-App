package com.injent.miscalls.ui.registry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.calllist.ListEmptyException;
import com.injent.miscalls.data.registry.Registry;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.util.Collections;
import java.util.List;

public class RegistryViewModel extends ViewModel {

    private final RegistryRepository repository;
    private final MutableLiveData<List<Registry>> registryItems = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    public RegistryViewModel() {
        this.repository = new RegistryRepository();
    }

    public LiveData<List<Registry>> getRegistryItemsLiveData() {
        return registryItems;
    }

    public void loadRegistryItems() {
        repository.getRegistry(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, list -> {
            if (list.isEmpty()) {
                error.postValue(new ListEmptyException());
            } else
                registryItems.postValue(list);
        });
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }
}