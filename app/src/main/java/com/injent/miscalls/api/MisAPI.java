package com.injent.miscalls.api;

import com.injent.miscalls.data.AuthModelOut;
import com.injent.miscalls.data.User;
import com.injent.miscalls.data.calllist.CallInfo;
import com.injent.miscalls.data.calllist.QueryToken;
import com.injent.miscalls.data.diagnosis.RecommendationTemp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MisAPI {

    @POST("getPatients.php")
    Call<List<CallInfo>> patients(@Body QueryToken token);

    @POST("auth.php")
    Call<User> auth(@Body AuthModelOut authModel);

    @POST("getProtocols.php")
    Call<List<RecommendationTemp>> protocolTemps(@Body QueryToken token);

    @GET("protocoltemp.txt")
    Call<String> getProtocolTemp();
}
