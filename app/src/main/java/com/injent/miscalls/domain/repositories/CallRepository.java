package com.injent.miscalls.domain.repositories;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.media.DeniedByServerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.injent.miscalls.App;
import com.injent.miscalls.R;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.calls.MedCall;
import com.injent.miscalls.data.database.calls.CallDao;
import com.injent.miscalls.data.database.calls.Geo;
import com.injent.miscalls.data.database.calls.GeoDao;
import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.rest.dto.CallDto;
import com.injent.miscalls.network.rest.dto.TokenDto;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallRepository {

    private final CallDao callDao;
    private final GeoDao geoDao;

    private MutableLiveData<List<MedCall>> calls;
    private MutableLiveData<Throwable> error;

    private CompletableFuture<MedCall> callById;
    private CompletableFuture<List<MedCall>> loadCalls;
    private CompletableFuture<Void> insertCallsWithDropTable;
    private CompletableFuture<MedCall> insertCall;

    public CallRepository() {
        callDao = AppDatabase.getCallInfoDao();
        geoDao = AppDatabase.getGeoDao();
        calls = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public void loadCallInfoById(Function<Throwable, MedCall> ex, Consumer<MedCall> consumer, int id) {
        callById = CompletableFuture
                .supplyAsync(() -> {
                    MedCall medCall = callDao.getById(id);
                    medCall.setGeo(geoDao.getGeoByCallId(id));
                    return medCall;
                })
                .exceptionally(ex);
        callById.thenAcceptAsync(consumer);
    }

    public void clear() {
        if (loadCalls != null) {
            loadCalls.cancel(true);
        }
        if (callById != null) {
            callById.cancel(true);
        }
        if (insertCall != null) {
            insertCall.cancel(true);
        }
        if (insertCallsWithDropTable != null) {
            insertCallsWithDropTable.cancel(true);
        }
        calls = null;
        error = null;
    }

    public void loadAllCallsInfo(int userId) {
        loadCalls = CompletableFuture
                .supplyAsync(() -> {
                    List<MedCall> list = callDao.getCallByUserId(userId);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setGeo(geoDao.getGeoByCallId(userId));
                    }
                    return list;
                })
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    return Collections.emptyList();
                });
        loadCalls.thenAcceptAsync(medCalls -> calls.postValue(medCalls));
    }

    public void insertCallsWithDropTable(Function<Throwable,Void> ex, List<MedCall> list) {
        insertCallsWithDropTable = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    for (MedCall item : list) {
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

    public void updateCall(Function<Throwable, MedCall> ex, MedCall medCall) {
        insertCall = CompletableFuture
                .supplyAsync((Supplier<MedCall>) () -> {
                    callDao.updateCall(medCall);
                    return null;
                })
                .exceptionally(ex);
    }

    public LiveData<List<MedCall>> getCallsLiveData() {
        return calls;
    }

    public void downloadCallList(Context context, Token token){
        if (!NetworkManager.isInternetAvailable(context)) {
            error.postValue(new NetworkErrorException());
        }
        NetworkManager.getMisAPI().patients(TokenDto.toDto(token)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        return;
                    }
                    List<MedCall> callList = response.body().getCalls().stream().map(CallDto::toDomainObject).collect(Collectors.toList());
                    insertCallsWithDropTable(throwable -> {
                        error.postValue(throwable);
                        throwable.printStackTrace();
                        return null;
                    }, callList);
                    calls.postValue(callList);
                } else {
                    error.postValue(new DeniedByServerException("Wrong response"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JResponse> call, @NonNull Throwable t) {
                error.postValue(t);
                t.printStackTrace();
            }
        });
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }
}
