package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
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
        this.dao = App.getInstance().getDiagnosisDao();
    }

    private CompletableFuture<List<Diagnosis>> diagnosesByParent;

    public void cancelFutures() {
        if (diagnosesByParent != null) {
            diagnosesByParent.cancel(true);
        }
        if (diagnosisFuture != null) {
            diagnosisFuture.cancel(true);
        }
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

    private CompletableFuture<Diagnosis> diagnosisFuture;

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
