package com.mksoft.imageload;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class APIRepo {
    private static int FRESH_TIMEOUT_IN_MINUTES = 8;

    private final APIService webservice;

    @Inject
    public APIRepo(APIService webservice) {
        Log.d("testResultRepo", "make it!!!");

        this.webservice = webservice;

    }
    public void sendFile(MultipartBody.Part   fbody){
        Call<String> call = webservice.sendFile(fbody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Log.d("test0602", response.body());
                }

                Log.d("test0602","fail 응답");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("test0602",t.toString());

            }
        });

    }


}
