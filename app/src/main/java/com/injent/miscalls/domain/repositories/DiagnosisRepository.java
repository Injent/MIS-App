package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.diagnosis.Diagnosis;
import com.injent.miscalls.data.diagnosis.DiagnosisDao;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiagnosisRepository {

    private final DiagnosisDao dao;

    public DiagnosisRepository() {
        this.dao = App.getInstance().getDiagnosisDao();
    }

    public void getAllDiagnosis(Function<Throwable, List<Diagnosis>> ex, Consumer<List<Diagnosis>> consumer) {
        CompletableFuture<List<Diagnosis>> future = CompletableFuture
                .supplyAsync(() -> dao.getAll())
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }
}
