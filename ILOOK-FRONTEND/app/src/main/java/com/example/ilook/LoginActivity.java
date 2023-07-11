package com.example.ilook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ilook.Model.AccessToken;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.databinding.ActivityLoginBinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ilook.Model.MyApp.getContext;
import static com.example.ilook.Util.CommonUtils.BearerRemove;

public class LoginActivity extends AppCompatActivity{

    private ActivityLoginBinding binding;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);

        SharedPreferences auto = getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
        String authorization = auto.getString("accessToken", null);

        //refresh 토큰 헤더에 받지 말것! jwt로 자동로그인 구현?
        /*if(authorization !=null){
            loginAccessToken(authorization);
        }*/

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> user = new HashMap<>();
                    user.put("id", binding.loginEmail.getText().toString());
                    user.put("password", binding.loginPassword.getText().toString());

                    binding.textView24.setVisibility(View.GONE);
                    binding.textView24.setVisibility(View.GONE);

                    Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().login(user);
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                System.out.println("로그인 성공");
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("accessToken", BearerRemove(response.headers().get("accessToken")));
                                editor.putString("refreshToken", BearerRemove(response.headers().get("refreshToken")));

                                //로그인 유지상태가 check 됐을 때
                                if(binding.loginCheck.isChecked()){
                                    editor.putString("check", "1");
                                }
                                editor.commit();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                System.out.println("로그인 실패");
                                try {
                                    ObjectMapper mapper = new ObjectMapper();
                                    String json = response.errorBody().string();
                                    Map map = mapper.readValue(json, Map.class);
                                    System.out.println(map);

                                    if(map.get("data") != null){
                                        Map map1 = (Map) map.get("data");
                                        if(map1.get("id") != null){
                                            binding.textView6.setVisibility(View.VISIBLE);
                                            binding.textView6.setText(map1.get("id").toString());
                                        }
                                        if(map1.get("password") != null){
                                            binding.textView24.setVisibility(View.VISIBLE);
                                            binding.textView24.setText(map1.get("password").toString());
                                        }
                                    }

                                    if(map.get("message") != null){
                                        System.out.println(map.get("message").toString());
                                        Toast.makeText(getContext(),"잘못된 아이디 또는 비밀번호입니다.", Toast.LENGTH_SHORT).show();                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            System.out.println("에러");
                        }
                    });
                }
            });



        binding.loginFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindActivity.class);
                startActivity(intent);
            }
        });

        binding.registerBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EmailAuthActivity1.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1111){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        }
    }

    private void loginAccessToken(String authorization) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().loginAccessToken(new AccessToken(authorization));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("로그인 성공");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
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
