package com.injent.miscalls.domain;

import com.injent.miscalls.API.HttpManager;
import com.injent.miscalls.data.AuthModelIn;
import com.injent.miscalls.data.AuthModelOut;
import com.injent.miscalls.data.User;

import retrofit2.Call;

public class AuthRepository {

    public Call<User> auth(String login, String password){
        //TODO
        //Всегда будет входить при любых условиях
        //Потому что пользователь всегда найден и
        //логика должна осуществлсяться на сервере
        return HttpManager.getMisAPI().auth(new AuthModelOut(login, password));
    }
}
