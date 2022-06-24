package com.injent.miscalls.ui.main;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.injent.miscalls.App.getUserDataManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.util.ForegroundServiceApp;
import com.injent.miscalls.domain.repositories.AuthRepository;
import com.injent.miscalls.util.CustomOnBackPressedFragment;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final int READ_EXTERNAL_STORAGE_REQUEST = 0x1045;

    private long backPressTime;
    private Toast exitToast;
    public static final long EXPIRATION_DELAY = 12000L;
    private NavController navController;
    private MainViewModel viewModel;

    private int safeTime;
    private Timer timer;
    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            safeTime++;
            if (safeTime <= 5 || navController.getCurrentDestination() == null) return;

            if (navController.getCurrentDestination().getId() != R.id.authFragment) {
                App.getUser().setAuthed(false);
                new AuthRepository(getApplicationContext()).updateUser(App.getUser());

                runOnUiThread(() -> navController.navigate(R.id.authFragment));
                safeTime = 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getInstance().initData();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(getBaseContext());
        viewModel.getActiveSession().observe(this, user -> {
            if (timer == null) {
                timer = new Timer();
                timer.schedule(timerTask, 0L, EXPIRATION_DELAY);
            }
            clear();
        });
        startWork();

        viewModel.loginActiveSession();
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.statusBar, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar, getTheme()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        navController = Navigation.findNavController(this, R.id.container);
        requestPermission();
    }

    public void startWork() {
        if (getUserDataManager().getInt(R.string.keyMode) != 1) {
            cancelWork();
            return;
        }

        viewModel.getWorkInfo().observe(this, workInfo -> {
            WorkInfo.State state = workInfo.getState();
            Log.e("TAG", "startWork: " + state.name() );
            Intent service = new Intent(getApplicationContext(), ForegroundServiceApp.class);
            startForegroundService(service);
//            if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.BLOCKED) {
//
//            }
        });
    }

    public void cancelWork() {
        viewModel.cancelWork();
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
    public void onBackPressed() {
        // Override custom onBackPressed in fragments
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (navHostFragment == null || navController.getCurrentDestination() == null) return;

        Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (!(fragment instanceof CustomOnBackPressedFragment)) {
            super.onBackPressed();
        } else if (((CustomOnBackPressedFragment) fragment).onBackPressed()) {
            super.onBackPressed();
        } else {
            // Confirm exit toast
            int currentDestinationId = navController.getCurrentDestination().getId();
            if (currentDestinationId == R.id.homeFragment || currentDestinationId == R.id.authFragment) {
                if (backPressTime + 2000 > System.currentTimeMillis()) {
                    exitToast.cancel();
                    closeApp();
                    return;
                } else {
                    exitToast = Toast.makeText(getBaseContext(), R.string.exit, Toast.LENGTH_SHORT);
                    exitToast.show();
                }
                backPressTime = System.currentTimeMillis();
            }
        }
    }

    private boolean haveStoragePermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (!haveStoragePermission()) {
            String[] permission = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permission, READ_EXTERNAL_STORAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST: {
                if (grantResults.length != 0 && grantResults[0] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "SUCCESSFUL", Toast.LENGTH_LONG).show();
                } else {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    );

                    if (showRationale) {
                        Toast.makeText(this, "SHOW NO ACCESS", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "GO TO SETTINGS", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    public void closeApp() {
        finish();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
        navController = null;
        viewModel.onCleared();
    }

    private void clear() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}