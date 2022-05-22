package com.injent.miscalls.domain.repositories;

import android.content.Context;
import android.content.res.AssetManager;
import android.print.PDFPrint;
import android.util.Log;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;
import com.injent.miscalls.data.database.registry.Registry;
import com.injent.miscalls.data.database.registry.RegistryDao;
import com.injent.miscalls.data.recommendation.Recommendation;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryRepository {

    private final RegistryDao dao;
    private final DiagnosisDao diagnosisDao;
    private final CallInfoDao callInfoDao;

    private CompletableFuture<Registry> registryFuture;
    private CompletableFuture<Void> insertFuture;
    private CompletableFuture<List<Registry>> registriesFuture;

    public RegistryRepository() {
        this.dao = App.getInstance().getRegistryDao();
        this.diagnosisDao = App.getInstance().getDiagnosisDao();
        this.callInfoDao = App.getInstance().getCallInfoDao();
    }

    public void cancelFutures() {
        if (insertFuture != null)
            insertFuture.cancel(true);
        if (registriesFuture != null)
            registriesFuture.cancel(true);
        if (registryFuture != null)
            registryFuture.cancel(true);
    }

    public void insertRegistry(Function<Throwable, Void> ex, Registry registry) {
        insertFuture = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    dao.insert(registry);
                    return null;
                })
                .exceptionally(ex);
    }

    public void getRegistries(Function<Throwable, List<Registry>> ex, Consumer<List<Registry>> consumer) {
        registriesFuture = CompletableFuture
                .supplyAsync(() -> {
                    List<Registry> list = new ArrayList<>();

                    for (Registry registry : dao.getAll()) {
                        list.add(fletchRegistry(registry));
                    }
                    return list;
                })
                .exceptionally(ex);
        registriesFuture.thenAcceptAsync(consumer);
    }

    public void loadRegistryById(Function<Throwable, Registry> ex, Consumer<Registry> consumer, int id) {
        registryFuture = CompletableFuture
                .supplyAsync(() -> fletchRegistry(dao.getById(id)))
                .exceptionally(ex);
        registryFuture.thenAcceptAsync(consumer);
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
