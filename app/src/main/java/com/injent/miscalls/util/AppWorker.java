package com.injent.miscalls.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.domain.repositories.CallRepository;

public class AppWorker extends Worker {

    private Context context;
    public static final String TAG =  "BACKGROUND_WORKER";

    public AppWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        downloadDatabase();
        context = null;
        return Result.success();
    }

    public void downloadDatabase() {
        new CallRepository().downloadCallListInBackground(context, new Token("0",0,2));
    }
}
