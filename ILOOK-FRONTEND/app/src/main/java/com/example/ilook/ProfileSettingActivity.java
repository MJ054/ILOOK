package com.example.ilook;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityProfileSettingBinding;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettingActivity  extends AppCompatActivity {

    private ActivityProfileSettingBinding binding;
    private Uri image;
    private String profileImage;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar11);


        //intent 값 가져오기
        Intent intent = getIntent();
        System.out.println(profileImage);
        profileImage = intent.getStringExtra("프로필사진");
        nickname = intent.getStringExtra("닉네임");
        Glide.with(this)
                .load(profileImage)
                .into(binding.imageProfileSetting);
        binding.editTextNicknameCh.setText(nickname);

        // 앨범으로 이동하는 버튼
        binding.textView14ProfileImageCh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });


        binding.buttonCh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //프로필 사진만 변경
                        addImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2222){
            if(data == null){   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            }
            else {
                // 이미지를 하나 선택한 경우
                    image = data.getData();
                    Glide.with(this)
                            .load(image)
                            .into(binding.imageProfileSetting);
            }
        }
    }

    //이미지 서버에 저장
    private void addImage() throws IOException {

       // jsonPlaceHolderApi = RetrofitClient.getJsonPlaceHolderApi();

// 여러 file들을 담아줄 ArrayList

       // URL url = new URL(profileImage.toString());
       // File fileI = readFileFromUrl( url );

        ArrayList<MultipartBody.Part> files = new ArrayList<>();
            // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), getFile(getApplicationContext(), image));
            // 사진 파일 이름
        String fileName = "photoProfile" + ".jpg";
            // RequestBody로 Multipart.Part 객체 생성
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, fileBody);
            // 추가
        files.add(filePart);

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), binding.editTextNicknameCh.getText().toString());
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().userPatch(files, requestBody);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("성공");
                    Intent intent = new Intent();
                    setResult(3333);
                    finish();
                }else{
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        String json = response.errorBody().string();
                        Map map = mapper.readValue(json, Map.class);
                        Toast.makeText(getApplicationContext(), (String)map.get("message"), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("실패");
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

    public static File readFileFromUrl(URL imageUrl) throws IOException {
        File temp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
        temp.deleteOnExit();
        FileUtils.copyURLToFile(imageUrl, temp);
        return temp;
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
