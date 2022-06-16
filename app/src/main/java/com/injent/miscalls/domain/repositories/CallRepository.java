package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.calls.Geo;
import com.injent.miscalls.data.database.calls.GeoDao;
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

public class CallRepository {

    private final CallInfoDao callDao;
    private final GeoDao geoDao;

    private CompletableFuture<CallInfo> callById;
    private CompletableFuture<List<CallInfo>> allCallsInfo;
    private CompletableFuture<Void> insertCallsWithDropTable;
    private CompletableFuture<CallInfo> insertCallInfo;

    public CallRepository() {
        callDao = AppDatabase.getCallInfoDao();
        geoDao = AppDatabase.getGeoDao();
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

    public Call<JResponse> getPatientList(Token token) {
        return NetworkManager.getMisAPI().patients(token);
    }

    public void loadCallInfoById(Function<Throwable, CallInfo> ex, Consumer<CallInfo> consumer, int id) {
        callById = CompletableFuture
                .supplyAsync(() -> {
                    CallInfo call = callDao.getById(id);
                    call.setGeo(geoDao.getGeoByCallId(id));
                    return call;
                })
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

    public void loadAllCallsInfo(Function<Throwable, List<CallInfo>> ex, Consumer<List<CallInfo>> consumer, int userId) {
        allCallsInfo = CompletableFuture
                .supplyAsync(() -> {
                    List<CallInfo> list = callDao.getCallByUserId(userId);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setGeo(geoDao.getGeoByCallId(userId));
                    }
                    return list;
                })
                .exceptionally(ex);
        allCallsInfo.thenAcceptAsync(consumer);
    }

    public void insertCallsWithDropTable(Function<Throwable,Void> ex, List<CallInfo> list) {
        insertCallsWithDropTable = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    for (CallInfo item : list) {
                        if (callDao.getBySnils(item.getSnils()) == null) {
                            long callId = callDao.insertCall(item);
                            Geo geo = item.getGeo();
                            geo.setCallId((int) callId);
                            geoDao.insertGeo(geo);
                        }
                    }
                    return null;
                })
                .exceptionally(ex);
    }

    public void updateCall(Function<Throwable, CallInfo> ex, CallInfo callInfo) {
        insertCallInfo = CompletableFuture
                .supplyAsync((Supplier<CallInfo>) () -> {
                    callDao.updateCall(callInfo);
                    return null;
                })
                .exceptionally(ex);
    }
}
