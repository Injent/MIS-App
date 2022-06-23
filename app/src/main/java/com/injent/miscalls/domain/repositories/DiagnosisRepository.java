package com.injent.miscalls.domain.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DiagnosisRepository {

    private final DiagnosisDao dao;

    private MutableLiveData<List<Diagnosis>> diagnoses;
    private MutableLiveData<List<Diagnosis>> searchDiagnosesList;
    private MutableLiveData<Throwable> error;

    private CompletableFuture<List<Diagnosis>> diagnosesByParent;
    private CompletableFuture<Diagnosis> diagnosisFuture;
    private CompletableFuture<List<Diagnosis>> searchNotParentDiagnoses;
    private CompletableFuture<List<Diagnosis>> searchDiagnoses;

    public DiagnosisRepository() {
        this.dao = AppDatabase.getDiagnosisDao();
        diagnoses = new MutableLiveData<>();
        searchDiagnosesList = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public void clear() {
        if (diagnosesByParent != null) {
            diagnosesByParent.cancel(true);
        }
        if (diagnosisFuture != null) {
            diagnosisFuture.cancel(true);
        }
        if (searchNotParentDiagnoses != null) {
            searchNotParentDiagnoses.cancel(true);
        }
        if (searchDiagnoses != null) {
            searchDiagnoses.cancel(true);
        }
        diagnoses = null;
        searchDiagnosesList = null;
        error = null;
    }

    public LiveData<List<Diagnosis>> getSearchDiagnoses() {
        return searchDiagnosesList;
    }

    public LiveData<List<Diagnosis>> getDiagnoses() {
        return diagnoses;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void searchNotParentDiagnoses(String s) {
        searchNotParentDiagnoses = CompletableFuture
                .supplyAsync(() -> dao.searchNotParentLike("%" + s + "%"))
        .exceptionally(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        });
        searchNotParentDiagnoses.thenAcceptAsync(list -> searchDiagnosesList.postValue(list));
    }

    public void searchDiagnoses(String s) {
        searchDiagnoses = CompletableFuture
                .supplyAsync(() -> dao.searchLike("%" + s + "%"))
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    throwable.printStackTrace();
                    return null;
                });
        searchDiagnoses.thenAcceptAsync(list -> searchDiagnosesList.postValue(list));
    }

    public void getDiagnosesByParentId(int parentId) {
        diagnosesByParent = CompletableFuture
                .supplyAsync(() -> dao.getByParentId(parentId))
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    throwable.printStackTrace();
                    return null;
                });
        diagnosesByParent.thenAcceptAsync(list -> diagnoses.postValue(list));
    }

    public void loadDiagnosisById(Function<Throwable, Diagnosis> ex, Consumer<Diagnosis> consumer, int id) {
        diagnosisFuture = CompletableFuture
                .supplyAsync(() -> dao.getById(id))
                .exceptionally(ex);
        diagnosisFuture.thenAcceptAsync(consumer);
    }
}
