package com.injent.miscalls.API;

import com.injent.miscalls.data.AuthModelOut;
import com.injent.miscalls.data.User;
import com.injent.miscalls.data.patientlist.Patient;
import com.injent.miscalls.data.patientlist.QueryPatients;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MisAPI {

    @POST("getPatients.php")
    Call<List<Patient>> patients(@Body QueryPatients token);

    @POST("auth.php")
    Call<User> auth(@Body AuthModelOut authModel);
}
