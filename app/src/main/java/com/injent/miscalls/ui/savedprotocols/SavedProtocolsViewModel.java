package com.injent.miscalls.ui.savedprotocols;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.patientlist.ListEmptyException;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.domain.repositories.ProtocolRepository;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SavedProtocolsViewModel extends ViewModel {

    private final ProtocolRepository repository;
    private final MutableLiveData<List<Protocol>> protocols = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    public SavedProtocolsViewModel() {
        this.repository = new ProtocolRepository();
    }

    public LiveData<List<Protocol>> getProtocolsLiveData() {
        return protocols;
    }

    public void loadProtocols() {
        repository.getProtocols(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, list -> {
            if (list.isEmpty()) {
                error.postValue(new ListEmptyException());
            } else
                protocols.postValue(list);
        });
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }
}