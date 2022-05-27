package com.injent.miscalls.network;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.recommendation.Recommendation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MisAPI {

    String BASE_URL = "https://exitoso.ru/injent/";

    /**
     * Receiving call data from the server Requires a network connection
     * @param token a personal token derived from the user's account, required to retrieve data
     * from the server. The token is stored in
     * {@link androidx.security.crypto.EncryptedSharedPreferences}
     * @return {@link Call}<{@link List}<{@link CallInfo}>> that represents received data of calls
     * from server
     */
    @POST("getPatients.php")
    Call<List<CallInfo>> patients(@Body QueryToken token);

    /**
     *
     * @param authModel contains
     * @return
     */
    @POST("auth.php")
    Call<User> auth(@Body AuthModelOut authModel);
}
