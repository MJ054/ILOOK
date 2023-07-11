package com.example.ilook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.ilook.Fragments.PostMainFragment;
import com.example.ilook.Fragments.ProfileFragment;
import com.example.ilook.Fragments.SearchFragment;
import com.example.ilook.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    public static Context context_main;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        context_main = this;

        Intent intent= getIntent();
        int profileUser = intent.getIntExtra("프로필유저", 0);
        System.out.println(profileUser);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectorFragment = new PostMainFragment();
                        break;
                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;
                    case R.id.nav_mypage:
                        selectorFragment = new ProfileFragment();
                        break;

                }
                if(selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
                return true;
            }
        });

        if(profileUser == 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostMainFragment()).commit();
        }else {
            ProfileFragment fragment = new ProfileFragment();
            //requestActivity에 fragment1을 띄워줌
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            //번들객체 생성, text값 저장

            Bundle bundle = new Bundle();
            bundle.putInt("프로필유저",profileUser);
            //fragment1로 번들 전달
            fragment.setArguments(bundle);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode);
        System.out.println(requestCode);
        if(resultCode == 3333) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        }else if(resultCode == 5555)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostMainFragment()).commit();
        }

    private long backpressedTime = 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {

            //자동로그인 여부
            SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
            String check = pref.getString("check",null);
            if(check == null){
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("accessToken");
                editor.remove("refreshToken");
                editor.remove("check");
                editor.commit();
            }

            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }
    }

}
