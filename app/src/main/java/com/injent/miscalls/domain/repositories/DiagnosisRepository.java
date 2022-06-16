package com.injent.miscalls.domain.repositories;

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

    public DiagnosisRepository() {
        this.dao = AppDatabase.getDiagnosisDao();
    }

    private CompletableFuture<List<Diagnosis>> diagnosesByParent;
    private CompletableFuture<Diagnosis> diagnosisFuture;
    private CompletableFuture<List<Diagnosis>> searchNotParentDiagnoses;
    private CompletableFuture<List<Diagnosis>> searchDiagnoses;

    public void cancelFutures() {
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
    }

    public void searchNotParentDiagnoses(String s, Consumer<List<Diagnosis>> consumer) {
        searchNotParentDiagnoses = CompletableFuture
                .supplyAsync(() -> dao.searchNotParentLike("%" + s + "%"))
        .exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        searchNotParentDiagnoses.thenAcceptAsync(consumer);
    }

    public void searchDiagnoses(Function<Throwable, List<Diagnosis>> ex, Consumer<List<Diagnosis>> consumer, String s) {
        searchDiagnoses = CompletableFuture
                .supplyAsync(() -> dao.searchLike("%" + s + "%"))
                .exceptionally(ex);
        searchDiagnoses.thenAcceptAsync(consumer);
    }

    public void getDiagnosesByParentId(Function<Throwable, List<Diagnosis>> ex, Consumer<List<Diagnosis>> consumer, int parentId) {
        diagnosesByParent = CompletableFuture
                .supplyAsync(() -> dao.getByParentId(parentId))
                .exceptionally(ex);
        diagnosesByParent.thenAcceptAsync(consumer);
    }

    public void loadDiagnosisById(Function<Throwable, Diagnosis> ex, Consumer<Diagnosis> consumer, int id) {
        diagnosisFuture = CompletableFuture
                .supplyAsync(() -> dao.getById(id))
                .exceptionally(ex);
        diagnosisFuture.thenAcceptAsync(consumer);
    }
}
