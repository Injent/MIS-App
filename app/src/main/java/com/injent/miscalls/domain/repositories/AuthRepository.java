package com.injent.miscalls.domain.repositories;

import com.injent.miscalls.network.NetworkManager;
import com.injent.miscalls.network.AuthModelOut;
import com.injent.miscalls.data.User;

import retrofit2.Call;

public class AuthRepository {

    public Call<User> auth(String login, String password){
        return NetworkManager.getMisAPI().auth(new AuthModelOut(login, password));
    }
}
