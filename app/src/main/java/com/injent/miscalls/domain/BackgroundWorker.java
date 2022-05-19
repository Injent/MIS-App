package com.injent.miscalls.domain;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.Executors;

public class BackgroundWorker extends Worker {

    public static final String TAG =  "callSupplier";

    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                for (int i = 0; i < 30; i++) {
                    Log.d("TAG",i + " - current i");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
        return Result.success();
    }
}
