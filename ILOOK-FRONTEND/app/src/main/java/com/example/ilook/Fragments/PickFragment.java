package com.example.ilook.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.ilook.Adapter.PickAdapter;
import com.example.ilook.Model.PickMain;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class PickFragment extends Fragment {

    int page=0, limit=6;
    //FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    PickAdapter adapter;
    Bitmap bitmap;
    ProgressBar progressBar;
    ArrayList<PickMain> dataArrayList;
    NestedScrollView nestedScrollView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pick, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_pick);
        progressBar = view.findViewById(R.id.progress_bar_pick);
        nestedScrollView = view.findViewById(R.id.nestedScrollView_pick);

        dataArrayList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new PickAdapter(getActivity(),dataArrayList);
        recyclerView.setAdapter(adapter);


        //Data를 얻어온다
        getData(page, limit);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    progressBar.setVisibility(View.VISIBLE);
                    page = page+limit;
                    getData(page, limit);
                }
            }
        });


        return view;
    }

    ////선

    private void getData(int page, int limit)
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().getMainImages("PICK",page,limit);
        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    progressBar.setVisibility(View.GONE);

                    System.out.println(response.toString());
                    JSONArray jsonArray;
                    System.out.println(response.body().getData());

                    ArrayList<JSONObject> array = (ArrayList) response.body().getData();
                    System.out.println(array);
                    System.out.println(array.get(0));
                    parseResult(array);
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

    private void parseResult(ArrayList<JSONObject> array)
    {
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < array.size(); i++) {

                        JSONObject jsonObject = new JSONObject((Map) array.get(i));

                        String a = (String) jsonObject.get("path");
                        a = a.replace("\\", "%2F");

                        URL url = new URL(RetrofitClient.BASE_URL + "pictures?url="+a);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                        conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)
                        InputStream is = conn.getInputStream(); //inputStream 값 가져오기

                        bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 반환
                        dataArrayList.add(new PickMain(bitmap, (int)(double)jsonObject.get("date"),(Double)jsonObject.get("post_idx")));
                    }
                } catch (MalformedURLException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        uThread.start(); // 작업 Thread 실행

        try{
            uThread.join();

            adapter = new PickAdapter(getActivity(),dataArrayList);
            recyclerView.setAdapter(adapter);

            progressBar.setVisibility(View.GONE);

        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
