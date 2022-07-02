package com.injent.miscalls.network;

import com.injent.miscalls.network.dto.CallDto;
import com.injent.miscalls.network.dto.RegistryDto;
import com.injent.miscalls.network.dto.TokenDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Every method returns {@link JResponse} this object contains certain data received from the server
 */
public interface MisAPI {

    String BASE_URL = "http://185.26.121.81:8080/";

    /**
     * Receiving call data from the server Requires a network connection
     * @param token a personal token derived from the user's account, required to retrieve data
     * from the server. The token is stored in
     * {@link androidx.security.crypto.EncryptedSharedPreferences}
     * @return {@link Call}<{@link List}<{@link CallDto}>> that represents received data of calls
     * from server
     */
    @POST("calls")
    Call<JResponse> patients(@Body TokenDto token);

    /**
     * @param authModel contains login and password that will be used to search the server
     * database rows
     * @return {@link JResponse}
     */
    @POST("user/auth")
    Call<JResponse> auth(@Body AuthModelOut authModel);

    @GET("calls/add")
    Call<JResponse> addPatient(@Query("name") String name, @Query("userId") int userId);

    @GET("calls/delete")
    Call<JResponse> deletePatient(@Query("id") int id);

    @POST("registry/upload")
    Call<JResponse> uploadDocument(@Body RegistryDto registry);
}
