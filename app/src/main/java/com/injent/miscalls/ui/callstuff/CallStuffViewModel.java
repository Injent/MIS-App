package com.injent.miscalls.ui.callstuff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.domain.repositories.DiagnosisRepository;
import com.injent.miscalls.domain.repositories.HomeRepository;
import com.injent.miscalls.domain.repositories.RecommendationRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class CallStuffViewModel extends ViewModel {

    private final RegistryRepository registryRepository;
    private final HomeRepository homeRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final RecommendationRepository recommendationRepository;

    private MutableLiveData<CallInfo> selectedCall = new MutableLiveData<>();
    private MutableLiveData<Throwable> callError = new MutableLiveData<>();
    private MutableLiveData<String> currentInspection = new MutableLiveData<>();
    private MutableLiveData<String> currentRecommendation = new MutableLiveData<>();
    private MutableLiveData<List<Diagnosis>> currentDiagnoses = new MutableLiveData<>();
    private MutableLiveData<Throwable> error = new MutableLiveData<>();
    private MutableLiveData<List<Diagnosis>> diagnosesDatabaseList = new MutableLiveData<>();

    public CallStuffViewModel() {
        registryRepository = new RegistryRepository();
        homeRepository = new HomeRepository();
        diagnosisRepository = new DiagnosisRepository();
        recommendationRepository = new RecommendationRepository();
    }

    public LiveData<List<Diagnosis>> getCurrentDiagnosesLiveData() {
        return currentDiagnoses;
    }
    public void load() {
        ArrayList<Diagnosis> list = new ArrayList<>();
        Diagnosis d = new Diagnosis();
        d.setId(1);
        d.setName("A");
        d.setCallInfoId(1);
        d.setCode("AE@");
        list.add(d);
        currentInspection.setValue("ALEG");
        currentDiagnoses.setValue(list);
        currentRecommendation.setValue("REC");
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
        List<Diagnosis> list = currentDiagnoses.getValue();
        if (selectedCall.getValue() == null || list == null) {
            error.postValue(new IllegalStateException());
            return;
        }
        Registry registry = new Registry();
        registry.setId(Objects.requireNonNull(selectedCall.getValue()).getId());
        registry.setRecommendation(currentRecommendation.getValue());
        registry.setInspection(currentInspection.getValue());

        StringBuilder sb = new StringBuilder();
        for (Diagnosis d : list) {
            sb.append(d.getId());
            if (!d.equals(list.get(list.size() - 1))) {
                sb.append(";");
            }
        }
        registry.setDiagnosesId(sb.toString());
        registryRepository.insertRegistry(throwable -> {
            error.postValue(throwable);
            return null;
        }, registry);
    }

    public LiveData<String> getCurrentRecommendationLiveData() {
        return currentRecommendation;
    }

    public void setCurrentRecommendation(int id) {
        recommendationRepository.loadRecommendationById(throwable -> {
            error.postValue(throwable);
            return null;
        }, recommendation -> currentRecommendation.postValue(recommendation.getContent()), id);
    }

    public LiveData<String> getCurrentInspectionLiveData() {
        return currentInspection;
    }

    public void setCurrentInspection(String s) {
        currentInspection.setValue(s);
    }

    public void loadDiagnosisDatabase() {

    }

    public LiveData<List<Diagnosis>> getDiagnosisDatabaseListLiveData() {
        return diagnosesDatabaseList;
    }

    public String getAutoFilledField(CallInfo callInfo, String[] presets) {
        if (callInfo != null && presets != null && presets.length > 0) {
            return diagnosisRepository.getAutoFilledField(callInfo, presets);
        }
        return "";
    }

    public boolean isInspectionDone() {
        return currentDiagnoses.getValue() != null && !currentDiagnoses.getValue().isEmpty() && currentInspection.getValue() != null && !currentInspection.getValue().isEmpty() && currentRecommendation.getValue() != null;
    }

    @Override
    protected void onCleared() {
        selectedCall = new MutableLiveData<>();
        callError = new MutableLiveData<>();
        currentInspection = new MutableLiveData<>();
        currentRecommendation = new MutableLiveData<>();
        currentDiagnoses = new MutableLiveData<>();
        error = new MutableLiveData<>();
        diagnosesDatabaseList = new MutableLiveData<>();

        diagnosisRepository.cancelFutures();
        recommendationRepository.cancelFutures();
        homeRepository.cancelFutures();
        registryRepository.cancelFutures();
        super.onCleared();
    }
}
