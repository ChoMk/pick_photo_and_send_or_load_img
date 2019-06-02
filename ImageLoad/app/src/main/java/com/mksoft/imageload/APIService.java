package com.mksoft.imageload;

import android.service.autofill.UserData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {
    @Multipart
    @POST("/files/upload")
    Call<String> sendFile (
            @Part MultipartBody.Part file );
}
