package com.injent.miscalls.domain.repositories;

import android.content.SharedPreferences;

import com.injent.miscalls.R;
import com.injent.miscalls.domain.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.calllist.CallInfoDao;
import com.injent.miscalls.data.calllist.QueryToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import retrofit2.Call;

public class HomeRepository {

    private final CallInfoDao dao;
    private final ExecutorService es;

    public HomeRepository() {
        dao = App.getInstance().getPatientDao();
        es = Executors.newSingleThreadExecutor();
    }

    public String setNewPatientDbDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        SharedPreferences sp = App.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(App.getInstance().getString(R.string.keyDbDate),currentDate);
        editor.apply();
        return currentDate;
    }

    public String getPatientDbDate() {
        return App.getInstance().getSharedPreferences().getString(App.getInstance().getString(R.string.keyDbDate), "");
    }

    public Call<List<CallInfo>> getPatientList(QueryToken token) {
        return HttpManager.getMisAPI().patients(token);
    }

    public void deleteAll() {
        es.submit(() -> App.getInstance().getCallDatabase().clearAllTables());
    }

    public void getCallById(Function<Throwable, CallInfo> ex, Consumer<CallInfo> consumer, int id) {
        CompletableFuture<CallInfo> future = CompletableFuture
                .supplyAsync(() -> dao.getById(id), es)
                .exceptionally(ex);
        future.thenAcceptAsync(consumer);
    }

    public void insertPatient(CallInfo callInfo) {
        es.submit(() -> dao.insert(callInfo));
    }

    public void getAll(Function<Throwable, List<CallInfo>> ex, Consumer<List<CallInfo>> consumer) {
        CompletableFuture<List<CallInfo>> future = CompletableFuture
                .supplyAsync(dao::getAll, es)
                .exceptionally(ex);

        future.thenAcceptAsync(consumer);
    }

    public void insertWithDropDb(CallInfo... callInfoList) {
        es.submit(() -> {
            App.getInstance().getCallDatabase().clearAllTables();
            for (CallInfo callInfo : callInfoList) {
                dao.insert(callInfo);
            }
        });
    }
}
