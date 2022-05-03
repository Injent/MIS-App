package com.injent.miscalls.api;

import com.injent.miscalls.data.AuthModelOut;
import com.injent.miscalls.data.User;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.QueryToken;
import com.injent.miscalls.data.templates.ProtocolTemp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MisAPI {

    @POST("getPatients.php")
    Call<List<Patient>> patients(@Body QueryToken token);

    @POST("auth.php")
    Call<User> auth(@Body AuthModelOut authModel);

    @POST("getProtocols.php")
    Call<List<ProtocolTemp>> protocolTemps(@Body QueryToken token);

    @GET("protocoltemp.txt")
    Call<String> getProtocolTemp();
}