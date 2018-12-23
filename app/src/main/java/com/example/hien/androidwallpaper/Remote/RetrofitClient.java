package com.example.hien.androidwallpaper.Remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hien on 30/11/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofitClient;

    public static Retrofit getClient(String baseURL){
        if(retrofitClient == null){
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofitClient = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofitClient;
    }
}
