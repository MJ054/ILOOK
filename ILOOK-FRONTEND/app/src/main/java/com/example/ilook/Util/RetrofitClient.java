package com.example.ilook.Util;

import android.content.Context;

import com.example.ilook.Api.JsonPlaceHolderApi;
import com.example.ilook.Model.Intercepter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient{
    private static RetrofitClient instance = null;
    private static JsonPlaceHolderApi jsonPlaceHolderApi;
    public final static String BASE_URL = "http://192.168.43.209:8080/";
    //public final static String BASE_URL = "http://10.0.2.2:8080/";
    //public final static String BASE_URL = "http://192.168.0.7:8080/";
    //public final static String BASE_URL = "http://15.165.111.170:3000/";
    private Context context;

    private RetrofitClient(Context context){

        this.context = context;

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Intercepter(context)).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    public static RetrofitClient getInstance(Context context){
        if(instance == null){
            instance = new RetrofitClient(context);
        }
        return instance;
    }

    public JsonPlaceHolderApi getJsonPlaceHolderApi(){
        return jsonPlaceHolderApi;
    }
}
