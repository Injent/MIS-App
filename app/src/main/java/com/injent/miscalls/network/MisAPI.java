package com.injent.miscalls.network;

import com.injent.miscalls.data.database.user.Token;
import com.injent.miscalls.data.database.calls.CallInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Every method returns {@link JResponse} this object contains certain data received from the server
 */
public interface MisAPI {

    String BASE_URL = "https://mis-calls.herokuapp.com/";

    /**
     * Receiving call data from the server Requires a network connection
     * @param token a personal token derived from the user's account, required to retrieve data
     * from the server. The token is stored in
     * {@link androidx.security.crypto.EncryptedSharedPreferences}
     * @return {@link Call}<{@link List}<{@link CallInfo}>> that represents received data of calls
     * from server
     */
    @POST("calls")
    Call<List<CallInfo>> patients(@Body Token token);

    /**
     * @param authModel contains login and password that will be used to search the server
     * database rows
     * @return {@link JResponse}
     */
    @POST("user/auth")
    Call<JResponse> auth(@Body AuthModelOut authModel);

    /**
     * @param token you only need to specify the value of the token in the object to check it's
     * validation, the rest is irrelevant
     * @return {@link JResponse}
     */
    @POST("token/expiration")
    Call<JResponse> checkTokenExpiration(Token token);
}
