package com.injent.miscalls.ui.protocoltempedit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.domain.repositories.ProtocolTempRepository;

public class ProtocolTempEditViewModel extends ViewModel {

    private final ProtocolTempRepository repository;
    private final MutableLiveData<ProtocolTemp> selectedProtocol = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();

    public ProtocolTempEditViewModel() {
        this.repository = new ProtocolTempRepository();
    }

    public LiveData<ProtocolTemp> getProtocolTempLiveData() {
        return selectedProtocol;
    }

    public void loadProtocolTemp(int protocolId) {
        repository.getProtocolTempById(throwable -> {
            error.postValue(throwable);
            return null;
        }, protocolTemp -> {
            if (protocolTemp == null)
                error.postValue(new NullPointerException());
            else
                selectedProtocol.postValue(protocolTemp);
        },protocolId);
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }

    public void saveProtocolTemp(ProtocolTemp protocolTemp) {
        repository.insertProtocol(protocolTemp);
    }
}
