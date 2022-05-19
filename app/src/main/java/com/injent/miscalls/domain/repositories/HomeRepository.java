package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.QueryToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import retrofit2.Call;

public class HomeRepository {

    private final CallInfoDao dao;

    public HomeRepository() {
        dao = App.getInstance().getCallInfoDao();
    }

    public String setNewPatientDbDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        App.getUserSettings().setDbDate(currentDate).write();
        return currentDate;
    }

    public String getPatientDbDate() {
        return App.getUserSettings().getDbDate();
    }

    public Call<List<CallInfo>> getPatientList(QueryToken token) {
        return NetworkManager.getMisAPI().patients(token);
    }

    private CompletableFuture<CallInfo> callInfoFuture;

    public void getCallById(Function<Throwable, CallInfo> ex, Consumer<CallInfo> consumer, int id) {
        callInfoFuture = CompletableFuture
                .supplyAsync(() -> dao.getById(id))
                .exceptionally(ex);
        callInfoFuture.thenAcceptAsync(consumer);
    }

    private CompletableFuture<List<CallInfo>> callInfoListFuture;

    public void cancelFutures() {
        if (callInfoListFuture != null) {
            callInfoListFuture.cancel(true);
        }
        if (callInfoFuture != null) {
            callInfoFuture.cancel(true);
        }
    }

    public void getAll(Function<Throwable, List<CallInfo>> ex, Consumer<List<CallInfo>> consumer) {
        callInfoListFuture = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        callInfoListFuture.thenAcceptAsync(consumer);
    }

    public void insertCallsWithDropTable(Function<Throwable,Void> ex, List<CallInfo> list) {
        CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    dao.insertAll(list);
                    return null;
                })
                .exceptionally(ex);
    }
}
