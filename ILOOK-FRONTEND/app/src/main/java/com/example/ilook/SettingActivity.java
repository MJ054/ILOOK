package com.example.ilook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Api.JsonPlaceHolderApi;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivitySettingBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private ActivitySettingBinding binding;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar9);

        RetrofitClient retrofitClient = RetrofitClient.getInstance(getApplicationContext());
        jsonPlaceHolderApi = retrofitClient.getJsonPlaceHolderApi();

        binding.textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("accessToken");
                editor.remove("refreshToken");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.textViewDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ApiResponse> call = jsonPlaceHolderApi.userDelete();
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            System.out.println("회원탈퇴 성공");

                            Toast myToast = Toast.makeText(getApplicationContext(),"회원이 탈퇴되었습니다", Toast.LENGTH_SHORT);
                            myToast.show();
                            SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("authorization");
                            editor.remove("refreshToken");
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            System.out.println("회원탈퇴 실패");
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
            }
        });
        }


    //툴바 뒤로가기 클릭
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back 키를 눌렸을 때 동작작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}


