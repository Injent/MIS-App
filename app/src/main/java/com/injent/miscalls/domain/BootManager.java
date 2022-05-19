package com.injent.miscalls.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.injent.miscalls.App;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //App.getInstance().startService();
        //App.getInstance().startWork();
    }
}
