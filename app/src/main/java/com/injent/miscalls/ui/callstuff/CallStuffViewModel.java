package com.injent.miscalls.ui.callstuff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.registry.Registry;
import com.injent.miscalls.domain.repositories.HomeRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;

public class CallStuffViewModel extends ViewModel {

    private final RegistryRepository registryRepository;
    private final HomeRepository homeRepository;

    private final MutableLiveData<CallInfo> selectedCall = new MutableLiveData<>();
    private final MutableLiveData<Throwable> callError = new MutableLiveData<>();

    public CallStuffViewModel() {
        registryRepository = new RegistryRepository();
        homeRepository = new HomeRepository();
    }

    public LiveData<CallInfo> getCallLiveData() {
        return selectedCall;
    }

    public LiveData<Throwable> getCallErrorLiveData() {
        return callError;
    }

    public void loadCall(int callId) {
        homeRepository.getCallById(throwable -> {
            callError.postValue(throwable);
            return null;
        }, callInfo -> {
            if (callInfo == null) {
                callError.postValue(new UnknownError());
            } else
                selectedCall.postValue(callInfo);
        }, callId);
    }

    public void saveRegistry(Registry registry) {
        registryRepository.insertRegistry(registry);
    }
}
