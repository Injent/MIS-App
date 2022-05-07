package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.registry.Registry;
import com.injent.miscalls.data.registry.RegistryDao;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class RegistryRepository {

    private final RegistryDao dao;
    private final ExecutorService es;

    public RegistryRepository() {
        this.dao = App.getInstance().getProtocolDao();
        this.es = Executors.newSingleThreadExecutor();
    }

    public void clearDb() {
        es.submit(() -> App.getInstance().getProtocolDatabase().clearAllTables());
    }

    public void deleteProtocol(int id) {
        es.submit(() -> dao.delete(id));
    }

    public void getProtocolById(Function<Throwable, Registry> ex, Consumer<Registry> consumer, int id) {
        CompletableFuture<Registry> future = CompletableFuture
                .supplyAsync(() -> dao.getProtocolByPatientId(id))
                .exceptionally(ex);

        future.thenAcceptAsync(consumer);
    }

    public Registry getProtocolByName(int patientId) {
        Future<Registry> protocolFuture = es.submit(() -> dao.getProtocolByPatientId(patientId));
        try {
            return protocolFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public void insertRegistry(Registry registry) {
        es.submit(() -> dao.insert(registry));
    }

    public Boolean insertProtocolWithDropDb(List<Registry> list) {
        Future<Boolean> booleanFuture = es.submit(() -> {
            App.getInstance().getProtocolDatabase().clearAllTables();
            for (Registry protocol : list) {
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

    public void getRegistry(Function<Throwable, List<Registry>> ex, Consumer<List<Registry>> consumer) {
        CompletableFuture<List<Registry>> future = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }

    public boolean checkDuplication(Registry registry, int patientId) {
        Future<Boolean> future = es.submit(() -> dao.getProtocolByPatientId(patientId).getCallId() == registry.getCallId());

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
