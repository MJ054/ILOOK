package com.example.ilook.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ilook.Api.JsonPlaceHolderApi;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class Intercepter implements Interceptor {

    private Context context;
    private static JsonPlaceHolderApi jsonPlaceHolderApi;

    public Intercepter(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {

        SharedPreferences sharedPreferences = MyApp.getContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        Request newRequest = chain.request().newBuilder()
                .addHeader("accessToken", sharedPreferences.getString("accessToken", String.valueOf(MODE_PRIVATE)))
                .build();
        Response response = chain.proceed(newRequest);

        return response;
    }
}