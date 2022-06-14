package com.injent.miscalls.domain;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.Executors;

public class BackgroundDownloader extends Worker {

    public static final String TAG =  "callSupplier";

    public BackgroundDownloader(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Executors.newSingleThreadExecutor().execute(this::downloadDatabase);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return Result.failure();
        }
        return Result.success();
    }

    public void downloadDatabase() {
//        HomeRepository repository = new HomeRepository();
//        repository.getPatientList(App.getUser().getToken()).enqueue(new Callback<>() {
//            @Override
//            public void onResponse(@NonNull Call<List<CallInfo>> call, @NonNull Response<List<CallInfo>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    repository.insertCallsWithDropTable(throwable -> null, response.body());
//                    repository.setNewPatientDbDate();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<CallInfo>> call, @NonNull Throwable t) {
//                // Nothing to do
//            }
//        });
    }
}
