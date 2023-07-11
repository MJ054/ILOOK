package com.example.ilook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilook.Model.PickMain;
import com.example.ilook.PickActivity;
import com.example.ilook.PostActivity;
import com.example.ilook.R;

import java.util.ArrayList;

public class PickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<PickMain> listData = new ArrayList<>();
    private Context mContext = null ;

    public PickAdapter(Activity activity, ArrayList<PickMain> mDataPhoto){
        this.mContext = activity;
        this.listData = mDataPhoto;
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        public MyViewHolder(View view){
            super(view);
            this.textView = (TextView) view.findViewById(R.id.pick_date);
            this.imageView = (ImageView) view.findViewById(R.id.pick_photo);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pick, parent, false);
        return new PickAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PickAdapter.MyViewHolder holder1 = (PickAdapter.MyViewHolder)holder;

        int date = listData.get(position).getDate();
        if(date == 0) {
            holder1.textView.setText("D-Day");
            holder1.textView.setTextColor(Color.parseColor("#ff0000"));
        }else if(date > 0){
            holder1.textView.setText(listData.get(position).getDate() + "-Day");
        }else{
            holder1.textView.setVisibility(View.GONE);
        }
        holder1.imageView.setImageBitmap(listData.get(position).getImage());

        holder1.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), PostActivity.class);
                double postIdx = listData.get(position).getPostIdx();
                intent.putExtra("게시물번호", (int)postIdx);
                intent.putExtra("카테고리", "PICK");
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

   /* public void addItem(Bitmap data) {
        listData.add(data);
    }*/
}