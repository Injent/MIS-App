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
    private final MutableLiveData<String> currentInspection = new MutableLiveData<>();
    private final MutableLiveData<String> currentRecommendation = new MutableLiveData<>();
    private final MutableLiveData<List<Diagnosis>> currentDiagnoses = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private final MutableLiveData<List<Diagnosis>> diagnosesDatabaseList = new MutableLiveData<>();

    public CallStuffViewModel() {
        registryRepository = new RegistryRepository();
        homeRepository = new HomeRepository();
        diagnosisRepository = new DiagnosisRepository();
    }

    public LiveData<List<Diagnosis>> getCurrentDiagnosesLiveData() {
        return currentDiagnoses;
    }

    public void setCurrentDiagnoses(List<Diagnosis> list) {
        currentDiagnoses.setValue(list);
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
            if (callInfo != null) {
                selectedCall.postValue(callInfo);
            } else {
                callError.postValue(new UnknownError());
            }
        }, callId);
    }

    public void saveRegistry() {
        if (selectedCall.getValue() == null || currentDiagnoses.getValue() == null) {
            error.postValue(new IllegalStateException());
            return;
        }
        Registry registry = new Registry();
        registry.setRecommendations(currentRecommendation.getValue());
        registry.setInspection(currentInspection.getValue());
        registry.setDiagnosisId(selectedCall.getValue().getId());
        registry.setName(selectedCall.getValue().getFullName());
        registry.setDiagnosisCode(Diagnosis.listToStringCodes(currentDiagnoses.getValue()));
        registryRepository.insertRegistry(registry);
    }

    public LiveData<String> getCurrentRecommendationLiveData() {
        return currentRecommendation;
    }

    public void setCurrentRecommendation(String s) {
        currentRecommendation.setValue(s);
    }

    public LiveData<String> getCurrentInspectionLiveData() {
        return currentInspection;
    }

    public void setCurrentInspection(String s) {
        currentInspection.setValue(s);
    }

    public void loadDiagnosisDatabase() {
        diagnosisRepository.getAllDiagnosis(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, diagnoses -> {
            if (!diagnoses.isEmpty()) {
                diagnosesDatabaseList.postValue(diagnoses);
            } else {
                error.postValue(new ListEmptyException());
            }
        });
    }

    public LiveData<List<Diagnosis>> getDiagnosisDatabaseListLiveData() {
        return diagnosesDatabaseList;
    }
}
