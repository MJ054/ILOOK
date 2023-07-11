package com.example.ilook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.ilook.Model.DataPhoto2;
import com.example.ilook.PostActivity;
import com.example.ilook.R;
import com.example.ilook.RegisterActivity;

import java.util.ArrayList;

public class OotdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<DataPhoto2> listData = new ArrayList<>();
    private Activity activity;
    private Context context;

    public OotdAdapter(Activity activity, ArrayList<DataPhoto2> mDataPhoto, Context context){
        this.activity = activity;
        this.listData = mDataPhoto;
        this.context = context;
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        public MyViewHolder(View view){
            super(view);
            this.imageView = view.findViewById(R.id.ootd_photo);
            this.textView = view.findViewById(R.id.textView_post_id);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilook_photo, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holder1 = (MyViewHolder)holder;

        Glide.with(holder.itemView)
                .load(listData.get(position).getImage())
                .into(holder1.imageView);   // Glide 코드 작성

        holder1.textView.setText(Integer.toString(listData.get(position).getPostIdx()));

        holder1.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), PostActivity.class);
                intent.putExtra("게시물번호", listData.get(position).getPostIdx());
                intent.putExtra("카테고리", "OOTD");
                intent.putExtra("결과", 5555);
                ((Activity) activity).startActivityForResult(intent,5555);

            }
        });
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

}