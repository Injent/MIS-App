package com.injent.miscalls.ui.savedprotocols;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.domain.repositories.ProtocolRepository;

import java.util.List;

public class SavedProtocolsViewModel extends ViewModel {

    private final ProtocolRepository repository;
    private final MutableLiveData<List<Protocol>> protocols = new MutableLiveData<>();
    private final MutableLiveData<Protocol> editingProtocol = new MutableLiveData<>();

    public SavedProtocolsViewModel() {
        this.repository = new ProtocolRepository();
    }

    public void loadProtocols() {
        if (protocols.getValue() == null)
            protocols.setValue(repository.getProtocols());
    }

    public LiveData<Protocol> getEditingProtocolLiveData() {
        return editingProtocol;
    }

    public LiveData<List<Protocol>> getProtocolsLiveData() {
        return protocols;
    }
}