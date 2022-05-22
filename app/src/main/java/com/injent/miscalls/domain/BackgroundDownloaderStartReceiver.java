package com.injent.miscalls.domain;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.injent.miscalls.App;

import java.util.concurrent.TimeUnit;

public class BackgroundDownloaderStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (App.getUserSettings().getMode() != 1) {
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .build();

        PeriodicWorkRequest backgroundWorkRequest = new PeriodicWorkRequest.Builder(BackgroundDownloader.class,60, TimeUnit.MINUTES)
                .addTag(BackgroundDownloader.TAG)
                .setConstraints(constraints)
                .build();

        Intent service = new Intent(context, ForegroundServiceApp.class);
        ((Activity) context).startForegroundService(service);

        WorkManager workManager = WorkManager.getInstance(context);

        workManager.enqueueUniquePeriodicWork(BackgroundDownloader.TAG, ExistingPeriodicWorkPolicy.KEEP, backgroundWorkRequest);
    }
}
