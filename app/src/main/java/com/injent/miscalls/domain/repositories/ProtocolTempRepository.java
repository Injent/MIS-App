package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.api.HttpManager;
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.data.templates.ProtocolTempDao;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;

public class ProtocolTempRepository {

    private final ProtocolTempDao dao;
    private final ExecutorService es;

    public ProtocolTempRepository() {
        this.dao = App.getInstance().getProtocolTempDao();
        this.es = Executors.newSingleThreadExecutor();
    }

    public Call<List<ProtocolTemp>> getProtocolsFromServer(QueryToken token) {
        return HttpManager.getMisAPI().protocolTemps(token);
    }

    public void clearDb() {
        es.submit(() -> App.getInstance().getProtocolTempDatabase().clearAllTables());
    }

    public void deleteProtocolTemp(int id) {
        es.submit(() -> dao.delete(id));
    }

    public ProtocolTemp getProtocolTempById(int id) {
        Future<ProtocolTemp> protocolTempFuture = es.submit(() -> dao.getProtocolById(id));
        try {
            return protocolTempFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public void insertProtocol(ProtocolTemp protocolTemp) {
        es.submit(() -> dao.insert(protocolTemp));
    }

    public void insertProtocolTempWithDropDb(List<ProtocolTemp> list) {
        es.submit(() -> {
            App.getInstance().getProtocolTempDatabase().clearAllTables();
            for (ProtocolTemp protocol : list)
                dao.insert(protocol);
        });
    }

    public List<ProtocolTemp> getProtocolTemps() {
        Future<List<ProtocolTemp>> listFuture = es.submit(dao::getAll);
        try {
            return listFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return Collections.emptyList();
    }
}
