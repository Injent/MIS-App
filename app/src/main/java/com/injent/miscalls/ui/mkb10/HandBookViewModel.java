package com.injent.miscalls.ui.mkb10;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.calls.ListEmptyException;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.domain.repositories.DiagnosisRepository;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class HandBookViewModel extends ViewModel {

    private final DiagnosisRepository repository;

    private MutableLiveData<Diagnosis> previousDiagnosis = new MutableLiveData<>();
    private MutableLiveData<List<Diagnosis>> diagnoses = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private final MutableLiveData<Diagnosis> selectedDiagnosis = new MutableLiveData<>();

    public HandBookViewModel() {
        repository = new DiagnosisRepository();
    }

    public LiveData<List<Diagnosis>> getDiagnosesLiveData() {
        return diagnoses;
    }

    public void loadDiagnosesByParent(int id) {
        repository.getDiagnosesByParentId(throwable -> {
            error.postValue(throwable);
            return Collections.emptyList();
        }, list -> {
            diagnoses.postValue(list);
        }, id);
    }

    public LiveData<Diagnosis> getSelectedDiagnosisLiveData() {
        return selectedDiagnosis;
    }

    public LiveData<Diagnosis> getPreviousDiagnosis() {
        return previousDiagnosis;
    }

    public void loadDiagnosisById(int id) {
        repository.loadDiagnosisById(throwable -> {
            error.postValue(throwable);
            return null;
        }, diagnosis -> {
            if (previousDiagnosis != null)
                previousDiagnosis.postValue(diagnosis);
        }, id);
    }

    public void searchDiagnoses(String s) {
        repository.searchDiagnoses(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        }, list -> diagnoses.postValue(list), s);
    }

    @Override
    protected void onCleared() {
        diagnoses = new MutableLiveData<>();
        previousDiagnosis = new MutableLiveData<>();

        repository.cancelFutures();
        super.onCleared();
    }
}