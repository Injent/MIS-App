package com.injent.miscalls.ui.main;

import static com.injent.miscalls.data.UserDataManager.MODE_REGULAR_UPDATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.domain.repositories.AuthRepository;
import com.injent.miscalls.util.CustomOnBackPressedFragment;
import com.injent.miscalls.domain.ForegroundServiceApp;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

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
        getWindow().setNavigationBarColor(getResources().getColor(R.color.statusBar, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar, getTheme()));

        // Init
        App.getInstance().initData();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(getBaseContext());
        startWork(App.getUserDataManager().getInt(R.string.keyMode));
        // Find active session to login
        viewModel.loginActiveSession();
        setContentView(R.layout.activity_main);
        setListeners();
    }

    private void setListeners() {
        viewModel.getActiveSession().observe(this, user -> {
            if (timer == null) {
                timer = new Timer();
                timer.schedule(timerTask, 0L, EXPIRATION_DELAY);
            }
            clear();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        navController = Navigation.findNavController(this, R.id.container);
    }

    public void startWork(int mode) {
        if (mode != MODE_REGULAR_UPDATE) {
            cancelWork();
            return;
        }

        viewModel.startWork();

        viewModel.getWorkInfo().observe(this, workInfo -> {
            WorkInfo.State state = workInfo.getState();
            Log.e("TAG", "startWork: " + state.name() );
            Intent service = new Intent(getApplicationContext(), ForegroundServiceApp.class);
            startForegroundService(service);
        });
    }

    public void cancelWork() {
        viewModel.cancelWork();
        Intent service = new Intent(getApplicationContext(), ForegroundServiceApp.class);
        stopService(service);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openExplorer(String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        } else {
            Uri selectedUri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");

            if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                startActivity(intent);
            }
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

        List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments();
        Fragment fragment = null;
        for (Fragment item : fragments) {
            if (!item.isHidden()) {
                fragment = item;
                break;
            }
        }
        int currentDestinationId = navController.getCurrentDestination().getId();
        if (!(fragment instanceof CustomOnBackPressedFragment)) {
            super.onBackPressed();
        } else if (((CustomOnBackPressedFragment) fragment).onBackPressed()) {
            super.onBackPressed();
        } else if (currentDestinationId == R.id.homeFragment || currentDestinationId == R.id.authFragment) {
            // Confirm exit toast
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