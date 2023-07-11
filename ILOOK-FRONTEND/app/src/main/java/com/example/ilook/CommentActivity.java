package com.example.ilook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ilook.Adapter.CommentAdapter;
import com.example.ilook.Model.CommentGroup;
import com.example.ilook.Model.CommentList;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Model.Comment;
import com.example.ilook.Api.JsonPlaceHolderApi;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Model.SoftKeyboardDectectorView;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityCommentBinding;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.example.ilook.Model.MyApp.getContext;

public class CommentActivity extends AppCompatActivity{

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private ActivityCommentBinding binding;
    private CommentAdapter adatpter;
    private List<CommentList> replyArrayList;
    private int parent=-1;
    private int postIdx;
    private int state = 0;
    private List<CommentGroup> groupArrayList;
    private int position = 0;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent= getIntent();
        postIdx = intent.getIntExtra("게시글번호", 0);

        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar12);

        replyArrayList = new ArrayList<>();
        groupArrayList = new ArrayList<>();

        binding.recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));
        adatpter = new CommentAdapter(groupArrayList, getApplication(), postIdx);
        binding.recyclerViewComment.setAdapter(adatpter);


        binding.buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    insertComment(parent);
            }
        });


        final SoftKeyboardDectectorView softKeyboardDectector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDectector,new FrameLayout.LayoutParams(-1,-1));
        //키보드 상태 리스너 메소드 오버라이드
        //키보드 팝업 때
        softKeyboardDectector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                Log.d(TAG,"키보드 올라와요");
                //키보드 팝업 시 이벤트 쓰는 부분
            }
        });

        //키보드 사라질 때
        softKeyboardDectector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                Log.d(TAG,"키보드 내려가요");
                binding.editTextComment.setHint("댓글을 입력하세요");
                parent = -1;
            }
        });

        getComment();

    }

    private void insertComment(int parent) {
        String comment = binding.editTextComment.getText().toString();
        binding.editTextComment.setText("");

        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().comment(new Comment(comment,parent,postIdx));
        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("댓글달기 성공");

                    Map map = (Map)response.body().getData();
                    URL url = null;
                    try {
                        url = new URL(RetrofitClient.BASE_URL + "pictures?url="+(String)map.get("profile_image"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    groupArrayList.add(new CommentGroup(new CommentList((int)Math.round((Double) map.get("user_idx")),
                                (int)Math.round((Double) map.get("comment_idx")), url
                                ,(String)map.get("nickname"),(String)map.get("comment"),(String)map.get("create_date")
                    ),replyArrayList));

                    adatpter.notifyDataSetChanged();

                }else{
                    System.out.println("댓글달기 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                t.printStackTrace();
                System.out.println("댓글달기 에러");
            }
        });

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

    private void getComment()
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().commentList(postIdx, parent);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");
                    List<Map> commentList = (List<Map>)response.body().getData();

                    System.out.println(commentList);
                    for(int i = 0; i<commentList.size();i++){
                        Map map = commentList.get(i);

                        URL url = null;

                        try {
                             url = new URL(RetrofitClient.BASE_URL + "pictures?url="+(String)map.get("profile_image"));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        groupArrayList.add(new CommentGroup(new CommentList((int)Math.round((Double) map.get("user_idx")),
                                (int)Math.round((Double) map.get("comment_idx")), url
                                ,(String)map.get("nickname"),(String)map.get("comment"),(String)map.get("create_date")
                                ),replyArrayList));
                    }

                    adatpter = new CommentAdapter(groupArrayList, getApplication(),postIdx);
                    binding.recyclerViewComment.setAdapter(adatpter);

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

}




