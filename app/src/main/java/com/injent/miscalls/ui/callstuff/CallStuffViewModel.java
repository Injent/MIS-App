package com.injent.miscalls.ui.callstuff;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.R;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.recommendation.Recommendation;
import com.injent.miscalls.domain.repositories.DiagnosisRepository;
import com.injent.miscalls.domain.repositories.HomeRepository;
import com.injent.miscalls.domain.repositories.RecommendationRepository;
import com.injent.miscalls.domain.repositories.RegistryRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private MutableLiveData<List<Recommendation>> recommendationsList = new MutableLiveData<>();

    public CallStuffViewModel() {
        registryRepository = new RegistryRepository();
        homeRepository = new HomeRepository();
        diagnosisRepository = new DiagnosisRepository();
        recommendationRepository = new RecommendationRepository();
    }

    public LiveData<List<Diagnosis>> getCurrentDiagnosesLiveData() {
        return currentDiagnoses;
    }

    public void setCurrentDiagnoses(List<Diagnosis> list) {
        currentDiagnoses.setValue(list);
    }

    public List<Diagnosis> deleteItemFromList(List<Diagnosis> list, Diagnosis diagnosis) {
        List<Diagnosis> diagnosisList = new ArrayList<>(list);
        int idToDelete = 0;
        for (int i = 0; i < diagnosisList.size(); i++) {
            if (diagnosisList.get(i).equals(diagnosis)) {
                idToDelete = i;
                break;
            }
        }
        diagnosisList.remove(idToDelete);
        currentDiagnoses.setValue(diagnosisList);
        return diagnosisList;
    }

    public List<Diagnosis> addItemToList(Context context, List<Diagnosis> list, Diagnosis diagnosis) {
        List<Diagnosis> currentList = new ArrayList<>(list);
        boolean added = false;
        for (Diagnosis d : currentList) {
            if (d.equals(diagnosis)) {
                Toast.makeText(context, R.string.diagnosisAlreadyAdded,Toast.LENGTH_SHORT).show();
                added = true;
                break;
            }
        }
        if (!added) {
            currentList.add(diagnosis);
        }
        currentDiagnoses.setValue(currentList);
        return currentList;
    }

    public LiveData<CallInfo> getCallLiveData() {
        return selectedCall;
    }

    public LiveData<Throwable> getCallErrorLiveData() {
        return callError;
    }

    public void loadCall(int callId) {
        homeRepository.loadCallInfoById(throwable -> {
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
        CallInfo newCallInfo = selectedCall.getValue();
        if (newCallInfo == null || list == null || currentInspection.getValue() == null) {
            error.postValue(new IllegalStateException());
            return;
        }
        Registry registry = new Registry();
        registry.setId(newCallInfo.getId());
        registry.setRecommendation(currentRecommendation.getValue());
        String formedInspection = CallInfo.autoFillString(newCallInfo,currentInspection.getValue());
        registry.setInspection(formedInspection);

        String diagnosisString = Diagnosis.listToStringIds(list, ';');
        registry.setDiagnosesId(diagnosisString);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy\nhh:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        registry.setCreateDate(currentDate);

        registryRepository.insertRegistry(throwable -> {
            error.postValue(throwable);
            return null;
        }, registry);

        newCallInfo.setInspected(true);

        homeRepository.insertCall(throwable -> {
            error.postValue(throwable);
            return null;
        }, newCallInfo);
    }

    public LiveData<String> getCurrentRecommendationLiveData() {
        return currentRecommendation;
    }

    public void setCurrentRecommendation(String s) {
        currentRecommendation.setValue(s);
    }

    public void findRecommendation(int id) {
        recommendationRepository.loadRecommendationById(throwable -> {
            error.postValue(throwable);
            return null;
        }, recommendation -> currentRecommendation.postValue(recommendation.getContent()), id);
    }


    public LiveData<List<Recommendation>> getRecommendationsListLiveData() {
        return recommendationsList;
    }

    public void loadRecommendationsList() {
        recommendationRepository.loadAllRecommendations(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, list -> recommendationsList.postValue(list));
    }

    public LiveData<String> getCurrentInspectionLiveData() {
        return currentInspection;
    }

    public void setCurrentInspection(String s) {
        currentInspection.setValue(s);
    }

    public LiveData<List<Diagnosis>> getDiagnosesListLiveData() {
        return diagnosesDatabaseList;
    }

    public void loadDiagnosisList(int id) {
        diagnosisRepository.getDiagnosesByParentId(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, list -> diagnosesDatabaseList.postValue(list), id);
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
        recommendationsList = new MutableLiveData<>();
        diagnosesDatabaseList = new MutableLiveData<>();

        diagnosisRepository.cancelFutures();
        recommendationRepository.cancelFutures();
        homeRepository.cancelFutures();
        registryRepository.cancelFutures();
        super.onCleared();
    }
}
