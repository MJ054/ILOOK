package com.example.ilook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Api.JsonPlaceHolderApi;
import com.example.ilook.Model.Register;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityRegisterBinding;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.INVISIBLE;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar);

        Button id_btn = (Button) findViewById(R.id.check_id_btn);
        Button nickname_btn = (Button) findViewById(R.id.check_nickname_btn);
        Button register_btn = (Button) findViewById(R.id.register_btn);
        EditText id_editText = (EditText) findViewById(R.id.editText_id);
        EditText pwd_editText = (EditText) findViewById(R.id.editText_pwd);
        EditText pwd2_editText = (EditText) findViewById(R.id.editText_pwd2);
        EditText nickname_editText = (EditText) findViewById(R.id.editText_nickname);

        Intent myIntent = getIntent();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


        binding.editTextId.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                binding.idDupNum.setText("0");
                binding.hiddenId.setVisibility(INVISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editTextNickname.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                binding.nicknameDupNum.setText("0");
                binding.hiddenNickname.setVisibility(INVISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Button.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.check_id_btn:
                        Call<ApiResponse> call = jsonPlaceHolderApi.checkIdDuplication(id_editText.getText().toString());
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (!response.isSuccessful()) {
                                    try {
                                        ObjectMapper mapper = new ObjectMapper();
                                        String json = response.errorBody().string();
                                        Map map = mapper.readValue(json, Map.class);
                                        System.out.println(map);
                                        binding.hiddenId.setVisibility(View.VISIBLE);
                                        binding.hiddenId.setText((String) map.get("message"));
                                        binding.hiddenId.setTextColor(Color.parseColor("#e61919"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }

                                binding.hiddenId.setVisibility(View.VISIBLE);
                                binding.hiddenId.setTextColor(Color.parseColor("#000000"));
                                binding.hiddenId.setText(response.body().getData().toString());
                                binding.idDupNum.setText("1");
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                System.out.println("실패");
                            }
                        });
                        break;
                    case R.id.check_nickname_btn:
                        Call<ApiResponse> call2 = jsonPlaceHolderApi.checkNicknameDuplication(nickname_editText.getText().toString());
                        call2.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (!response.isSuccessful()) {
                                    try {
                                        //System.out.println(response.errorBody().string());
                                        ObjectMapper mapper = new ObjectMapper();
                                        String json = response.errorBody().string();
                                        Map map = mapper.readValue(json, Map.class);

                                        binding.hiddenNickname.setVisibility(View.VISIBLE);
                                        binding.hiddenNickname.setText((String) map.get("message"));
                                        binding.hiddenNickname.setTextColor(Color.parseColor("#e61919"));

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    return;
                                }
                                binding.hiddenNickname.setVisibility(View.VISIBLE);
                                binding.hiddenNickname.setTextColor(Color.parseColor("#000000"));
                                binding.hiddenNickname.setText(response.body().getData().toString());
                                binding.nicknameDupNum.setText("1");
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                System.out.println("실패");
                            }
                        });
                        break;

                    case R.id.register_btn:
                        if (binding.idDupNum.getText().toString().equals("1") && binding.nicknameDupNum.getText().toString().equals("1")) {
                            Call<ApiResponse> call3 = jsonPlaceHolderApi.saveUser(new Register(id_editText.getText().toString(), myIntent.getStringExtra("email"),
                                    pwd_editText.getText().toString(), pwd2_editText.getText().toString(), nickname_editText.getText().toString()));
                            call3.enqueue(new Callback<ApiResponse>() {
                                @Override
                                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                    if (!response.isSuccessful()) {
                                        try {

                                            ObjectMapper mapper = new ObjectMapper();
                                            String json = response.errorBody().string();
                                            Map map = mapper.readValue(json, Map.class);
                                            Map map1 =(Map) map.get("data");

                                            if (map1.get("password") != null) {
                                                String password = (String) map1.get("password");
                                                binding.hiddenPwd.setVisibility(View.VISIBLE);
                                                binding.hiddenPwd.setText(password);
                                                binding.hiddenPwd.setTextColor(Color.parseColor("#e61919"));
                                            }else{
                                                binding.hiddenPwd.setVisibility(INVISIBLE);
                                            }

                                            if (map1.get("password2") != null) {
                                                String password2 = (String) map1.get("password2");
                                                binding.hiddenPwd2.setVisibility(View.VISIBLE);
                                                binding.hiddenPwd2.setText(password2);
                                                binding.hiddenPwd2.setTextColor(Color.parseColor("#e61919"));
                                            }else{
                                                binding.hiddenPwd2.setVisibility(INVISIBLE);
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return;
                                    }
                                    binding.hiddenPwd.setVisibility(INVISIBLE);
                                    binding.hiddenPwd2.setVisibility(INVISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    System.out.println("가입완료");
                                }

                                @Override
                                public void onFailure(Call<ApiResponse> call, Throwable t) {
                                    System.out.println("실패");
                                }
                            });
                            break;
                        }else{
                            if(binding.idDupNum.getText().toString().equals("0")){
                                binding.hiddenId.setVisibility(View.VISIBLE);
                                binding.hiddenId.setText("아이디 중복확인을 해주세요");
                                binding.hiddenId.setTextColor(Color.parseColor("#e61919"));
                            }

                            if(binding.nicknameDupNum.getText().toString().equals("0")){
                                binding.hiddenNickname.setVisibility(View.VISIBLE);
                                binding.hiddenNickname.setText("닉네임 중복확인을 해주세요");
                                binding.hiddenNickname.setTextColor(Color.parseColor("#e61919"));
                            }
                            return;
                        }

                }
            }
        };

        id_btn.setOnClickListener(onClickListener);
        nickname_btn.setOnClickListener(onClickListener);
        register_btn.setOnClickListener(onClickListener);
    }

        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){

            switch (item.getItemId()) {
                case android.R.id.home: { //toolbar의 back 키를 눌렸을 때 동작
                    finish();
                    return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }
}
