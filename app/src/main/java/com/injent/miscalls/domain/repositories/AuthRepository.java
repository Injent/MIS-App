package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.user.Organization;
import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.data.database.user.UserDao;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.AuthModelOut;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.network.JResponse;
import com.injent.miscalls.ui.auth.AuthFragment;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import retrofit2.Call;

public class AuthRepository {

    private final UserDao dao;

    private CompletableFuture<Boolean> insertUserFuture;
    private CompletableFuture<User> findActiveSession;
    private CompletableFuture<Void> updateUser;
    private CompletableFuture<User> authFromDb;

    public AuthRepository() {
        dao = AppDatabase.getUserDao();
    }

    public void cancelFutures() {
        if (insertUserFuture != null) {
            insertUserFuture.cancel(true);
        }
        if (findActiveSession != null) {
            findActiveSession.cancel(true);
        }
        if (updateUser != null) {
            updateUser.cancel(true);
        }
        if (authFromDb != null) {
            authFromDb.cancel(true);
        }
    }

    public Call<JResponse> auth(String login, String password){
        return NetworkManager.getMisAPI().auth(new AuthModelOut(login, password));
    }

    public void insertUser(Function<Throwable, Boolean> ex, Consumer<Boolean> consumer, User user) {
        insertUserFuture = CompletableFuture
                .supplyAsync(() -> {
                    int orgId = (int) dao.insertOrganization(user.getOrganization());
                    int tokenId = (int) dao.insertToken(user.getToken());
                    user.setTokenId(tokenId);
                    user.setOrganizationId(orgId);
                    user.setAuthed(true);
                    dao.insertUser(user);
                    return true;
                })
        .exceptionally(ex);
        insertUserFuture.thenAcceptAsync(consumer);
    }

    public void updateUser(User user) {
        updateUser = new CompletableFuture<>()
                .supplyAsync(() -> {
                    dao.updateUser(user);
                    return null;
                });
    }

    public void findActiveSession(Function<Throwable, User> ex, Consumer<User> consumer) {
        findActiveSession = CompletableFuture
                .supplyAsync(() -> {
                    User user = dao.getCurrentUser(1);
                    if (user == null) {
                        return null;
                    }
                    user.setToken(dao.getTokenById(user.getId()));
                    user.setOrganization(dao.getOrganizationById(user.getOrganizationId()));
                    return user;
                })
        .exceptionally(ex);
        findActiveSession.thenAcceptAsync(consumer);
    }

    public void authFromDb(Function<Throwable, User> ex, Consumer<User> consumer, String login, String password) {
        authFromDb = CompletableFuture
                .supplyAsync(() -> dao.getByLoginAndPassword(login, password))
                .exceptionally(ex);
        authFromDb.thenAcceptAsync(consumer);
    }
}
