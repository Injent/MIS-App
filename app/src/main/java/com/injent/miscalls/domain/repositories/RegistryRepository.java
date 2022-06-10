package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.database.registry.RegistryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryRepository {

    private final RegistryDao dao;
    private final DiagnosisDao diagnosisDao;
    private final CallInfoDao callInfoDao;

    private CompletableFuture<Registry> loadRegistryByIdFuture;
    private CompletableFuture<Void> insertRegistryFuture;
    private CompletableFuture<List<Registry>> loadRegistriesFuture;
    private CompletableFuture<Void> deleteRegistryFuture;

    public RegistryRepository() {
        this.dao = App.getInstance().getRegistryDao();
        this.diagnosisDao = App.getInstance().getDiagnosisDao();
        this.callInfoDao = App.getInstance().getCallInfoDao();
    }

    public void cancelFutures() {
        if (insertRegistryFuture != null)
            insertRegistryFuture.cancel(true);
        if (loadRegistriesFuture != null)
            loadRegistriesFuture.cancel(true);
        if (loadRegistryByIdFuture != null)
            loadRegistryByIdFuture.cancel(true);
        if (deleteRegistryFuture != null)
            deleteRegistryFuture.cancel(true);
    }

    public void insertRegistry(Function<Throwable, Void> ex, Registry registry) {
        insertRegistryFuture = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    dao.insertRegistry(registry);
                    return null;
                })
                .exceptionally(ex);
    }

    public void getRegistries(Function<Throwable, List<Registry>> ex, Consumer<List<Registry>> consumer) {
        loadRegistriesFuture = CompletableFuture
                .supplyAsync(() -> {
                    List<Registry> list = new ArrayList<>();

                    for (Registry registry : dao.getAll()) {
                        list.add(fletchRegistry(registry));
                    }
                    return list;
                })
                .exceptionally(ex);
        loadRegistriesFuture.thenAcceptAsync(consumer);
    }

    public void loadRegistryById(Function<Throwable, Registry> ex, Consumer<Registry> consumer, int id) {
        loadRegistryByIdFuture = CompletableFuture
                .supplyAsync(() -> fletchRegistry(dao.getById(id)))
                .exceptionally(ex);
        loadRegistryByIdFuture.thenAcceptAsync(consumer);
    }

    public void deleteRegistry(Function<Throwable,Void> ex, int id) {
        deleteRegistryFuture = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    dao.delete(id);
                    return null;
                })
                .exceptionally(ex);
    }

    public Registry fletchRegistry(Registry registry) {
        String[] stringIds = registry.getDiagnosesId().split(";");
        int[] ids = new int[stringIds.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Integer.parseInt(stringIds[i]);
        }
        List<Diagnosis> diagnoses = new ArrayList<>();
        for (Integer i : ids) {
            diagnoses.add(diagnosisDao.getById(i));
        }
        registry.setDiagnoses(diagnoses);

        CallInfo callInfo = callInfoDao.getById(registry.getId());
        registry.setCallInfo(callInfo);
        return registry;
    }
}
