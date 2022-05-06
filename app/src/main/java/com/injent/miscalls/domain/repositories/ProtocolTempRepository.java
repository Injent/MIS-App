package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.domain.HttpManager;
import com.injent.miscalls.data.calllist.QueryToken;
import com.injent.miscalls.data.diagnosis.RecommendationTemp;
import com.injent.miscalls.data.diagnosis.ProtocolTempDao;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import retrofit2.Call;

public class ProtocolTempRepository {

    private final ProtocolTempDao dao;
    private final ExecutorService es;

    public ProtocolTempRepository() {
        this.dao = App.getInstance().getRecommendationTempDao();
        this.es = Executors.newSingleThreadExecutor();
    }

    public Call<List<RecommendationTemp>> getProtocolsFromServer(QueryToken token) {
        return HttpManager.getMisAPI().protocolTemps(token);
    }

    public void clearDb() {
        es.submit(() -> App.getInstance().getProtocolTempDatabase().clearAllTables());
    }

    public void deleteProtocolTemp(int id) {
        es.submit(() -> dao.delete(id));
    }

    public void getProtocolTempById(Function<Throwable, RecommendationTemp> ex, Consumer<RecommendationTemp> consumer, int id) {
        CompletableFuture<RecommendationTemp> future = CompletableFuture
                .supplyAsync(() -> dao.getProtocolById(id))
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }

    public void insertProtocol(RecommendationTemp recommendationTemp) {
        es.submit(() -> dao.insert(recommendationTemp));
    }

    public void insertProtocolTempWithDropDb(List<RecommendationTemp> list) {
        es.submit(() -> {
            App.getInstance().getProtocolTempDatabase().clearAllTables();
            for (RecommendationTemp protocol : list)
                dao.insert(protocol);
        });
    }

    public void getProtocolTemps(Function<Throwable,List<RecommendationTemp>> ex, Consumer<List<RecommendationTemp>> consumer) {
        CompletableFuture<List<RecommendationTemp>> future = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }
}
