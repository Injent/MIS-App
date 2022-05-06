package com.injent.miscalls.ui.savedprotocols;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.calllist.ListEmptyException;
import com.injent.miscalls.data.savedprotocols.Inspection;
import com.injent.miscalls.domain.repositories.ProtocolRepository;

import java.util.Collections;
import java.util.List;

public class SavedProtocolsViewModel extends ViewModel {

    private final ProtocolRepository repository;
    private final MutableLiveData<List<Inspection>> protocols = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    public SavedProtocolsViewModel() {
        this.repository = new ProtocolRepository();
    }

    public LiveData<List<Inspection>> getProtocolsLiveData() {
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