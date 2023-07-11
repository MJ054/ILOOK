package com.example.ilook.Util;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ilook.R;

import java.net.MalformedURLException;
import java.net.URL;

public class CommonUtils {

    public static void createToolbar(Activity activity, int toolbarNum) {
        Toolbar toolbar = activity.findViewById(toolbarNum);
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
        toolbar.setTitle("");
        toolbar.setSubtitle("");
    }

    public static String BearerRemove(String token) {
        return token.substring("Bearer ".length());
    }

}
