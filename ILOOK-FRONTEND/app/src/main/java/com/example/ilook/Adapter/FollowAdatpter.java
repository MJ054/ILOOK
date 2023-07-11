package com.example.ilook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ilook.MainActivity;
import com.example.ilook.Model.Follow;
import com.example.ilook.PostActivity;
import com.example.ilook.R;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;

public class FollowAdatpter extends RecyclerView.Adapter {

    private ArrayList<Follow> followArrayList = null ;
    private Context mContext = null ;

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public FollowAdatpter(ArrayList<Follow> followArrayList, Context context) {
        this.followArrayList = followArrayList ;
        this.mContext = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView nickname;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.image_profile_follow);
            nickname = itemView.findViewById(R.id.textView6_nickname);
        }
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
    @Override
    public FollowAdatpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.item_follow, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        FollowAdatpter.ViewHolder vh = new FollowAdatpter.ViewHolder(view) ;
        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        FollowAdatpter.ViewHolder holder1 = (FollowAdatpter.ViewHolder)holder;

        URL image_uri = followArrayList.get(position).getImage();
        String nickname = followArrayList.get(position).getNickname();

        Glide.with(mContext)
                .load(image_uri)
                .into(holder1.image);

        holder1.nickname.setText(nickname);

        holder1.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
                intent.putExtra("프로필유저", followArrayList.get(position).getUserIdx());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return followArrayList.size() ;
    }

}
