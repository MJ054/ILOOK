package com.example.ilook.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilook.Adapter.ProfileAdapter;
import com.example.ilook.Model.DataPhoto3;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.R;
import com.example.ilook.databinding.FragmentProfileBinding;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {


    private RecyclerView recyclerView;
    private ProfileAdapter adapter ;
    private ArrayList<Integer> items = new ArrayList<>();
    private ArrayList<DataPhoto3> items2 = new ArrayList<>();

    private boolean isLoading = false;

    private FragmentProfileBinding fragmentProfileBinding;
    String followerNum;
    String followedNum;
    String postCnt;
    Bitmap image;
    Bitmap image2;
    String nickname;
    double identification;
    double follow;
    String category;
    URL profileImage;
    int followNum2;
    Double role;
    int profileUser;
    int userIdx;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = fragmentProfileBinding.getRoot();

        recyclerView = view.findViewById(R.id.recyclerView2);

        int status;
        try{
            //post 통해서 들어왔을 때
            Bundle bundle = getArguments();
            status = bundle.getInt("프로필유저");
        } catch(Exception ex){
            //하단바 통해서 들어왔을 때
            status = -1;
        }

        System.out.println(status);
        if(status == -1){
            //하단바 통해 들어왔을 때
            getMyProfile();
        }else{
            //게시물 통해 들어왔을 때
            getUserProfile(status);
        }
       // initScrollListener();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(position){
                    case 0:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        return view;

    }

    private void getMyProfile()
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().getMyProfile();
        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");
                    Map map =  (Map)response.body().getData();
                    System.out.println(map);
                    role = 0.0;
                    getResult(map, role);

                }else{
                    System.out.println("실패");
                    SharedPreferences auto = getActivity().getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
                    String authorization = auto.getString("accessToken", null);
                    System.out.println(authorization);

                    //팝업창 띄우기
                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                    ad.setIcon(R.mipmap.ic_launcher);
                    ad.setMessage("로그인이 필요한 기능이에요. 로그인 페이지로 이동하시겠어요?");
                    ad.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getParentFragmentManager().beginTransaction().replace(R.id.framelayout22, new LoginFragment()).commit();
                        }
                    });
                    ad.setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();

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

    private void getUserProfile(int status)
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().getUserProfile(status);
        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println(status);
                    System.out.println("성공");
                    Map map =  (Map)response.body().getData();
                    System.out.println(map);

                    role = (Double)map.get("role");
                    getResult(map, role);


                }else {
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

    //profile 결과 get
    private void getResult(Map map ,Double role) {

        if(role == 1.0){
            follow = (Double)map.get("follow");
        }else{
            follow = -1;
        }

        followerNum = Integer.toString((int)(double)map.get("followerCnt"));
        followedNum = Integer.toString((int)(double)map.get("followedCnt"));
        followNum2 = (int)(double)map.get("followedCnt");
        postCnt = Integer.toString((int)(double)map.get("postCnt"));
        nickname = (String)map.get("nickname");
        int userIdx = (int)Math.round((Double) map.get("user_idx"));
        parseResult2((String) map.get("profile_image"));
        ArrayList map2 = (ArrayList) map.get("images");
        ArrayList map3 = (ArrayList) map.get("post_idx");
        parseResult(map2, map3);

        adapter = new ProfileAdapter(items2,followerNum,followedNum,postCnt,nickname,getContext(), role,follow,userIdx,profileImage,followNum2);
        recyclerView.setAdapter(adapter);
    }



    private void parseResult(ArrayList map2, ArrayList map3)
    {
                    for (int i = 0; i < map2.size(); i++) {

                        Map a = (Map) map2.get(i);
                        String b = (String) a.get("path");
                        b = b.replace("\\", "%2F");

                        URL url = null;
                        try {
                            url = new URL(RetrofitClient.BASE_URL + "pictures?url=" + b);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        items2.add(new DataPhoto3(url, (int) Math.round((Double) (a.get("post_idx"))), ((String) a.get("category"))));
                    }
    }

    private void parseResult2(String a)
    {
        final String[] b = {a};
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    b[0] = b[0].replace("\\", "%2F");
                    System.out.println(b[0]);

                    URL url = new URL(RetrofitClient.BASE_URL + "pictures?url="+ b[0]);
                    profileImage = url;

                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        uThread.start(); // 작업 Thread 실행

        try{
            uThread.join();

        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void initScrollListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                if (!isLoading) {
                    if (gridLayoutManager != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() == items.size()-1) {
                        //리스트 마지막
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        items.add(null);
        adapter.notifyItemInserted(items.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                items.remove(items.size() - 1);
                int scrollPosition = items.size();
                adapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    items.add(currentSize);
                    currentSize++;
                }

                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }


}
