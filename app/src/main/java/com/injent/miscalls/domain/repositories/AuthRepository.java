package com.injent.miscalls.domain.repositories;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.media.DeniedByServerException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.data.database.user.UserDao;
import com.injent.miscalls.network.AuthModelOut;
import com.injent.miscalls.network.AuthorizationException;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.dto.UserDto;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private Context context;

    private final UserDao dao;

    private MutableLiveData<User> activeSession;
    private MutableLiveData<User> activeUser;
    private MutableLiveData<Throwable> error;

    private CompletableFuture<Void> insertUser;
    private CompletableFuture<User> findActiveSession;
    private CompletableFuture<Void> updateUser;
    private CompletableFuture<User> authFromDb;
    private CompletableFuture<User> authWithBiometric;

    public AuthRepository(Context context) {
        dao = AppDatabase.getUserDao();
        activeUser = new MutableLiveData<>();
        activeSession = new MutableLiveData<>();
        error = new MutableLiveData<>();
        this.context = context;
    }

    public void clear() {
        if (insertUser != null)
            insertUser.cancel(true);
        if (findActiveSession != null)
            findActiveSession.cancel(true);
        if (updateUser != null)
            updateUser.cancel(true);
        if (authFromDb != null)
            authFromDb.cancel(true);
        if (authWithBiometric != null)
            authWithBiometric.cancel(true);
        activeUser = null;
        activeSession = null;
        error = null;
        context = null;
    }

    public LiveData<User> getActiveUser() {
        return activeUser;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void authWithBiometric() {
        authWithBiometric = CompletableFuture
                .supplyAsync(dao::getByLastActive)
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    return null;
                });
        authWithBiometric.thenAcceptAsync(user -> {
            if (user != null) {
                activeUser.postValue(user);
                authFromDb(user.getLogin(), user.getPassword());
            }
        });
    }

    public void insertUser(User user) {
        insertUser = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    User lastUser = dao.getByLastActive();
                    if (lastUser != null) {
                        lastUser.setLastActive(false);
                        dao.updateUser(lastUser);
                    }
                    int orgId = (int) dao.insertOrganization(user.getOrganization());
                    int tokenId = (int) dao.insertToken(user.getToken());
                    user.setTokenId(tokenId);
                    user.setOrganizationId(orgId);

                    dao.insertUser(user);
                    return null;
                })
        .exceptionally(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        });
    }

    public void updateUser(User user) {
        updateUser = CompletableFuture
                .supplyAsync((Supplier<Void>) () -> {
                    dao.updateUser(user);
                    return null;
                })
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    throwable.printStackTrace();
                    return null;
                });
    }

    public LiveData<User> getActiveSession() {
        return activeSession;
    }

    public void loginActiveSession() {
        findActiveSession = CompletableFuture
                .supplyAsync(() -> {
                    User user = dao.getCurrentUser(1);
                    if (user == null) {
                        return null;
                    }
                    user.setToken(dao.getTokenById(user.getTokenId()));
                    user.setOrganization(dao.getOrganizationById(user.getOrganizationId()));
                    user.setAuthed(true);
                    user.setLastActive(true);
                    App.setUser(user);
                    return user;
                })
        .exceptionally(throwable -> {
            error.postValue(throwable);
            throwable.printStackTrace();
            return null;
        });

        findActiveSession.thenAcceptAsync(user -> {
            if (user != null) {
                user.setAuthed(true);
                App.setUser(user);
                updateUser(user);
                activeSession.postValue(user);
            } else {
                activeSession.postValue(null);
            }
        });
    }

    public void authFromServer(String login, String password) {
        if (!NetworkManager.isInternetAvailable(context)) {
            error.postValue(new NetworkErrorException());
            return;
        }
        NetworkManager.getMisAPI().auth(new AuthModelOut(login, password)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JResponse> call, @NonNull Response<JResponse> response) {
                if (response.body() == null || !response.isSuccessful()) {
                    error.postValue(new DeniedByServerException("Wrong body"));
                    return;
                }
                if (response.body().getUser() != null) {
                    User user = UserDto.toDomainObject(response.body().getUser());
                    user.setLogin(login);
                    user.setPassword(password);
                    user.setAuthed(true);
                    user.setLastActive(true);
                    user.setDbUpdateTime(new Date());
                    App.setUser(user);
                    insertUser(user);
                    activeUser.postValue(user);
                } else {
                    error.postValue(new AuthorizationException(response.body().getMessage()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JResponse> call, @NonNull Throwable t) {
                error.postValue(t);
                t.printStackTrace();
            }
        });
    }


    public void authFromDb(String login, String password) {
        authFromDb = CompletableFuture
                .supplyAsync(() -> {
                    // Clear last active user tag
                    User lastUser = dao.getByLastActive();
                    if (lastUser != null) {
                        lastUser.setLastActive(false);
                        dao.updateUser(lastUser);
                    }

                    return dao.getByLoginAndPassword(login, password);
                })
                .exceptionally(throwable -> {
                    error.postValue(throwable);
                    return null;
                });

        authFromDb.thenAcceptAsync(user -> {
            if (user != null) {
                user.setAuthed(true);
                user.setLastActive(true);
                user.setOrganization(dao.getOrganizationById(user.getOrganizationId()));
                user.setToken(dao.getTokenById(user.getTokenId()));
                App.setUser(user);
                updateUser(user);
                activeUser.postValue(user);
            } else {
                authFromServer(login, password);
            }
        });
    }
}
