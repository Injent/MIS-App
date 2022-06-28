package com.injent.miscalls.ui.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.util.AppWorker;
import com.injent.miscalls.domain.repositories.AuthRepository;

import java.util.concurrent.TimeUnit;

/**
 * A {@link ViewModel} for {@link MainActivity} and
 * {@link com.injent.miscalls.ui.loading.LoadingScreenFragment}.
 *
 * Checks the activity of the session and performs background tasks.
 */
public class MainViewModel extends ViewModel {

    private WorkManager workManager;

    private AuthRepository repository;

    private LiveData<User> activeUser;
    private LiveData<WorkInfo> workInfo;

    public void init(Context context) {
        repository = new AuthRepository(context);
        activeUser = repository.getActiveSession();
        workManager = WorkManager.getInstance(context);
    }

    public LiveData<WorkInfo> getWorkInfo() {
        return workInfo;
    }

    public void startWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .build();

        PeriodicWorkRequest backgroundWorkRequest = new PeriodicWorkRequest.Builder(AppWorker.class,15, TimeUnit.MINUTES)
                .addTag(AppWorker.TAG)
                .setConstraints(constraints)
                .build();

        workInfo = workManager.getWorkInfoByIdLiveData(backgroundWorkRequest.getId());

        workManager.enqueueUniquePeriodicWork(AppWorker.TAG, ExistingPeriodicWorkPolicy.KEEP, backgroundWorkRequest);
    }

    public void cancelWork() {
        workManager.cancelAllWork();
    }

    public LiveData<User> getActiveSession() {
        return activeUser;
    }

    public void loginActiveSession() {
        if (App.getUser() != null) return;
        repository.loginActiveSession();
    }

    @Override
    public void onCleared() {
        repository.clear();
        activeUser = null;

        super.onCleared();
    }
}
