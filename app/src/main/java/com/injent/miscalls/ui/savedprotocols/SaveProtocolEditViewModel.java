package com.injent.miscalls.ui.savedprotocols;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.savedprotocols.Protocol;

public class SaveProtocolEditViewModel extends ViewModel {

    private final MutableLiveData<Protocol> selectedProtocol = new MutableLiveData<>();

    public LiveData<Protocol> getProtocolLiveData() {
        return selectedProtocol;
    }
}