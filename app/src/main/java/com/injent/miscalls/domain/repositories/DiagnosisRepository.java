package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiagnosisRepository {

    private final DiagnosisDao dao;

    public DiagnosisRepository() {
        this.dao = AppDatabase.getDiagnosisDao();
    }

    private CompletableFuture<List<Diagnosis>> diagnosesByParent;
    private CompletableFuture<Diagnosis> diagnosisFuture;
    private CompletableFuture<List<Diagnosis>> searchDiagnosis;

    public void cancelFutures() {
        if (diagnosesByParent != null) {
            diagnosesByParent.cancel(true);
        }
        if (diagnosisFuture != null) {
            diagnosisFuture.cancel(true);
        }
        if (searchDiagnosis != null) {
            searchDiagnosis.cancel(true);
        }
    }

    public void searchDiagnoses(String s, Consumer<List<Diagnosis>> consumer) {
        searchDiagnosis = CompletableFuture
                .supplyAsync(() -> dao.searchLike("%" + s + "%"))
        .exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        searchDiagnosis.thenAcceptAsync(consumer);
    }

    public void getDiagnosesByParentId(Function<Throwable, List<Diagnosis>> ex, Consumer<List<Diagnosis>> consumer, int parentId) {
        diagnosesByParent = CompletableFuture
                .supplyAsync(() -> {
                    List<Diagnosis> list = new ArrayList<>();
                    for (Diagnosis d : dao.getAll()) {
                        if (d.getParentId() == parentId) {
                            Diagnosis child = dao.getByParentId(d.getId());
                            if (child != null) {
                                d.setParent(true);
                            }
                            list.add(d);
                        }
                    }
                    return list;
                })
                .exceptionally(ex);
        diagnosesByParent.thenAcceptAsync(consumer);
    }

    public void loadDiagnosisById(Function<Throwable, Diagnosis> ex, Consumer<Diagnosis> consumer, int id) {
        diagnosisFuture = CompletableFuture
                .supplyAsync(() -> dao.getById(id))
                .exceptionally(ex);
        diagnosisFuture.thenAcceptAsync(consumer);
    }

    public Diagnosis getDiagnosisById(int id) {
        return dao.getById(id);
    }
}
