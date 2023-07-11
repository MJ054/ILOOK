package com.example.ilook.Adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ilook.R;

class HeaderViewHolder extends RecyclerView.ViewHolder {

    TextView textView1;
    TextView textView2;
    TextView textView3;
    ImageView imageView;
    TextView textViewN;

    Button edit;
    Button follower;
    Button followed;

    ImageView setting;
    TextView follower_tv;
    TextView followed_tv;

    HeaderViewHolder(View headerView) {
        super(headerView);
        textView1 = itemView.findViewById(R.id.textView_follower_num) ;
        textView2 = itemView.findViewById(R.id.textView_follow_num) ;
        textView3 = itemView.findViewById(R.id.textView_post_num) ;
        imageView = itemView.findViewById(R.id.image_profile_setting);
        textViewN = itemView.findViewById(R.id.textView_nickname);
        edit = itemView.findViewById(R.id.button_profile_edit);
        follower = itemView.findViewById(R.id.button_profile_follow);
        followed = itemView.findViewById(R.id.button_profile_follow2);
        setting = itemView.findViewById(R.id.imageView_setting);
        follower_tv = itemView.findViewById(R.id.textView_follower_num);
        followed_tv = itemView.findViewById(R.id.textView_follow_num);
    }
}