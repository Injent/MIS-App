package com.injent.miscalls.domain.repositories;

import android.util.Log;

import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.database.registry.RegistryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryRepository {

    private final RegistryDao registryDao;
    private final DiagnosisDao diagnosisDao;
    private final CallInfoDao callInfoDao;

    private CompletableFuture<Registry> loadRegistryByIdFuture;
    private CompletableFuture<Void> insertRegistryFuture;
    private CompletableFuture<List<Registry>> loadRegistriesFuture;
    private CompletableFuture<Void> deleteRegistryFuture;
    private CompletableFuture<Registry> loadRegistryByCallId;

    public RegistryRepository() {
        this.registryDao = AppDatabase.getRegistryDao();
        this.diagnosisDao = AppDatabase.getDiagnosisDao();
        this.callInfoDao = AppDatabase.getCallInfoDao();
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
                    long id = registryDao.insertRegistry(registry);
                    Objectively obj = registry.getObjectively();
                    obj.setRegistryId((int) id);
                    registryDao.insertObjectively(obj);
                    return null;
                })
                .exceptionally(ex);
    }

    public void getRegistries(Function<Throwable, List<Registry>> ex, Consumer<List<Registry>> consumer) {
        loadRegistriesFuture = CompletableFuture
                .supplyAsync((Supplier<List<Registry>>) () -> {
                    List<Registry> list = registryDao.getAllRawRegistries();
                    for (int i = 0; i < list.size(); i++) {
                        List<Diagnosis> diagnoses = new ArrayList<>();
                        String[] diagnosesId = list.get(i).getDiagnosesId().split(";");
                        for (String s : diagnosesId) {
                            diagnoses.add(registryDao.getDiagnosis(Integer.parseInt(s)));
                        }
                        list.get(i).setDiagnoses(diagnoses);
                        list.get(i).setCallInfo(registryDao.getCallInfo(list.get(i).getCallId()));
                    }
                    return list;
                })
                .exceptionally(ex);
        loadRegistriesFuture.thenAcceptAsync(consumer);
    }

    public void loadRegistryById(Function<Throwable, Registry> ex, Consumer<Registry> consumer, int id) {
        loadRegistryByIdFuture = CompletableFuture
                .supplyAsync(() -> {
                    Registry registry = registryDao.getRegistry(id);
                    registry.setObjectively(registryDao.getObjectivelyByRegId(id));
                    registry.setCallInfo(callInfoDao.getById(registry.getCallId()));

                    List<Diagnosis> diagnoses = new ArrayList<>();
                    String[] diagnosesId = registry.getDiagnosesId().split(";");
                    for (String s : diagnosesId) {
                        diagnoses.add(registryDao.getDiagnosis(Integer.parseInt(s)));
                    }
                    registry.setDiagnoses(diagnoses);
                    return registry;
                })
                .exceptionally(ex);
        loadRegistryByIdFuture.thenAcceptAsync(consumer);
    }

    public void loadRegistryByCallId(Function<Throwable, Registry> ex, Consumer<Registry> consumer, int id) {
        loadRegistryByCallId = CompletableFuture
                .supplyAsync(() -> {
                    Registry registry = registryDao.getRegistryByCallId(id);
                    registry.setObjectively(registryDao.getObjectivelyByRegId(id));
                    registry.setCallInfo(callInfoDao.getById(registry.getCallId()));

                    List<Diagnosis> diagnoses = new ArrayList<>();
                    String[] diagnosesId = registry.getDiagnosesId().split(";");
                    for (String s : diagnosesId) {
                        diagnoses.add(registryDao.getDiagnosis(Integer.parseInt(s)));
                    }
                    registry.setDiagnoses(diagnoses);
                    return registry;
                })
                .exceptionally(ex);
        loadRegistryByCallId.thenAcceptAsync(consumer);
    }

    public void deleteRegistry(Function<Throwable,Void> ex, int id) {
        deleteRegistryFuture = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    registryDao.deleteRegistry(id);
                    registryDao.deleteObjectivelyByRegId(id);
                    return null;
                })
                .exceptionally(ex);
    }

    public void dropTable() {
        //dao.deleteAll();
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
