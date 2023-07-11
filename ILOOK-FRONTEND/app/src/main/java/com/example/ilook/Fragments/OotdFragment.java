package com.example.ilook.Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilook.Adapter.OotdAdapter;
import com.example.ilook.Model.DataPhoto2;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class OotdFragment extends Fragment {

    int page=0, limit=6;
    //FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    OotdAdapter adapter;
    Bitmap bitmap;
    ProgressBar progressBar;
    ArrayList<DataPhoto2> dataArrayList;
    NestedScrollView nestedScrollView;
    private SharedPreferences preferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("OOtdFragment");

        preferences = getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);
        SharedPreferences auto = getActivity().getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
        System.out.println(auto.getString("accessToken", null));

        View view = inflater.inflate(R.layout.fragment_ootd, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_ootd);
        progressBar = view.findViewById(R.id.progress_bar_ootd);
        nestedScrollView = view.findViewById(R.id.nestedScrollView_ootd);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        //새로 ^^
        dataArrayList = new ArrayList<>();
        adapter = new OotdAdapter(getActivity(), dataArrayList,getContext());
        recyclerView.setAdapter(adapter);

        //Data를 가져온다
        getData(page, limit);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    System.out.println(dataArrayList.size());
                    int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int itemTotalCount = recyclerView.getAdapter().getItemCount()-1;
                    if(lastVisibleItemPosition == itemTotalCount) {
                        progressBar.setVisibility(View.VISIBLE);
                        page = page + limit;
                        getData(page, limit);
                    }
                }
            }
        });


        return view;
    }

    ////선

    private void getData(int page, int limit)
    {

        preferences = getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().getMainImages("OOTD",page,limit);
        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    progressBar.setVisibility(View.GONE);
                    ArrayList<JSONObject> array = (ArrayList) response.body().getData();
                    System.out.println(array);;
                    parseResult(array);
                }else{
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                t.printStackTrace();
                System.out.println("에러");
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void parseResult(ArrayList array)
    {
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.size(); i++) {
                        Map map = (Map) array.get(i);
                        String path = (String)map.get("path");
                        path = path.replace("\\", "%2F");
                        int postIdx = ((Double) map.get("post_idx")).intValue();
                        URL url = new URL(RetrofitClient.BASE_URL + "pictures?url=" + path);
                        Uri uri = Uri.parse(url.toString());
                        System.out.println(url.getPath());
                        dataArrayList.add(new DataPhoto2(uri, postIdx));
                    }
                } catch (MalformedURLException e) {

                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        };
        uThread.start(); // 작업 Thread 실행

        try{
            uThread.join();

            adapter = new OotdAdapter(getActivity(),dataArrayList,getContext());
            recyclerView.setAdapter(adapter);

            progressBar.setVisibility(View.GONE);

        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
