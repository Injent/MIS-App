package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.App;
import com.injent.miscalls.data.database.user.Organization;
import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.data.database.user.UserDao;
import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.AuthModelOut;
import com.injent.miscalls.data.database.user.User;
import com.injent.miscalls.network.JResponse;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;

public class AuthRepository {

    private final UserDao dao;

    private CompletableFuture<Void> insertUserFuture;

    public AuthRepository() {
        dao = App.getInstance().getUserDao();
    }

    public void cancelFutures() {
        if (insertUserFuture != null) {
            insertUserFuture.cancel(true);
        }
    }

    public Call<JResponse> auth(String login, String password){
        return NetworkManager.getMisAPI().auth(new AuthModelOut(login, password));
    }

    public void insertUser(User user) {
        insertUserFuture = CompletableFuture
                .supplyAsync(() -> {
                    Token token = user.getToken();
                    Organization organization = user.getOrganization();
                    dao.insertUser(user);
                    dao.insertOrganization(organization);
                    dao.insertToken(token);
                    return null;
                });
    }
}
