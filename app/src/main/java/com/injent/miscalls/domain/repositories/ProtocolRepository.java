package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.savedprotocols.Inspection;
import com.injent.miscalls.data.savedprotocols.InspectionDao;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProtocolRepository {

    private final InspectionDao dao;
    private final ExecutorService es;

    public ProtocolRepository() {
        this.dao = App.getInstance().getProtocolDao();
        this.es = Executors.newSingleThreadExecutor();
    }

    public void clearDb() {
        es.submit(() -> App.getInstance().getProtocolDatabase().clearAllTables());
    }

    public void deleteProtocol(int id) {
        es.submit(() -> dao.delete(id));
    }

    public void getProtocolById(Function<Throwable, Inspection> ex, Consumer<Inspection> consumer, int id) {
        CompletableFuture<Inspection> future = CompletableFuture
                .supplyAsync(() -> dao.getProtocolByPatientId(id))
                .exceptionally(ex);

        future.thenAcceptAsync(consumer);
    }

    public Inspection getProtocolByName(int patientId) {
        Future<Inspection> protocolFuture = es.submit(() -> dao.getProtocolByPatientId(patientId));
        try {
            return protocolFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public void insertProtocol(Inspection inspection) {
        es.submit(() -> dao.insert(inspection));
    }

    public Boolean insertProtocolWithDropDb(List<Inspection> list) {
        Future<Boolean> booleanFuture = es.submit(() -> {
            App.getInstance().getProtocolDatabase().clearAllTables();
            for (Inspection protocol : list) {
                dao.insert(protocol);
            }
            return true;
        });
        try {
            return booleanFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
    }

    public void getProtocols(Function<Throwable, List<Inspection>> ex, Consumer<List<Inspection>> consumer) {
        CompletableFuture<List<Inspection>> future = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }

    public boolean checkDuplication(Inspection inspection, int patientId) {
        Future<Boolean> future = es.submit(() -> dao.getProtocolByPatientId(patientId).getPatientId() == inspection.getPatientId());

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
