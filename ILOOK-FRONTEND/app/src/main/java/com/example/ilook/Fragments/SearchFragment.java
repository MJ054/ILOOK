package com.example.ilook.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ilook.Adapter.CommentAdapter;
import com.example.ilook.Adapter.FollowAdatpter;
import com.example.ilook.MainActivity;
import com.example.ilook.Model.AccessToken;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Model.Follow;
import com.example.ilook.R;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.databinding.ActivityLoginBinding;
import com.example.ilook.databinding.FragmentFindPwBinding;
import com.example.ilook.databinding.FragmentSearchBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private ArrayList<Follow> followArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.searchRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        FollowAdatpter adatpter = new FollowAdatpter(followArrayList, getContext());
        binding.searchRecyclerview.setAdapter(adatpter);

        //검색버튼 클릭
        binding.searchButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = binding.editTextTextPersonName3.toString();
                getSearch(search);
            }
        });

        return view;
    }

    private void getSearch(String search) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().getSearch(search);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("검색 성공");
                    System.out.println(response.body().getData().toString());
                } else {
                    System.out.println("검색 실패");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
            }
        });
    }
}