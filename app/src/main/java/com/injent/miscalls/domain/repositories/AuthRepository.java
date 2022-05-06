package com.injent.miscalls.domain.repositories;

import android.content.SharedPreferences;

import com.injent.miscalls.domain.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.AuthModelOut;
import com.injent.miscalls.data.User;

import retrofit2.Call;

public class AuthRepository {

    public Call<User> auth(String login, String password){
        return HttpManager.getMisAPI().auth(new AuthModelOut(login, password));
    }
}
