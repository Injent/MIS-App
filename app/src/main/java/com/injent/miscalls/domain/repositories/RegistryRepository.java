package com.injent.miscalls.domain.repositories;

import android.accounts.NetworkErrorException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.medcall.MedCall;
import com.injent.miscalls.data.database.medcall.MedCallDao;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;
import com.injent.miscalls.data.database.diagnosis.DiagnosisDao;
import com.injent.miscalls.data.database.registry.Objectively;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.database.registry.RegistryDao;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.dto.RegistryDto;
import com.injent.miscalls.ui.inspection.AdditionalField;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistryRepository {

    private final RegistryDao registryDao;
    private final DiagnosisDao diagnosisDao;
    private final MedCallDao medCallDao;

    private MutableLiveData<Registry> registryData;
    private MutableLiveData<String> docSentMessage;
    private MutableLiveData<Throwable> error;

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
        this.medCallDao = AppDatabase.getCallInfoDao();
        registryData = new MutableLiveData<>();
        docSentMessage = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public void clear() {
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
        registryData = null;
        docSentMessage = null;
        error = null;
    }

    public LiveData<String> getDocSentMessage() {
        return docSentMessage;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<Registry> getRegistry() {
        return registryData;
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

    public void loadRegistryById(int id) {
        loadRegistryByIdFuture = CompletableFuture
                .supplyAsync(() -> {
                    Registry registry = registryDao.getRegistry(id, App.getUser().getId());
                    registry.setObjectively(registryDao.getObjectivelyByRegId(id));
                    registry.setCallInfo(medCallDao.getById(registry.getCallId()));

                    List<Diagnosis> diagnoses = new ArrayList<>();
                    String[] diagnosesId = registry.getDiagnosesId().split(";");
                    for (String s : diagnosesId) {
                        diagnoses.add(registryDao.getDiagnosis(Integer.parseInt(s)));
                    }
                    registry.setDiagnoses(diagnoses);
                    return registry;
                })
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    throwable.printStackTrace();
                    return null;
                });
        loadRegistryByIdFuture.thenAcceptAsync(registry -> registryData.postValue(registry));
    }

    public void sendDocument(Registry registry) {
        NetworkManager.getMisAPI().uploadDocument(RegistryDto.toDto(registry)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccessful()) {
                        docSentMessage.postValue(response.body().getMessage());
                    } else {
                        error.postValue(new NetworkErrorException());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JResponse> call, @NonNull Throwable t) {
                error.postValue(t);
                t.printStackTrace();
            }
        });
    }

    public void loadRegistryByCallId(Function<Throwable, Registry> ex, Consumer<Registry> consumer, int id) {
        loadRegistryByCallId = CompletableFuture
                .supplyAsync(() -> {
                    Registry registry = registryDao.getRegistryByCallId(id);
                    registry.setObjectively(registryDao.getObjectivelyByRegId(registry.getId()));
                    registry.setCallInfo(medCallDao.getById(registry.getCallId()));

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
                    MedCall medCall = medCallDao.getById(registry.getCallId());
                    medCall.setInspected(false);
                    medCallDao.updateCall(medCall);
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
