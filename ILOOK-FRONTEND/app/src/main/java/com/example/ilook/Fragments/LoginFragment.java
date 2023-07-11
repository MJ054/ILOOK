package com.example.ilook.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ilook.EmailAuthActivity1;
import com.example.ilook.FindActivity;
import com.example.ilook.MainActivity;
import com.example.ilook.Model.AccessToken;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.databinding.ActivityLoginBinding;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ilook.Util.CommonUtils.BearerRemove;

public class LoginFragment  extends Fragment {

    private ActivityLoginBinding binding;
    private SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        preferences = getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);

        // 자동로그인 처리
        SharedPreferences auto = getActivity().getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
        String authorization = auto.getString("accessToken", null);
        System.out.println(authorization);
        //refresh 토큰 헤더에 받지 말것! jwt로 자동로그인 구현?
        if(authorization !=null){
            loginAccessToken(authorization);
        }

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> user = new HashMap<>();
                user.put("id", binding.loginEmail.getText().toString());
                user.put("password", binding.loginPassword.getText().toString());
                login(user);
            }
        });

        binding.loginFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), FindActivity.class);
                startActivity(intent);
            }
        });

        binding.registerBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EmailAuthActivity1.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void login(Map user) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().login(user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("로그인 성공");

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("accessToken", BearerRemove(response.headers().get("accessToken")));
                    editor.putString("refreshToken", BearerRemove(response.headers().get("refreshToken")));
                    System.out.println("accessToken" + BearerRemove(response.headers().get("accessToken")));
                    System.out.println(getActivity().getSharedPreferences("UserInfo", Activity.MODE_PRIVATE).getString("accessToken", null));
                    //로그인 유지상태가 check 됐을 때
                    if(binding.loginCheck.isChecked()){
                        editor.putString("check", "1");
                    }
                    editor.commit();

                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    System.out.println("로그인 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }


    private void loginAccessToken(String authorization) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().loginAccessToken(new AccessToken(authorization));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("로그인 성공");
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    System.out.println("로그인 실패");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
            }
        });
    }

}


