package com.mksoft.imageload;


import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {




// --- NETWORK INJECTION ---

    private static String BASE_URL = "http://116.44.253.75:8888";

    @Provides
    Gson provideGson() { return new GsonBuilder().create(); }

    @Provides
    Retrofit provideRetrofit(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    APIService provideApiWebservice(Retrofit restAdapter) {
        return restAdapter.create(APIService.class);
    }


    // --- REPOSITORY INJECTION ---
    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }


    @Provides
    @Singleton
    APIRepo provideAPIRepository(APIService webservice) {
        return new APIRepo(webservice);
    }


}
