package com.injent.miscalls;

import static com.injent.miscalls.App.getUserDataManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.injent.miscalls.data.UserDataManager;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.domain.BackgroundDownloader;
import com.injent.miscalls.domain.ForegroundServiceApp;
import com.injent.miscalls.domain.repositories.AuthRepository;

import org.osmdroid.config.Configuration;

import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final long EXPIRATION_DELAY = 12000L;
    private NavController navController;

    private int safeTime;
    private Timer timer;
    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            safeTime++;
            if (safeTime <= 5 || navController.getCurrentDestination() == null) return;

            if (navController.getCurrentDestination().getId() != R.id.authFragment) {
                App.getUser().setAuthed(false);
                new AuthRepository().updateUser(App.getUser());

                runOnUiThread(() -> navController.navigate(R.id.authFragment));
                safeTime = 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        App.getInstance().initSettings();
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.statusBar, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar, getTheme()));

        startWork();
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
        if (getUserDataManager().getInt(R.string.keyMode) != 1) {
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

    @SuppressLint("QueryPermissionsNeeded")
    public void openExplorer(String path) {
        Uri selectedUri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        }
    }

    public static void hideKeyBoard(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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