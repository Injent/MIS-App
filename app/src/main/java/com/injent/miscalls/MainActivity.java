package com.injent.miscalls;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.injent.miscalls.domain.BackgroundDownloader;
import com.injent.miscalls.domain.ForegroundServiceApp;
import com.injent.miscalls.ui.auth.AuthFragment;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.statusBar, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar, getTheme()));

        startWork();

        if (savedInstanceState != null && savedInstanceState.getBoolean(getString(R.string.keyOffUpdates), false)) {
            AuthFragment authFragment = new AuthFragment();
            Bundle args = new Bundle();
            args.putBoolean(getString(R.string.keyOffUpdates), true);
            authFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,authFragment)
                    .commit();
        }
    }

    public void startWork() {
        if (App.getUserSettings().getMode() != 1) {
            cancelWork();
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

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        workManager.getWorkInfoByIdLiveData(backgroundWorkRequest.getId())
                .observe(this, workInfo -> {
                    WorkInfo.State state = workInfo.getState();

                    if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        Log.i("MainActivity","Database downloaded!");
                    }
                    if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.BLOCKED) {
                        Intent service = new Intent(getApplicationContext(), ForegroundServiceApp.class);
                        startForegroundService(service);
                    }
                });

        workManager.enqueueUniquePeriodicWork(BackgroundDownloader.TAG, ExistingPeriodicWorkPolicy.KEEP, backgroundWorkRequest);
    }

    public void cancelWork() {
        WorkManager.getInstance(getApplicationContext()).cancelAllWork();
        Intent service = new Intent(getApplicationContext(), ForegroundServiceApp.class);
        stopService(service);
    }
}