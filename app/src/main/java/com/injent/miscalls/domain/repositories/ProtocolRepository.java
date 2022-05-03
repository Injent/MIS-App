package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.data.savedprotocols.ProtocolDao;
import java.util.Collections;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProtocolRepository {

    private final ProtocolDao dao;
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

    public void getProtocolById(Function<Throwable,Protocol> ex, Consumer<Protocol> consumer, int id) {
        CompletableFuture<Protocol> future = CompletableFuture
                .supplyAsync(() -> dao.getProtocolByPatientId(id))
                .exceptionally(ex);

        future.thenAcceptAsync(consumer);
    }

    public Protocol getProtocolByName(int patientId) {
        Future<Protocol> protocolFuture = es.submit(() -> dao.getProtocolByPatientId(patientId));
        try {
            return protocolFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public void insertProtocol(Protocol protocol) {
        es.submit(() -> dao.insert(protocol));
    }

    public Boolean insertProtocolWithDropDb(List<Protocol> list) {
        Future<Boolean> booleanFuture = es.submit(() -> {
            App.getInstance().getProtocolDatabase().clearAllTables();
            for (Protocol protocol : list) {
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

    public void getProtocols(Function<Throwable, List<Protocol>> ex, Consumer<List<Protocol>> consumer) {
        CompletableFuture<List<Protocol>> future = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }

    public boolean checkDuplication(Protocol protocol, int patientId) {
        Future<Boolean> future = es.submit(() -> dao.getProtocolByPatientId(patientId).getPatientId() == protocol.getPatientId());

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
