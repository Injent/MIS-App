package com.injent.miscalls.API;

import com.injent.miscalls.data.AuthModelIn;
import com.injent.miscalls.data.AuthModelOut;
import com.injent.miscalls.data.patientlist.Patient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MisAPI {

    @GET("A59WiDdQ")
    Call<List<Patient>> patients();

    @POST("auth.php")
    Call<AuthModelIn> auth(@Body AuthModelOut authModel);
}
