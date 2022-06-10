package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.NetworkManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import retrofit2.Call;

public class HomeRepository {

    private final CallInfoDao dao;

    private CompletableFuture<CallInfo> callById;
    private CompletableFuture<List<CallInfo>> allCallsInfo;
    private CompletableFuture<Void> insertCallsWithDropTable;
    private CompletableFuture<CallInfo> insertCallInfo;

    public HomeRepository() {
        dao = App.getInstance().getCallInfoDao();
    }

    public String setNewPatientDbDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy / HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        App.getUserDataManager().setData(R.string.keyDbDate, currentDate).write();
        return currentDate;
    }

    public String getPatientDbDate() {
        return App.getUserDataManager().getString(R.string.keyDbDate);
    }

    public Call<List<CallInfo>> getPatientList(Token token) {
        return NetworkManager.getMisAPI().patients(token);
    }

    public void loadCallInfoById(Function<Throwable, CallInfo> ex, Consumer<CallInfo> consumer, int id) {
        callById = CompletableFuture
                .supplyAsync(() -> dao.getById(id))
                .exceptionally(ex);
        callById.thenAcceptAsync(consumer);
    }

    public void cancelFutures() {
        if (allCallsInfo != null) {
            allCallsInfo.cancel(true);
        }
        if (callById != null) {
            callById.cancel(true);
        }
        if (insertCallInfo != null) {
            insertCallInfo.cancel(true);
        }
        if (insertCallsWithDropTable != null) {
            insertCallsWithDropTable.cancel(true);
        }
    }

    public void loadAllCallsInfo(Function<Throwable, List<CallInfo>> ex, Consumer<List<CallInfo>> consumer) {
        allCallsInfo = CompletableFuture
                .supplyAsync(dao::getAll)
                .exceptionally(ex);
        allCallsInfo.thenAcceptAsync(consumer);
    }

    public void insertCallsWithDropTable(Function<Throwable,Void> ex, List<CallInfo> list) {
        insertCallsWithDropTable = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    dao.clearAll();
                    dao.insertAll(list);
                    return null;
                })
                .exceptionally(ex);
    }

    public void insertCall(Function<Throwable, CallInfo> ex, CallInfo callInfo) {
        insertCallInfo = CompletableFuture
                .supplyAsync((Supplier<CallInfo>) () -> {
                    dao.insert(callInfo);
                    return null;
                })
                .exceptionally(ex);
    }
}
