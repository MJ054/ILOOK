package com.example.ilook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Model.PasswordRequest;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityChangePwBinding;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ilook.Model.MyApp.getContext;

public class ChangePwActivity  extends AppCompatActivity {

    private ActivityChangePwBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePwBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String id = intent.getStringExtra("아이디");

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar4);

        binding.nextPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().changePwd(id, new PasswordRequest(
                        binding.editTextTextPassword4.getText().toString(), binding.editTextTextPassword5.getText().toString()
                ));
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            System.out.println("비밀번호 변경 성공");
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            String json = null;
                            Map map = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                json = response.errorBody().string();
                                 map = mapper.readValue(json, Map.class);
                                System.out.println(map);
                                Map map2 = (Map)map.get("data");
                                if(map2.get("password") !=null){
                                    binding.textView26.setVisibility(View.VISIBLE);
                                    binding.textView26.setText((String)map2.get("password"));
                                    binding.textView26.setTextColor(Color.parseColor("#e61919"));

                                }else{
                                    binding.textView26.setVisibility(View.GONE);
                                }

                                if(map2.get("password2") != null){
                                    binding.textView27.setVisibility(View.VISIBLE);
                                    binding.textView27.setText((String)map2.get("password2"));
                                    binding.textView27.setTextColor(Color.parseColor("#e61919"));

                                }else{
                                    binding.textView27.setVisibility(View.GONE);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("비밀번호 변경 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
            }
        });
    }

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

