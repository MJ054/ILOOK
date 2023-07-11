package com.example.ilook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ilook.Adapter.FollowAdatpter;
import com.example.ilook.Model.Follow;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityFollowBinding;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowActivity extends AppCompatActivity {

    private ActivityFollowBinding binding;
    private SharedPreferences preferences;
    private FollowAdatpter adatpter;
    private ArrayList<Follow> followArrayList;
private int profileUser;
    String followerCnt;
    String followedCnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityFollowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar10);

        Intent intent= getIntent();
         profileUser = intent.getIntExtra("유저번호", 0);
         followerCnt = intent.getStringExtra("팔로워수");
         followedCnt = intent.getStringExtra("팔로우수");


        followArrayList = new ArrayList<>();

        binding.recyclerViewFollow.setLayoutManager(new LinearLayoutManager(this));
        //adatpter = new FollowAdatpter(followArrayList, getApplication());
        //binding.recyclerViewFollow.setAdapter(adatpter);

        getFollower();

        binding.textView6Follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFollower();
            }
        });

        binding.textView14Followed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFollowed();
            }
        });
    }

    private void getFollower()
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().followList(profileUser);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");

                    followArrayList = new ArrayList<>();
                    binding.textView6Follower.setTextColor(Color.parseColor("#6E16EC"));
                    binding.textView14Followed.setTextColor(Color.parseColor("#000000"));
                    binding.textView22.setText("총 "+followerCnt+"팔로워");

                    profile((List<Map>) response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                t.printStackTrace();
                System.out.println("에러");
            }
        });

    }

    private void getFollowed()
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().followingList(profileUser);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");

                    followArrayList = new ArrayList<>();
                    binding.textView6Follower.setTextColor(Color.parseColor("#000000"));
                    binding.textView14Followed.setTextColor(Color.parseColor("#6E16EC"));
                    binding.textView22.setText("총 "+followedCnt+"팔로우");
                    profile((List<Map>) response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
    }

    private void profile(List<Map> list)
    {
        String image;
        String nickname;
        int userIdx;
        URL url = null;

        for(int i = 0; i<list.size();i++) {

            image = (String)list.get(i).get("profile_image");
            nickname = (String)list.get(i).get("nickname");
            userIdx = (int)Math.round((Double) list.get(i).get("user_idx"));

            try {
                url = new URL(RetrofitClient.BASE_URL + "pictures?url="+image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            followArrayList.add(new Follow(url, nickname,userIdx));
        }
        adatpter = new FollowAdatpter(followArrayList, getApplication());
        binding.recyclerViewFollow.setAdapter(adatpter);
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



