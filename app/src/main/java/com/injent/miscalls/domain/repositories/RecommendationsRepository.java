package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.domain.HttpManager;
import com.injent.miscalls.data.calllist.QueryToken;
import com.injent.miscalls.data.recommendation.Recommendation;
import com.injent.miscalls.data.recommendation.RecommendationDao;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import retrofit2.Call;

public class RecommendationsRepository {

    private final RecommendationDao dao;
    private final ExecutorService es;

    public RecommendationsRepository() {
        this.dao = App.getInstance().getRecommendationDao();
        this.es = Executors.newSingleThreadExecutor();
    }

    public Call<List<Recommendation>> getProtocolsFromServer(QueryToken token) {
        return HttpManager.getMisAPI().protocolTemps(token);
    }

    public void deleteProtocolTemp(int id) {
        es.submit(() -> dao.delete(id));
    }

    public void getById(Function<Throwable, Recommendation> ex, Consumer<Recommendation> consumer, int id) {
        CompletableFuture<Recommendation> future = CompletableFuture
                .supplyAsync(() -> dao.getById(id))
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }

    public void insertProtocol(Recommendation recommendation) {
        es.submit(() -> dao.insert(recommendation));
    }

    public void insertWithDropTable(List<Recommendation> list) {
        es.submit(() -> {
            App.getInstance().getAppDatabase().recommendationDao().clear();
            dao.insert((Recommendation) list);
        });
    }

    public void getAll(Function<Throwable,List<Recommendation>> ex, Consumer<List<Recommendation>> consumer) {
        CompletableFuture<List<Recommendation>> future = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }
}
