package com.injent.miscalls.domain.repositories;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.media.DeniedByServerException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.medcall.MedCall;
import com.injent.miscalls.data.database.medcall.MedCallDao;
import com.injent.miscalls.data.database.medcall.Geo;
import com.injent.miscalls.data.database.medcall.GeoDao;
import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.util.NetworkManager;
import com.injent.miscalls.network.dto.CallDto;
import com.injent.miscalls.network.dto.TokenDto;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallRepository {

    private final MedCallDao medCallDao;
    private final GeoDao geoDao;

    private MutableLiveData<List<MedCall>> calls;
    private MutableLiveData<Throwable> error;

    private CompletableFuture<MedCall> callById;
    private CompletableFuture<List<MedCall>> loadCalls;
    private CompletableFuture<Void> insertCallsWithDropTable;
    private CompletableFuture<MedCall> insertCall;

    public CallRepository() {
        medCallDao = AppDatabase.getCallInfoDao();
        geoDao = AppDatabase.getGeoDao();
        calls = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public void loadCallInfoById(Function<Throwable, MedCall> ex, Consumer<MedCall> consumer, int id) {
        callById = CompletableFuture
                .supplyAsync(() -> {
                    MedCall medCall = medCallDao.getById(id);
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
                    List<MedCall> list = medCallDao.getCallByUserId(userId);
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
                        if (medCallDao.getBySnils(item.getSnils()) == null) {
                            long callId = medCallDao.insertCall(item);
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
                    medCallDao.updateCall(medCall);
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
            return;
        }
        NetworkManager.getMisAPI().patients(TokenDto.toDto(token)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        return;
                    }
                    List<MedCall> callList = response.body().getCalls().stream().map(CallDto::toDomainObject).collect(Collectors.toList());
                    if (callList.isEmpty()) {
                        error.postValue(new ArrayStoreException());
                        return;
                    }
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

    public void downloadCallListInBackground(Context context, Token token){
        if (!NetworkManager.isInternetAvailable(context)) {
            return;
        }
        NetworkManager.getMisAPI().patients(TokenDto.toDto(token)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        return;
                    }
                    List<MedCall> callList = response.body().getCalls().stream().map(CallDto::toDomainObject).collect(Collectors.toList());
                    if (callList.isEmpty()) {
                        return;
                    }
                    insertCallsWithDropTable(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    }, callList);
                }
                Log.e("TAG", "onResponse: " + "All WORKS!" );
        }

            @Override
            public void onFailure(@NonNull Call<JResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public LiveData<Throwable> getErrorLiveData() {
        return error;
    }
}
