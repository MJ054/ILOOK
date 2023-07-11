package com.example.ilook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ilook.Adapter.PostImageSliderAdapter;
import com.example.ilook.Adapter.PostProductListAdapter;
import com.example.ilook.Model.PostProduct;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityPickBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ilook.Model.MyApp.getContext;

public class PickActivity extends AppCompatActivity {

    private ActivityPickBinding binding;
    ArrayList<URL> dataArrayList = new ArrayList<>();
    PostImageSliderAdapter adapter;
    Double likeExsist;
    Double followExsist;
    ArrayList<PostProduct> postProducts = new ArrayList<>();
    PostProductListAdapter postProductListAdapter;
    int userIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityPickBinding.inflate(getLayoutInflater());
        //binding.getRoot()를 해야 적용됨....(오류해결)
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar8_pick);

        Intent secondIntent = getIntent();
        int a = secondIntent.getIntExtra("게시물번호", 0);
        System.out.println("게시물번호" + a);

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.

        binding.sliderViewPagerPostImagePick.setOffscreenPageLimit(1);
        binding.sliderViewPagerPostImagePick.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });


        binding.imageViewLikePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imageViewLike2Pick.setVisibility(View.VISIBLE);
                binding.imageViewLikePick.setVisibility(View.INVISIBLE);
                likes(a);
            }
        });

        binding.imageViewLike2Pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imageViewLike2Pick.setVisibility(View.INVISIBLE);
                binding.imageViewLikePick.setVisibility(View.VISIBLE);
                likeDelete(a);
            }
        });

        binding.buttonFollowPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.buttonFollowPick.setVisibility(View.INVISIBLE);
                binding.buttonFollow2Pick.setVisibility(View.VISIBLE);
                follow(9);
            }
        });

        binding.buttonFollow2Pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.buttonFollowPick.setVisibility(View.VISIBLE);
                binding.buttonFollow2Pick.setVisibility(View.INVISIBLE);
                followDelete(89);
            }
        });

        binding.imageProfilePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("프로필유저", userIdx);
                startActivity(intent);
            }
        });

        getPostDetail(a);
    }

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
            binding.layoutIndicatorsPick.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = binding.layoutIndicatorsPick.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.layoutIndicatorsPick.getChildAt(i);
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

    private void getPostDetail(int a) {

        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().getPost1(a);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("성공");
                    Map map = (Map) response.body().getData();
                    System.out.println(map);
                    //System.out.println(Integer.parseInt(map.get("like_count").toString()));
                    binding.textViewLikeCntPick.setText((int) (double) map.get("like_count") + " likes");
                    binding.textView2ContentPick.setText((String) map.get("content"));
                    binding.textViewNickname2Pick.setText((String) map.get("nickname"));

                    userIdx = (int) (double) map.get("user_idx");
                    //SimpleDateFormat day = new SimpleDateFormat("yyyy.MM.dd");
                    //String ndate = day.format((String)map.get("create_date"));

                    binding.textView5Pick.setText((String) map.get("create_date"));
                    System.out.println("aaaaaaaa"+(String) map.get("deadline"));
                    binding.textViewDeadlinePick.setText((String) map.get("deadline"));

                    System.out.println("kkkkk" + map.get("profile_image"));
                    ArrayList map1 = new ArrayList();
                    map1.add((String) map.get("profile_image"));
                    parseResult2((String) map.get("profile_image"));

                    ArrayList map2 = (ArrayList) map.get("image");
                    ArrayList map3 = (ArrayList) map.get("product");
                    likeExsist = (Double) map.get("like_exsist");
                    followExsist = (Double) map.get("follow_exsist");

                    for (int i = 0; i < map3.size(); i++) {
                        Map a = (Map) map3.get(i);
                        postProducts.add(new PostProduct("aa", (String) a.get("size"), (String) a.get("name"), (String) a.get("brand")));
                    }

                    postProductListAdapter = new PostProductListAdapter(postProducts);

                    if ((Double) map.get("like_exsist") == 1.0) {
                        binding.imageViewLike2Pick.setVisibility(View.VISIBLE);
                        binding.imageViewLikePick.setVisibility(View.INVISIBLE);
                    }
                    if (followExsist == 1.0) {
                        binding.buttonFollowPick.setVisibility(View.INVISIBLE);
                        binding.buttonFollow2Pick.setVisibility(View.VISIBLE);
                    }
                    //Map mapa = (Map)map2.get(0);
                    // System.out.println(mapa.get("path"));
                    //  sliderViewPager.setAdapter(new PostImageSliderAdapter(getApplicationContext(), map2));
                    setupIndicators(map2.size());
                    parseResult(map2);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
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
                        System.out.println(b);

                        URL url = new URL(RetrofitClient.BASE_URL + "pictures?url=" + b);
                        System.out.println("aaaa" + url);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                        conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)
                        InputStream is = conn.getInputStream(); //inputStream 값 가져오기
                        dataArrayList.add(url);
                    }
                } catch (MalformedURLException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
        };
        uThread.start(); // 작업 Thread 실행

        try {
            uThread.join();

            adapter = new PostImageSliderAdapter(getApplicationContext(), dataArrayList);
            binding.sliderViewPagerPostImagePick.setAdapter(adapter);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseResult2(String a) {
        final String[] b = {a};
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    b[0] = b[0].replace("\\", "%2F");
                    System.out.println(b[0]);

                    URL url = new URL(RetrofitClient.BASE_URL + "pictures?url=" + b[0]);
                    System.out.println("aaaa" + url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)
                    InputStream is = conn.getInputStream(); //inputStream 값 가져오기

                    Bitmap bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 반환

                    binding.imageProfilePick.setImageBitmap(bitmap);

                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        uThread.start(); // 작업 Thread 실행

        try {
            uThread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void likes(int a) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().postLikes(a);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("성공");

                }else{
                    System.out.println("실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
    }

    private void likeDelete(int a) {

        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().postDeleteLike(a);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("성공");

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
    }


    public void follow(int a) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().follow(userIdx);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("성공");

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
    }

    private void followDelete(int a) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().followDelete(userIdx);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("성공");

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
    }

}