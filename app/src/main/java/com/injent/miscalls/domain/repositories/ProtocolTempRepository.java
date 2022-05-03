package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.HttpManager;
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.data.templates.ProtocolTemp;
import com.injent.miscalls.data.templates.ProtocolTempDao;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public void getProtocolTempById(Function<Throwable, ProtocolTemp> ex, Consumer<ProtocolTemp> consumer, int id) {
        CompletableFuture<ProtocolTemp> future = CompletableFuture
                .supplyAsync(() -> dao.getProtocolById(id))
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
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

    public void getProtocolTemps(Function<Throwable,List<ProtocolTemp>> ex, Consumer<List<ProtocolTemp>> consumer) {
        CompletableFuture<List<ProtocolTemp>> future = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }
}
