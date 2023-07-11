package com.example.ilook;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ilook.Model.PostProduct;
import com.example.ilook.Util.CommonUtils;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar6);

        EditText editText1 = findViewById(R.id.editText_brand);
        EditText editText2 = findViewById(R.id.editText_name);
        EditText editText3 = findViewById(R.id.editText_size);
        Spinner spinner = findViewById(R.id.spinner_month);

        ArrayList arrayList = new ArrayList();
        arrayList.add("상의");
        arrayList.add("아우터");
        arrayList.add("언더웨어");
        arrayList.add("운동복");
        arrayList.add("유아동");
        arrayList.add("전통의류");
        arrayList.add("정장/원피스");
        arrayList.add("하의");

        Intent intent1 = getIntent();
        PostProduct postProduct = (PostProduct) intent1.getParcelableExtra("상품");
        if(postProduct !=null) {
            spinner.setSelection(arrayList.indexOf(postProduct.getCategory()));
            editText1.setText(postProduct.getBrand());
            editText2.setText(postProduct.getName());
            editText3.setText(postProduct.getSize());
        }

        TextView button = findViewById(R.id.button_complete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText1.getText().toString().length() == 0 || editText2.getText().toString().length() == 0|| editText3.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("category", spinner.getSelectedItem().toString());
                intent.putExtra("brand",editText1.getText().toString());
                intent.putExtra("name",editText2.getText().toString());
                intent.putExtra("size",editText3.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

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
