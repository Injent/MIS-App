package com.injent.miscalls;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.injent.miscalls.domain.BackgroundDownloader;
import com.injent.miscalls.domain.ForegroundServiceApp;
import com.injent.miscalls.ui.auth.AuthFragment;

import org.osmdroid.config.Configuration;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final long EXPIRATION_DELAY = 60000L;
    private NavController navController;

    private int safeTime;
    private Timer timer;
    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            safeTime++;
            if (safeTime <= 5 || navController.getCurrentDestination() == null) return;

            if (navController.getCurrentDestination().getId() != R.id.authFragment) {
                App.getEncryptedUserDataManager()
                        .setData(R.string.keyAuthed, false)
                        .write();

                runOnUiThread(() -> navController.navigate(R.id.authFragment));
                safeTime = 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.statusBar, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar, getTheme()));

        startWork();

        if (savedInstanceState != null && savedInstanceState.getBoolean(getString(R.string.keyOffUpdates), false)) {
            Bundle args = new Bundle();
            args.putBoolean(getString(R.string.keyOffUpdates), true);
            navController.navigate(R.id.authFragment, args);
        }

        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (navController == null)
            navController = Navigation.findNavController(this,R.id.container);
        if (timer == null) {
            timer = new Timer();
            timer.schedule(timerTask, 0L, EXPIRATION_DELAY);
        }
    }

    public void startWork() {
        if (App.getUserDataManager().getInt(R.string.keyMode) != 1) {
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

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        safeTime = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        navController = null;
    }
}