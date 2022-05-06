package com.injent.miscalls.ui.patientstuff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.domain.repositories.HomeRepository;

public class CallStuffViewModel extends ViewModel {

    private final HomeRepository repository;

    private final MutableLiveData<CallInfo> selectedCall = new MutableLiveData<>();
    private final MutableLiveData<Throwable> callError = new MutableLiveData<>();

    public CallStuffViewModel() {
        repository = new HomeRepository();
    }

    public LiveData<CallInfo> getCallLiveData() {
        return selectedCall;
    }

    public LiveData<Throwable> getCallErrorLiveData() {
        return callError;
    }

    public void loadCall(int callId) {
        repository.getCallById(throwable -> {
            callError.postValue(throwable);
            return null;
        }, callInfo -> {
            if (callInfo == null) {
                callError.postValue(new UnknownError());
            } else
                selectedCall.postValue(callInfo);
        }, callId);
    }
}
