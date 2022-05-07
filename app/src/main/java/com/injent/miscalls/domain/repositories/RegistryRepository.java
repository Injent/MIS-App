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
        this.dao = App.getInstance().getRegistryDao();
        this.es = Executors.newSingleThreadExecutor();
    }

    public void insertRegistry(Registry registry) {
        es.submit(() -> dao.insert(registry));
    }

    public void getRegistry(Function<Throwable, List<Registry>> ex, Consumer<List<Registry>> consumer) {
        CompletableFuture<List<Registry>> future = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }
}
