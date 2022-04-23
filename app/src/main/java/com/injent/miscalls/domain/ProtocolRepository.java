package com.injent.miscalls.domain;

import com.injent.miscalls.App;
import com.injent.miscalls.data.savedprotocols.Protocol;
import com.injent.miscalls.data.savedprotocols.ProtocolDao;
import com.injent.miscalls.data.templates.ProtocolTemp;

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
        es.submit(new Runnable() {
            @Override
            public void run() {
                App.getInstance().getProtocolDatabase().clearAllTables();
            }
        });
    }

    public void deleteProtocol(int id) {
        es.submit(new Runnable() {
            @Override
            public void run() {
                dao.delete(id);
            }
        });
    }

    public Protocol getProtocolById(int id) {
        Future<Protocol> protocolFuture = es.submit(new Callable<Protocol>() {
            @Override
            public Protocol call() {
                return dao.getProtocol(id);
            }
        });
        try {
            return protocolFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertProtocol(Protocol protocol) {
        es.submit(new Runnable() {
            @Override
            public void run() {
                dao.insert(protocol);
            }
        });
    }

    public Boolean insertProtocolWithDropDb(List<Protocol> list) {
        Future<Boolean> booleanFuture = es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                App.getInstance().getProtocolDatabase().clearAllTables();
                for (Protocol protocol : list) {
                    dao.insert(protocol);
                }
                return true;
            }
        });
        try {
            return booleanFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Protocol> getProtocols() {
        Future<List<Protocol>> listFuture = es.submit(new Callable<List<Protocol>>() {
            @Override
            public List<Protocol> call(){
                return dao.getAll();
            }
        });
        try {
            return listFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
