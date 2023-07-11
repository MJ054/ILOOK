package com.example.ilook.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Model.Email;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.databinding.FragmentFindIdBinding;
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

public class FindIDFragment extends Fragment {

        private FragmentFindIdBinding binding;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            binding = FragmentFindIdBinding.inflate(inflater, container, false);
            View view = binding.getRoot();


            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            binding.editTextTextEmailAddress3.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    binding.emailAuthNumFragment.setText("0");
                    binding.hiddenIdTextViewEmail.setVisibility(View.GONE);
                }
                @Override
                public void afterTextChanged(Editable editable) {
                }
            });


            binding.idEmailAuthBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().find(new Email(binding.editTextTextEmailAddress3.getText().toString()));
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            ApiResponse apiResponse = response.body();

                            if (response.isSuccessful()) {
                                binding.emailAuthNumFragment.setText("1");
                                binding.hiddenIdTextViewEmail.setVisibility(view.VISIBLE);
                                binding.hiddenIdTextViewEmail.setTextColor(Color.parseColor("#000000"));
                                binding.hiddenIdTextViewEmail.setText("인증번호가 전송되었습니다");
                            } else {
                                try {
                                    binding.hiddenIdTextViewEmail.setVisibility(view.VISIBLE);

                                    ObjectMapper mapper = new ObjectMapper();
                                    String json = response.errorBody().string();
                                    System.out.println(json);
                                    Map map = mapper.readValue(json, Map.class);

                                    if ((String) map.get("message") == null) {
                                        Map map1 = (Map) map.get("data");
                                        binding.hiddenIdTextViewEmail.setText((String) map1.get("email"));
                                    } else {
                                        binding.hiddenIdTextViewEmail.setText((String) map.get("message"));
                                    }
                                    binding.hiddenIdTextViewEmail.setTextColor(Color.parseColor("#e61919"));
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

            binding.nextId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(binding.emailAuthNumFragment.getText().toString().equals("0")){
                        binding.hiddenIdTextViewEmail.setVisibility(view.VISIBLE);
                        binding.hiddenIdTextViewEmail.setText("이메일을 인증하여 주세요");
                        binding.hiddenIdTextViewEmail.setTextColor(Color.parseColor("#e61919"));
                        return;
                    }

                    Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().checkCode(new Email(binding.editTextTextEmailAddress3.getText().toString()),binding.textViewCode.getText().toString());
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (!response.isSuccessful()) {
                                try {
                                    System.out.println(response);
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    System.out.println(jsonObject);
                                    binding.hiddenIdCode.setVisibility(view.VISIBLE);
                                    System.out.println("kkkkkk");
                                    binding.hiddenIdCode.setText((String)jsonObject.get("message"));
                                    binding.hiddenIdCode.setTextColor(Color.parseColor("#e61919"));
                                } catch (IOException| JSONException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                           /* System.out.println(response.body().getData());
                            Intent intent = new Intent(getContext(), ActivityGetId.class);
                            intent.putExtra("id",response.body().getData().toString());
                            startActivity(intent);*/

                            //팝업창 띄우기
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("아이디 찾기");
                            builder.setMessage("회원님의 아이디는 "+response.body().getData().toString()+" 입니다.");

                            // Yes 버튼 및 이벤트 생성
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Pass
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
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
