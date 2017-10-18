package com.suma.coffeeshopmvpassignment.networking;

import android.Manifest;
import android.support.annotation.RequiresPermission;

import com.google.gson.GsonBuilder;
import com.suma.coffeeshopmvpassignment.constants.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//singleton retrofit client for handling api call
public class RetrofitClient {
    private static volatile Retrofit sRetrofit = null;
    private static RequestInterface retrofitService;

    public RetrofitClient() {
    }

    public static RequestInterface getApiService() {
        return initRetrofitService();
    }

    private static RequestInterface initRetrofitService() {
        if (retrofitService == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitService == null) {
                    retrofitService = getRetrofit().create(RequestInterface.class);
                }
            }
        }
        return retrofitService;
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    private synchronized static Retrofit getRetrofit() {
        if (sRetrofit == null) {
            synchronized (RetrofitClient.class) {
                if (sRetrofit == null) {
                    sRetrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .client(createClient())
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                            .build();
                }
            }
        }
        return sRetrofit;
    }

    private static OkHttpClient createClient() {
        return new Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}