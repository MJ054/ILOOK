package com.example.ilook.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ilook.ChangePwActivity;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Model.Email;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.databinding.FragmentFindPwBinding;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import com.example.ilook.RegisterActivity;

public class FindPwFragment extends Fragment{

    private FragmentFindPwBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentFindPwBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        binding.editTextTextEmailAddress3Pw.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                binding.textView26Check.setText("0");
                binding.hiddenTextViewEmail.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        binding.pwEmailAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().find(new Email(binding.editTextTextEmailAddress3Pw.getText().toString()));
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        ApiResponse apiResponse = response.body();

                        if (response.isSuccessful()) {
                            binding.textView26Check.setText("1");
                            binding.hiddenTextViewEmail.setVisibility(view.VISIBLE);
                            binding.hiddenTextViewEmail.setTextColor(Color.parseColor("#000000"));
                            binding.hiddenTextViewEmail.setText("인증번호가 전송되었습니다");
                        } else {
                            try {
                                binding.hiddenTextViewEmail.setVisibility(view.VISIBLE);

                                ObjectMapper mapper = new ObjectMapper();
                                String json = response.errorBody().string();
                                System.out.println(json);
                                Map map = mapper.readValue(json, Map.class);

                                if ((String) map.get("message") == null) {
                                    Map map1 = (Map) map.get("data");
                                    binding.hiddenTextViewEmail.setText((String) map1.get("email"));
                                } else {
                                    binding.hiddenTextViewEmail.setText((String) map.get("message"));
                                }
                                binding.hiddenTextViewEmail.setTextColor(Color.parseColor("#e61919"));
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

        binding.nextPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.textView26Check.getText().toString().equals("0")){
                    binding.hiddenTextViewEmail.setVisibility(view.VISIBLE);
                    binding.hiddenTextViewEmail.setText("이메일을 인증하여 주세요");
                    binding.hiddenTextViewEmail.setTextColor(Color.parseColor("#e61919"));
                    return;
                }

                Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().findPwd(new Email(binding.editTextTextEmailAddress3Pw.getText().toString()),binding.editTextTextPersonName.getText().toString(),binding.textViewCode.getText().toString());
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (!response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());

                                if(((String)jsonObject.get("message")).equals("인증번호가 일치하지 않습니다")){
                                    binding.hiddenEmailCode.setVisibility(View.VISIBLE);
                                    binding.hiddenEmailCode.setText((String)jsonObject.get("message"));
                                    binding.hiddenEmailCode.setTextColor(Color.parseColor("#e61919"));
                                }else{
                                    binding.hiddenEmailCode.setVisibility(View.GONE);
                                }

                                if(((String)jsonObject.get("message")).equals("등록된 사용자가 없습니다")){
                                    Toast.makeText(getContext(),"등록된 사용자가 없습니다", Toast.LENGTH_SHORT).show();
                                }

                            } catch (IOException| JSONException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                            Intent intent = new Intent(getContext(), ChangePwActivity.class);
                            intent.putExtra("아이디",binding.editTextTextPersonName.getText().toString());
                            startActivity(intent);

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
            }
        });

        return view;
    }


}
