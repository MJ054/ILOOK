package com.example.ilook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ilook.MainActivity;
import com.example.ilook.Model.CommentList;
import com.example.ilook.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReplyApdapter extends RecyclerView.Adapter {

    public List<CommentList> commentArrayList = new ArrayList<>() ;
    private Context mContext = null ;
    private View view;
    private ReplyApdapter.ViewHolder holder1;
    private Context context;


    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public ReplyApdapter(List<CommentList> commentArrayList, Context context) {
        this.commentArrayList = commentArrayList ;
        this.mContext = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView nickname;
        TextView comment;
        TextView date;
        TextView reply;
        RecyclerView recyclerView;
        TextView getReply;

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.image_profile_comment);
            nickname = itemView.findViewById(R.id.textView6_comment_nickname);
            comment = itemView.findViewById(R.id.textView14_comment);
            date = itemView.findViewById(R.id.textView24_comment_date);
            reply = itemView.findViewById(R.id.textView25_comment_reply);
            getReply = itemView.findViewById(R.id.textView26_comment_reply_list);
            recyclerView = itemView.findViewById(R.id.recyclerView_reply);
            getReply.setVisibility(View.GONE);
        }
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
    @Override
    public ReplyApdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        view = inflater.inflate(R.layout.item_comment, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        ReplyApdapter.ViewHolder vh = new ReplyApdapter.ViewHolder(view) ;
        context = parent.getContext();
        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder1 = (ReplyApdapter.ViewHolder)holder;

        URL profileImage = commentArrayList.get(position).getProfileImage();
        String nickname = commentArrayList.get(position).getNickname();
        String comment = commentArrayList.get(position).getComment();
        String createDate = commentArrayList.get(position).getCreateDate();

        Glide.with(mContext)
                .load(profileImage)
                .into(holder1.image);

        holder1.nickname.setText(nickname);
        holder1.comment.setText(comment);
        holder1.date.setText(createDate);

        holder1.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
                intent.putExtra("프로필유저", commentArrayList.get(position).getUserIdx());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return commentArrayList.size() ;
    }

}

