package com.example.ilook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ilook.FollowActivity;
import com.example.ilook.Model.DataPhoto3;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.PostActivity;
import com.example.ilook.ProfileSettingActivity;
import com.example.ilook.R;
import com.example.ilook.SettingActivity;

import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ilook.Model.MyApp.getContext;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 2;

    private Context mContext = null ;
    private List<DataPhoto3> items;
    private String followerNum;
    private String followNum;
    private String postNum;
    private URL profilImage;
    public String nickname;
    private Double role;
    private Double follow;
    private int userIdx;
    //private int Intfollow;
    private int followNum2;


    public ProfileAdapter(List<DataPhoto3> items, String followerNum, String followNum, String postNum, String nickname,Context context,
                          Double role, Double follow,int userIdx, URL profilImage, int followNum2) {
        this.items = items;
        this.followerNum = followerNum;
        this.followNum = followNum;
        this.postNum = postNum;
        this.nickname = nickname;
        mContext = context;
        this.role = role;
        this.follow = follow;
        this.userIdx = userIdx;
        this.profilImage = profilImage;
        this.followNum2 = followNum2;
        System.out.println(followNum2);
       // Intfollow = Integer.valueOf(followNum);
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_profile, parent, false);
            return new HeaderViewHolder(view);
        }
        else if(viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), PostActivity.class);
                    parent.getContext().startActivity(intent);
                }
            });
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){


            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            //ViewHolder holder1 = (ViewHolder) holder;
            headerViewHolder.textView1.setText(followerNum);
            headerViewHolder.textView2.setText(followNum);
            headerViewHolder.textView3.setText(postNum);
            //headerViewHolder.imageView.setImageBitmap(image);
            Glide.with(mContext)
                    .load(profilImage)
                    .into(headerViewHolder.imageView);
            headerViewHolder.textViewN.setText(nickname);

            //Intfollow = Integer.parseInt(followNum);
            //System.out.println(followNum);
            System.out.println(role);

            if(role == 0.0){
                //내 프로필
                headerViewHolder.edit.setVisibility(View.VISIBLE);
                headerViewHolder.follower.setVisibility(View.INVISIBLE);
                headerViewHolder.followed.setVisibility(View.INVISIBLE);
            }else{
                //다른사람 프로필
                headerViewHolder.edit.setVisibility(View.INVISIBLE);
                headerViewHolder.setting.setVisibility(View.INVISIBLE);
                if(follow == 0.0) {
                    headerViewHolder.follower.setVisibility(View.VISIBLE);
                    headerViewHolder.followed.setVisibility(View.INVISIBLE);
                }else if(follow == 1.0){
                    headerViewHolder.follower.setVisibility(View.INVISIBLE);
                    headerViewHolder.followed.setVisibility(View.VISIBLE);
                }
            }

            headerViewHolder.setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SettingActivity.class);
                    mContext.startActivity(intent);
                }
            });

            headerViewHolder.follower_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, FollowActivity.class);
                    intent.putExtra("유저번호", userIdx);
                    intent.putExtra("팔로워수", followerNum);
                    intent.putExtra("팔로우수", followNum);
                    mContext.startActivity(intent);
                }
            });

            headerViewHolder.followed_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, FollowActivity.class);
                    intent.putExtra("유저번호", userIdx);
                    intent.putExtra("팔로워수", followerNum);
                    intent.putExtra("팔로우수", followNum);
                    mContext.startActivity(intent);
                }
            });

            headerViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProfileSettingActivity.class);
                    intent.putExtra("프로필사진", profilImage.toString());
                    intent.putExtra("닉네임", nickname);
                    ((Activity)mContext).startActivityForResult(intent,3333);                }
            });

            headerViewHolder.follower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    follow(userIdx);
                    followNum2 = followNum2+1;
                    headerViewHolder.textView2.setText(Integer.toString(followNum2));
                    headerViewHolder.follower.setVisibility(View.INVISIBLE);
                    headerViewHolder.followed.setVisibility(View.VISIBLE);
                }
            });

            headerViewHolder.followed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followDelete(userIdx);
                    followNum2 = followNum2-1;
                    headerViewHolder.textView2.setText(Integer.toString(followNum2));
                    headerViewHolder.follower.setVisibility(View.VISIBLE);
                    headerViewHolder.followed.setVisibility(View.INVISIBLE);
                }
            });

        } else if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position == items.size() + 1)
            return VIEW_TYPE_LOADING;
        else
            return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items.size()+1;
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {

    }



    private void populateItemRows(ItemViewHolder holder, int position) {
        DataPhoto3 item = items.get(position-1);

         holder.setItem(item);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_photo);
        }

        public void setItem(DataPhoto3 item) {
          //  imageView.setImageResource(item);
            Glide.with(mContext)
                    .load(item.getImage())
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext.getApplicationContext(), PostActivity.class);
                    intent.putExtra("게시물번호", item.getPostIdx());
                    intent.putExtra("카테고리", item.getCategory());
                    intent.putExtra("결과", 3333);
                    ((Activity) mContext).startActivityForResult(intent,3333);
                }
            });

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    //팔로우
    public void follow(int userIdx){
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().follow(userIdx);

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

    //팔로우 취소
    private void followDelete(int userIdx) {

        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().followDelete(userIdx);

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