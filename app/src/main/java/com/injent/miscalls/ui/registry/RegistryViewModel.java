package com.injent.miscalls.ui.registry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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

    public void loadRegistryItems(int idToDelete) {
        repository.getRegistries(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return Collections.emptyList();
        }, list -> {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setDelete(list.get(i).getId() == idToDelete);
            }
            registryItems.postValue(list);
        });
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    public void deleteSelectedRegistry(int id) {
        repository.deleteRegistry(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        }, unused -> loadRegistryItems(-1), id);
    }

    public void sendRegistries() {
        repository.dropTable(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        }, unused -> loadRegistryItems(-1));

    }

    @Override
    protected void onCleared() {
        registryItems = new MutableLiveData<>();
        error = new MutableLiveData<>();

        repository.cancelFutures();
        super.onCleared();
    }
}