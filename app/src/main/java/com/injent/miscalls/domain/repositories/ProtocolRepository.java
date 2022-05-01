package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.data.savedprotocols.ProtocolDao;
import java.util.Collections;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public Protocol getProtocolById(int id) {
        Future<Protocol> protocolFuture = es.submit(() -> dao.getProtocolById(id));
        try {
            return protocolFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
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

    public List<Protocol> getProtocols() {
        Future<List<Protocol>> listFuture = es.submit(dao::getAll);
        try {
            return listFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return Collections.emptyList();
    }
}
