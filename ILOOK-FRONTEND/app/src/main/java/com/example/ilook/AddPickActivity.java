package com.example.ilook;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ilook.Adapter.AddPostAdapter;
import com.example.ilook.Model.PickFileVo;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityAddPickBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPickActivity extends AppCompatActivity {


    private ActivityAddPickBinding binding;
    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    AddPostAdapter adapter;  // 사진에 적용시킬 어댑터
    int numIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityAddPickBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       // RetrofitClient retrofitClient = RetrofitClient.getInstance(getApplicationContext());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar6_pick);


        // 앨범으로 이동하는 버튼
        binding.buttonAddPicPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });


        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.pickDday.setText(year+"-" + (month+1) + "-" + dayOfMonth);
            }
        }, mYear, mMonth, mDay);


        binding.pickDdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        //게시글 추가 버튼
        binding.buttonPickAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //사진 1장이상, content check
                if(uriList.size() == 0){
                    Toast.makeText(getApplicationContext(), "사진을 1장 이상 추가해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //내용이 작성되지 않았을 경우
                if(binding.pcikStory.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "내용을 작성하여주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String category = "PICK";

                Date rdate = null;
                try {
                    String date = binding.pickDday.getText().toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    rdate = formatter.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                System.out.println(rdate);


                addData(new PickFileVo(binding.pcikStory.getText().toString(),
                        category,binding.pickDday.getText().toString()));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }

    //toolbar의 back 키를 눌렸을 때 동작
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back 키를 눌렸을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2222){
            if(data == null){   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            }
            else{   // 이미지를 하나라도 선택한 경우
                binding.recyclerViewPick.setVisibility(View.VISIBLE);
                binding.textViewOnePick.setVisibility(View.INVISIBLE);
                if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);

                    adapter = new AddPostAdapter(uriList, getApplicationContext());
                    binding.recyclerViewPick.setAdapter(adapter);
                    binding.recyclerViewPick.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
                }
                else{      // 이미지를 여러장 선택한 경우
                    ClipData clipData = data.getClipData();
                    Log.e("clipData", String.valueOf(clipData.getItemCount()));

                    if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                        Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                    else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                        // Log.e(TAG, "multiple choice");

                        for (int i = 0; i < clipData.getItemCount(); i++){
                            Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                            try {
                                uriList.add(imageUri);  //uri를 list에 담는다.

                            } catch (Exception e) {
                                //Log.e(TAG, "File select error", e);
                            }
                        }
                        adapter = new AddPostAdapter(uriList, getApplicationContext());
                        binding.recyclerViewPick.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                        binding.recyclerViewPick.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                    }
                }
            }
        }
    }


    //이미지 서버에 저장
    private void addImage(ArrayList<Uri> data) throws IOException {

        RequestBody k = null;
        String ext = null;
        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        final MediaType MEDIA_TYPE_GIF = MediaType.parse("image/gif");
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

       // jsonPlaceHolderApi = RetrofitClient.getJsonPlaceHolderApi();

// 여러 file들을 담아줄 ArrayList
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

// 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
        for (int i = 0; i < data.size(); ++i) {

            data.get(i).getPath();

            // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), getFile(getApplicationContext(), data.get(i)));
            // 사진 파일 이름
            String fileName = "photo" + i + ".jpg";
            // RequestBody로 Multipart.Part 객체 생성
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, fileBody);
            System.out.println(filePart);
            // 추가
            files.add(filePart);
        }

        String text =  Integer.toString(numIdx);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), text);
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().uploadImages(files, requestBody);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                t.printStackTrace();
                System.out.println("에러");
            }
        });

    }
    ////
    public static File getFile(Context context, Uri uri) throws IOException {
        File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri));
        try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
        return destinationFilename;
    }

    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String queryName(Context context, Uri uri) {
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    //데이터 서버에 저장
    private void addData(PickFileVo pickFileVo)
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().uploadDataPick(pickFileVo);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {

                    System.out.println("성공");
                    double d = (double)response.body().getData();
                    numIdx = (int) d;
                    try {
                        addImage(adapter.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                t.printStackTrace();
                System.out.println("에러");
            }
        });
    }
}

