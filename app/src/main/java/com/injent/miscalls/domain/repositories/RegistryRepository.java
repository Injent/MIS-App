package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.calls.MedCall;
import com.injent.miscalls.data.database.calls.CallDao;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.database.registry.RegistryDao;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.rest.dto.RegistryDto;
import com.injent.miscalls.ui.adapters.AdditionalField;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import retrofit2.Call;

public class RegistryRepository {

    private final RegistryDao registryDao;
    private final DiagnosisDao diagnosisDao;
    private final CallDao callDao;

    private CompletableFuture<Registry> loadRegistryByIdFuture;
    private CompletableFuture<Void> insertRegistryFuture;
    private CompletableFuture<List<Registry>> loadRegistriesFuture;
    private CompletableFuture<Void> deleteRegistryFuture;
    private CompletableFuture<Registry> loadRegistryByCallId;
    private CompletableFuture<Void> sendRegistries;
    private CompletableFuture<List<AdditionalField>> configureAdditionalFields;

    public RegistryRepository() {
        this.registryDao = AppDatabase.getRegistryDao();
        this.diagnosisDao = AppDatabase.getDiagnosisDao();
        this.callDao = AppDatabase.getCallInfoDao();
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
        if (loadRegistryByCallId != null)
            loadRegistryByCallId.cancel(true);
        if (sendRegistries != null)
            sendRegistries.cancel(true);
        if (configureAdditionalFields != null)
            configureAdditionalFields.cancel(true);
    }

    public void insertRegistry(Function<Throwable, Void> ex, Registry registry) {
        insertRegistryFuture = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    if (registryDao.getRegistry(registry.getId(), App.getUser().getId()) != null) {
                        registryDao.updateRegistry(registry);
                        registryDao.updateObjectively(registry.getObjectively());
                        return null;
                    }
                    registry.setUserId(App.getUser().getId());
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
                .supplyAsync(() -> {
                    List<Registry> list = registryDao.getAllRawRegistries(App.getUser().getId());
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
                    Registry registry = registryDao.getRegistry(id, App.getUser().getId());
                    registry.setObjectively(registryDao.getObjectivelyByRegId(id));
                    registry.setCallInfo(callDao.getById(registry.getCallId()));

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

    public Call sendDocument(Registry registry) {
        return NetworkManager.getMisAPI().uploadDocument(RegistryDto.toDto(registry));
    }

    public void loadRegistryByCallId(Function<Throwable, Registry> ex, Consumer<Registry> consumer, int id) {
        loadRegistryByCallId = CompletableFuture
                .supplyAsync(() -> {
                    Registry registry = registryDao.getRegistryByCallId(id);
                    registry.setObjectively(registryDao.getObjectivelyByRegId(registry.getId()));
                    registry.setCallInfo(callDao.getById(registry.getCallId()));

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

    public void deleteRegistry(Function<Throwable,Void> ex, Consumer<Void> consumer, int id) {
        deleteRegistryFuture = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    Registry registry = registryDao.getRegistry(id, App.getUser().getId());
                    registryDao.deleteRegistry(id);
                    registryDao.deleteObjectivelyByRegId(id);
                    MedCall medCall = callDao.getById(registry.getCallId());
                    medCall.setInspected(false);
                    callDao.updateCall(medCall);
                    return null;
                })
                .exceptionally(ex);
        deleteRegistryFuture.thenAcceptAsync(consumer);
    }

    public void dropTable(Function<Throwable, Void> ex, Consumer<Void> consumer) {
        sendRegistries = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    registryDao.deleteAllRegistries();
                    registryDao.deleteAllObj();
                    return null;
                })
                .exceptionally(ex);
        sendRegistries.thenAcceptAsync(consumer);
    }

    public void configureAdditionalFields(Function<Throwable, List<AdditionalField>> ex, Consumer<List<AdditionalField>> consumer) {
        configureAdditionalFields = CompletableFuture
                .supplyAsync(new Supplier<List<AdditionalField>>() {
                    @Override
                    public List<AdditionalField> get() {

                        return null;
                    }
                })
                .exceptionally(ex);
        configureAdditionalFields.thenAcceptAsync(consumer);
    }
}
