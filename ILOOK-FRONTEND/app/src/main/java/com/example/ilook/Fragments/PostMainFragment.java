package com.example.ilook.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ilook.AddPickActivity;
import com.example.ilook.AddPostActivity;
import com.example.ilook.MainActivity;
import com.example.ilook.R;
import com.example.ilook.databinding.ActivityHomeBinding;
import com.example.ilook.databinding.FragmentProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PostMainFragment extends Fragment {

    ActivityHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         FragmentManager fragmentManager;
         OotdFragment ootdFragment;
         PickFragment pickFragment;
         FragmentTransaction transaction;

        binding = ActivityHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        System.out.println("PostMainFragment");

      /*  //로그인 여부
        SharedPreferences auto = getActivity().getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
        String authorization = auto.getString("accessToken", null);
        if(authorization == null){
            //로그인 안된 사용자
            binding.imageViewPlusPost.setVisibility(View.INVISIBLE);
        }*/


        fragmentManager = getParentFragmentManager();
        ootdFragment = new OotdFragment();
        pickFragment = new PickFragment();

        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_home, ootdFragment).commit();

        binding.homeOotd.setTextColor(Color.parseColor("#000000"));
        binding.homeOotd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.homeOotd.setTextColor(Color.parseColor("#000000"));
                binding.homePick.setTextColor(Color.parseColor("#959494"));;
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_home, ootdFragment).commit();
            }
        });

        binding.homePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homePick.setTextColor(Color.parseColor("#000000"));
                binding.homeOotd.setTextColor(Color.parseColor("#959494"));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_home, pickFragment).commit();
            }
        });

        binding.imageViewPlusPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.framelayout_home);
                System.out.println(fragment);
                if(fragment instanceof OotdFragment) {
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    startActivity(intent);
                }else if(fragment instanceof PickFragment){
                    Intent intent = new Intent(getActivity(), AddPickActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

}