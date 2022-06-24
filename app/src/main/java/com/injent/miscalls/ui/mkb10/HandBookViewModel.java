package com.injent.miscalls.ui.mkb10;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.domain.repositories.DiagnosisRepository;

import java.util.List;

public class HandBookViewModel extends ViewModel {

    private final DiagnosisRepository repository;

    private LiveData<List<Diagnosis>> diagnoses;
    private LiveData<List<Diagnosis>> searchDiagnoses;
    private LiveData<Throwable> error;

    public HandBookViewModel() {
        repository = new DiagnosisRepository();
        diagnoses = repository.getDiagnoses();
        searchDiagnoses = repository.getSearchDiagnoses();
        error = repository.getError();
    }

    public LiveData<List<Diagnosis>> getDiagnoses() {
        return diagnoses;
    }

    public LiveData<List<Diagnosis>> getSearchDiagnoses() {
        return searchDiagnoses;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void loadDiagnosesByParent(int id) {
        repository.getDiagnosesByParentId(id);
    }

    public void searchDiagnoses(String s, int limit) {
        repository.searchDiagnoses(s, limit);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        diagnoses = null;
        searchDiagnoses = null;
        error = null;

        repository.clear();
    }
}