package com.injent.miscalls.ui.callstuff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.calllist.ListEmptyException;
import com.injent.miscalls.data.diagnosis.Diagnosis;
import com.injent.miscalls.data.registry.Registry;
import com.injent.miscalls.domain.repositories.DiagnosisRepository;
import com.injent.miscalls.domain.repositories.HomeRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.util.Collections;
import java.util.List;

public class CallStuffViewModel extends ViewModel {

    private final RegistryRepository registryRepository;
    private final HomeRepository homeRepository;
    private final DiagnosisRepository diagnosisRepository;

    private final MutableLiveData<CallInfo> selectedCall = new MutableLiveData<>();
    private final MutableLiveData<Throwable> callError = new MutableLiveData<>();
    private final MutableLiveData<String> currentDiagnoses = new MutableLiveData<>();
    private final MutableLiveData<String> currentInspection = new MutableLiveData<>();
    private final MutableLiveData<String> currentRecommendation = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private final MutableLiveData<List<Diagnosis>> diagnosesList = new MutableLiveData<>();

    public CallStuffViewModel() {
        registryRepository = new RegistryRepository();
        homeRepository = new HomeRepository();
        diagnosisRepository = new DiagnosisRepository();
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

    public String getCurrentRecommendation() {
        return currentRecommendation.getValue();
    }

    public void setCurrentRecommendation(String s) {
        currentRecommendation.setValue(s);
    }

    public String getCurrentInspection() {
        return currentInspection.getValue();
    }

    public void setCurrentInspection(String s) {
        currentInspection.setValue(s);
    }

    public void setCurrentDiagnoses(String s) {
        currentDiagnoses.setValue(s);
    }

    public String getDiagnosesList() {
        return currentDiagnoses.getValue();
    }

    public void loadDiagnosis() {
        diagnosisRepository.getAllDiagnosis(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, diagnoses -> {
            if (diagnoses == null || diagnoses.isEmpty())
                error.postValue(new ListEmptyException());
            else
                CallStuffViewModel.this.diagnosesList.postValue(diagnoses);
        });
    }

    public LiveData<List<Diagnosis>> getDiagnosisLiveData() {
        return diagnosesList;
    }
}
