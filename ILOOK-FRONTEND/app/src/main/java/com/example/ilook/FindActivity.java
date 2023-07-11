package com.example.ilook;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ilook.Fragments.FindIDFragment;
import com.example.ilook.Fragments.FindPwFragment;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityFindBinding;
import com.example.ilook.databinding.ActivityLoginBinding;

public class FindActivity extends AppCompatActivity {

    private ActivityFindBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityFindBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar3);

        //fragment 생성
        FindIDFragment findIDFragment= new FindIDFragment();
        FindPwFragment findPwFragment= new FindPwFragment();


        TextView button_main = findViewById(R.id.textView11);
        TextView button_menu = findViewById(R.id.textView12);

        button_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_main.setTextColor(Color.parseColor("#000000"));
                button_menu.setTextColor(Color.parseColor("#959494"));;
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout2, findIDFragment).commit();
            }
        });

        button_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_menu.setTextColor(Color.parseColor("#000000"));
                button_main.setTextColor(Color.parseColor("#959494"));
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout2, findPwFragment).commit();
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
}
