package com.example.ilook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.ilook.Adapter.PostImageSliderAdapter;
import com.example.ilook.Adapter.PostProductListAdapter;
import com.example.ilook.Model.PostProduct;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityPostBinding;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostActivity extends AppCompatActivity {

    private ActivityPostBinding binding;
    ArrayList<URL> dataArrayList = new ArrayList<>();
    PostImageSliderAdapter adapter;
    Double likeExsist;
    Double followExsist;
    ArrayList<PostProduct> postProducts = new ArrayList<>();
    PostProductListAdapter postProductListAdapter;
    RecyclerView recyclerView;
    int userIdx;
    int postIdx;
    Double identification;
    String category;
    Map map;
    int like_cnt;
    int result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent secondIntent = getIntent();
        postIdx = secondIntent.getIntExtra("게시물번호", 0);
        category = secondIntent.getStringExtra("카테고리");
        result = secondIntent.getIntExtra("결과", 0);
        //binding
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar8);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        binding.sliderViewPagerPostImage.setOffscreenPageLimit(1);
        binding.sliderViewPagerPostImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });


        //좋아요
        binding.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likes(postIdx);
            }
        });

        //좋아요 취소
        binding.imageViewLike2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imageViewLike2.setVisibility(View.INVISIBLE);
                binding.imageViewLike.setVisibility(View.VISIBLE);
                likeDelete(postIdx);
                like_cnt = like_cnt - 1;
                binding.textViewLikesCnt.setText(like_cnt + " likes");
            }
        });

        //팔로우
        binding.buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                follow();
            }
        });

        //언팔로우
        binding.buttonFollow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.buttonFollow.setVisibility(View.VISIBLE);
                binding.buttonFollow2.setVisibility(View.INVISIBLE);
                followDelete();
            }
        });

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("프로필유저", userIdx);
                startActivity(intent);
            }
        });

        binding.imageViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("게시글번호", postIdx);
                startActivity(intent);
            }
        });

    }

    //게시물 정보
    private void getPostDetail(int a, Menu menu) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().getPost1(a);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("성공");
                    map = (Map) response.body().getData();
                    System.out.println(map);

                    like_cnt = (int) (double) map.get("like_count");

                    binding.textViewLikesCnt.setText((int) (double) map.get("like_count") + " likes");
                    binding.textView2Content.setText((String) map.get("content"));
                    binding.textViewNickname2.setText((String) map.get("nickname"));
                    binding.textViewDate.setText((String) map.get("create_date"));

                    if (((Double) map.get("advertise")) == 1.0) {
                        binding.advertise.setVisibility(View.VISIBLE);
                    }

                    userIdx = (int) (double) map.get("user_user_idx");

                    SharedPreferences auto = getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
                    String accessToken = auto.getString("accessToken", null);
                    if (accessToken != null) {
                        likeExsist = (Double) map.get("like_exsist");
                        followExsist = (Double) map.get("follow_exsist");
                        identification = (Double) map.get("identification");


                        if ((Double) map.get("like_exsist") == 1.0) {
                            // 좋아요 존재
                            binding.imageViewLike2.setVisibility(View.VISIBLE);
                            binding.imageViewLike.setVisibility(View.INVISIBLE);
                        }
                        if (followExsist == 1.0) {
                            // 팔로우 존재
                            binding.buttonFollow.setVisibility(View.INVISIBLE);
                            binding.buttonFollow2.setVisibility(View.VISIBLE);
                        }
                        if (identification == 1.0) {
                            // 게시물 작성자 == 보는 사람
                            getMenuInflater().inflate(R.menu.post, menu);
                            binding.buttonFollow.setVisibility(View.INVISIBLE);
                            binding.buttonFollow2.setVisibility(View.INVISIBLE);
                        }

                    }
                    parseResult2((String) map.get("profile_image"));
                    ArrayList images = (ArrayList) map.get("image");
                    setupIndicators(images.size());
                    parseResult(images);

                    if (category.equals("OOTD")) {
                        ArrayList products = (ArrayList) map.get("product");
                        for (int i = 0; i < products.size(); i++) {
                            Map a = (Map) products.get(i);
                            System.out.println(a);
                            postProducts.add(new PostProduct((String) a.get("category"), (String) a.get("brand"), (String) a.get("name"), (String) a.get("size")));
                        }
                        postProductListAdapter = new PostProductListAdapter(postProducts);
                        recyclerView.setAdapter(postProductListAdapter);

                    } else if (category.equals("PICK")) {
                        binding.textView3.setText((String) map.get("deadline"));
                        binding.recyclerProducts.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
    }

    //AppBar 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getPostDetail(postIdx, menu);
        return true;
    }

    //AppBar 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                deletePost();
                return true;
            case android.R.id.home: //toolbar의 back 키를 눌렸을 때 동작
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //게시물 삭제
    private void deletePost() {

        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().deletePost(postIdx);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("삭제 성공");
                    Toast.makeText(getApplication(), "게시물이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(result);
                    finish();
                } else {
                    System.out.println("삭제 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("삭제 노노");
            }
        });
    }

    //사진 인디케이터
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            binding.layoutIndicators.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = binding.layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.layoutIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }

    private void parseResult(ArrayList array) {
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.size(); i++) {

                        Map a = (Map) array.get(i);
                        String b = (String) a.get("path");
                        b = b.replace("\\", "%2F");

                        URL url = new URL(RetrofitClient.BASE_URL + "pictures?url=" + b);
                        dataArrayList.add(url);
                    }
                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
        };
        uThread.start(); // 작업 Thread 실행

        try {
            uThread.join();

            adapter = new PostImageSliderAdapter(getApplicationContext(), dataArrayList);
            binding.sliderViewPagerPostImage.setAdapter(adapter);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseResult2(String a) {
        final String[] b = {a};
        b[0] = b[0].replace("\\", "%2F");

        URL url = null;
        try {
            url = new URL(RetrofitClient.BASE_URL + "pictures?url=" + b[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Glide.with(getApplicationContext())
                .load(url)
                .into(binding.imageProfile);
    }


    //좋아요
    public void likes(int a){
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().postLikes(a);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");
                    binding.imageViewLike2.setVisibility(View.VISIBLE);
                    binding.imageViewLike.setVisibility(View.INVISIBLE);
                    like_cnt = like_cnt+1;
                    binding.textViewLikesCnt.setText(like_cnt + " likes");
                }else{
                    System.out.println("실패");
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

    //좋아요 취소
    private void likeDelete(int a) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().postDeleteLike(a);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");

                }else{
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                System.out.println("에러");
            }
        });
    }

    //팔로우
    public void follow(){
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().follow(userIdx);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");
                    binding.buttonFollow.setVisibility(View.INVISIBLE);
                    binding.buttonFollow2.setVisibility(View.VISIBLE);
                }else{
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

    //팔로우 취소
    private void followDelete() {

        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().followDelete(userIdx);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");

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

}