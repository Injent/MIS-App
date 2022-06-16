package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.calls.UserActiveUpdate;
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
    private CompletableFuture<User> authWithBiometric;
    private CompletableFuture<Void> resetLastActive;

    public AuthRepository() {
        dao = AppDatabase.getUserDao();
    }

    public void cancelFutures() {
        if (insertUserFuture != null)
            insertUserFuture.cancel(true);
        if (findActiveSession != null)
            findActiveSession.cancel(true);
        if (updateUser != null)
            updateUser.cancel(true);
        if (authFromDb != null)
            authFromDb.cancel(true);
        if (authWithBiometric != null)
            authWithBiometric.cancel(true);
        if (resetLastActive != null) {
            resetLastActive.cancel(true);
        }
    }

    public void authWithBiometric(Function<Throwable, User> ex, Consumer<User> consumer) {
        authWithBiometric = CompletableFuture
                .supplyAsync(dao::getByLastActive)
                .exceptionally(ex);
        authWithBiometric.thenAcceptAsync(consumer);
    }

    public Call<JResponse> auth(String login, String password){
        return NetworkManager.getMisAPI().auth(new AuthModelOut(login, password));
    }

    public void insertUser(Function<Throwable, Boolean> ex, Consumer<Boolean> consumer, User user) {
        insertUserFuture = CompletableFuture
                .supplyAsync(() -> {
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
                    return true;
                })
        .exceptionally(ex);
        insertUserFuture.thenAcceptAsync(consumer);
    }

    public void updateUser(User user) {
        updateUser = CompletableFuture
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
                .supplyAsync(() -> {
                    User lastUser = dao.getByLastActive();
                    if (lastUser != null) {
                        lastUser.setLastActive(false);
                        dao.updateUser(lastUser);
                    }
                    User user = dao.getByLoginAndPassword(login, password);
                    if (user == null) return null;
                    user.setAuthed(true);
                    user.setLastActive(true);
                    user.setOrganization(dao.getOrganizationById(user.getOrganizationId()));
                    user.setToken(dao.getTokenById(user.getTokenId()));
                    return user;
                })
                .exceptionally(ex);
        authFromDb.thenAcceptAsync(consumer);
    }
}
