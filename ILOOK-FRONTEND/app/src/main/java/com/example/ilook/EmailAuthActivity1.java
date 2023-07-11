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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Model.Email;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityEmailAuthBinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ilook.Model.MyApp.getContext;

public class EmailAuthActivity1 extends AppCompatActivity {

    String preText;
    String preText2;

    private ActivityEmailAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityEmailAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar7);


        EditText authCode = (EditText) findViewById(R.id.auth_code);
        TextView message2 = (TextView)findViewById(R.id.email_auth_message2);
        Button nextButton = (Button) findViewById(R.id.next_btn);

        Gson gson = new Gson();
        //서버와 연결

        //이메일이 변경될 경우(재확인 받아야하니까)
        binding.authEmail.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                binding.emailAuthNum.setText("0");
                binding.emailAuthMessage1.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


        binding.checkEmailBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().mailAuth(new Email(binding.authEmail.getText().toString()));
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        ApiResponse apiResponse= response.body();
                        binding.emailAuthMessage1.setVisibility(view.VISIBLE);
                        if (response.isSuccessful()) {
                            binding.emailAuthMessage1.setText((String) apiResponse.getData());
                            binding.emailAuthMessage1.setTextColor(Color.parseColor("#000000"));
                            binding.emailAuthNum.setText("1");
                        } else {
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                String json = response.errorBody().string();
                                System.out.println(json);
                                Map map = mapper.readValue(json, Map.class);

                                if((String)map.get("message") == null){
                                    Map map1 = (Map) map.get("data");
                                    binding.emailAuthMessage1.setText((String) map1.get("email"));
                                }else {
                                    binding.emailAuthMessage1.setText((String) map.get("message"));
                                }
                                binding.emailAuthMessage1.setTextColor(Color.parseColor("#e61919"));
                                } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
            }
        });

        nextButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.emailAuthNum.getText().toString().equals("0")){
                    binding.emailAuthMessage1.setVisibility(view.VISIBLE);
                    binding.emailAuthMessage1.setText("이메일을 인증하여 주세요");
                    binding.emailAuthMessage1.setTextColor(Color.parseColor("#e61919"));
                    return;
                }

                Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().checkAuth(new Email(binding.authEmail.getText().toString()),authCode.getText().toString());
                call.enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (!response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                message2.setVisibility(view.VISIBLE);
                                message2.setText((String)jsonObject.get("message"));
                                message2.setTextColor(Color.parseColor("#e61919"));
                            } catch (IOException|JSONException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        intent.putExtra("email",binding.authEmail.getText().toString());
                        startActivity(intent);
                    }


                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        System.out.println(t.getMessage());
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





