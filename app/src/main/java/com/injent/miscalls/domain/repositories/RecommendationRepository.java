package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.recommendation.Recommendation;
import com.injent.miscalls.data.recommendation.RecommendationDao;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class RecommendationRepository {

    private final RecommendationDao dao;

    private CompletableFuture<List<Recommendation>> recommendationsFuture;
    private CompletableFuture<Recommendation> recommendationFuture;

    public RecommendationRepository() {
        dao = App.getInstance().getRecommendationDao();
    }

    public void cancelFutures() {
        if (recommendationsFuture != null) {
            recommendationsFuture.cancel(true);
        }
        if (recommendationFuture != null) {
            recommendationFuture.cancel(true);
        }
    }

    public void loadAllRecommendations(Function<Throwable,List<Recommendation>> ex, Consumer<List<Recommendation>> consumer) {
        recommendationsFuture = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        recommendationsFuture.thenAcceptAsync(consumer);
    }

    public void loadRecommendationById(Function<Throwable, Recommendation> ex, Consumer<Recommendation> consumer, int id) {
        recommendationFuture = CompletableFuture
                .supplyAsync(() -> dao.getById(id))
                .exceptionally(ex);
        recommendationFuture.thenAcceptAsync(consumer);
    }
}
