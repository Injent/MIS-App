package com.injent.miscalls.domain.repositories;

import android.content.SharedPreferences;

import com.injent.miscalls.data.HttpManager;
import com.injent.miscalls.App;
import com.injent.miscalls.data.AuthModelOut;
import com.injent.miscalls.data.User;

import retrofit2.Call;

public class AuthRepository {

    public Call<User> auth(String login, String password){
        return HttpManager.getMisAPI().auth(new AuthModelOut(login, password));
    }

    public void setAuthed(boolean authed) {
        App.getInstance().setAuthed(authed);
        SharedPreferences sp = App.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("authed", authed).apply();
    }

    public boolean isAuthed() {
        return App.getInstance().isAuthed();
    }
}
