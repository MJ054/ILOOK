package com.example.ilook.Adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilook.Model.DataPhoto;
import com.example.ilook.R;

public class ViewHolderPhoto extends  RecyclerView.ViewHolder {
    ImageView iv_movie;


    public ViewHolderPhoto(@NonNull View itemView) {
        super(itemView);

        iv_movie = itemView.findViewById(R.id.pick_photo);

    }

    public void onBind(DataPhoto data){
        iv_movie.setImageResource(data.getImage());
    }
}