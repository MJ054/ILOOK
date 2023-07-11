package com.example.ilook;

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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ilook.Adapter.AddPostAdapter;
import com.example.ilook.Adapter.AddProductAdapter;
import com.example.ilook.Adapter.ItemClickListener;
import com.example.ilook.Model.BoardFileVo;
import com.example.ilook.Model.PostProduct;
import com.example.ilook.Model.ApiResponse;
import com.example.ilook.Util.RetrofitClient;
import com.example.ilook.Util.CommonUtils;
import com.example.ilook.databinding.ActivityAddPostBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ilook.Model.MyApp.getContext;

public class AddPostActivity extends AppCompatActivity implements ItemClickListener {

    private static final String TAG = "MultiImageActivity";
    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    ArrayList<URL> uList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    ArrayList<PostProduct> List2;     // 이미지의 uri를 담을 ArrayList 객체
    AddPostAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터
    AddProductAdapter addProductAdapter;
    Switch switchView;
    int advertise;
    int numIdx;
    int postIdx;

    private ActivityAddPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //툴바 생성
        CommonUtils.createToolbar(this, R.id.toolbar6);


        Map map = (Map) getIntent().getSerializableExtra("map");
        System.out.println(map);

        List2 = new ArrayList<>();

        if (map != null) {

            //추가버튼 invisible, 수정버튼 보이게
            binding.buttonPostEdit.setVisibility(View.VISIBLE);
            binding.buttonPostAdd.setVisibility(View.INVISIBLE);

            postIdx = (int) (double) map.get("post_idx");
            System.out.println(postIdx);
            //이미지 설정
            binding.recyclerViewOotd.setVisibility(View.VISIBLE);
            binding.textViewOnePost.setVisibility(View.INVISIBLE);
            ArrayList arrayList = (ArrayList) map.get("image");
            URL url = null;
            Uri uri = null;

            for (int i = 0; i < arrayList.size(); i++) {
                Map map1 = (Map) arrayList.get(i);
                try {
                    url = new URL("http://192.168.0.7:8080/pictures?url=" + map1.get("path"));
                    uri = Uri.parse(url.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                uriList.add(uri);
                uList.add(url);
            }

            adapter = new AddPostAdapter(uriList, getApplicationContext());
            binding.recyclerViewOotd.setAdapter(adapter);
            binding.recyclerViewOotd.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

            //content
            String content = (String) map.get("content");
            binding.editTextTextPersonName5.setText(content);

            //착용상품추가
            ArrayList products = (ArrayList) map.get("product");
            for (int i = 0; i < products.size(); i++) {
                Map product = (Map) products.get(i);
                List2.add(new PostProduct((String) product.get("category"), (String) product.get("brand"), (String) product.get("name"), (String) product.get("size")));
                binding.recyclerView3AddPost.setVisibility(View.VISIBLE);
                addProductAdapter = new AddProductAdapter(List2, (Context) this, this);
                binding.recyclerView3AddPost.setAdapter(addProductAdapter);
                binding.recyclerView3AddPost.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            }

        }

        // 앨범으로 이동하는 버튼
        binding.buttonAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        //상품 추가 버튼
        binding.buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
                startActivityForResult(intent, 1111);
                //startActivity(intent);
            }
        });

        //content, advertise, category, userIdx
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //True이면
                    advertise = 1;
                    System.out.println(advertise);
                } else {
                    //False이면
                    advertise = 0;
                    System.out.println(advertise);
                }
            }
        });

        //게시글 추가 버튼
        binding.buttonPostAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = binding.editTextTextPersonName5.getText().toString();
                //사진 1장이상, content check
                if (uriList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "사진을 1장 이상 추가해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (content.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 작성하여주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String category = "OOTD";

                addData(new BoardFileVo(postIdx, content, advertise, category, List2));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemSelected(View v, int position) {
        Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
        intent.putExtra("상품",  List2.get(position));
        List2.remove(position);
        startActivityForResult(intent,1111);
        //mContext.startActivity(intent);
    }

    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if(requestCode == 1111){
            if(data == null){
                return;
            }
            System.out.println(data.getStringExtra("category"));
            List2.add(new PostProduct(data.getStringExtra("category"),data.getStringExtra("brand"),
                    data.getStringExtra("name"),data.getStringExtra("size")));
            binding.recyclerView3AddPost.setVisibility(View.VISIBLE);
            //System.out.println(data.getStringExtra("brand"));
            addProductAdapter = new AddProductAdapter(List2,(Context)this,this);
            binding.recyclerView3AddPost.setAdapter(addProductAdapter);
            addProductAdapter.notifyDataSetChanged();
            binding.recyclerView3AddPost.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        }

        if(requestCode == 2222){
             if(data == null){   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
             }
            else{   // 이미지를 하나라도 선택한 경우
                 binding.recyclerViewOotd.setVisibility(View.VISIBLE);
                 binding.textViewOnePost.setVisibility(View.INVISIBLE);
                  if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                     Log.e("single choice: ", String.valueOf(data.getData()));
                     Uri imageUri = data.getData();
                     uriList.add(imageUri);

                     adapter = new AddPostAdapter(uriList, getApplicationContext());
                      binding.recyclerViewOotd.setAdapter(adapter);
                      binding.recyclerViewOotd.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }
                    adapter = new AddPostAdapter(uriList, getApplicationContext());
                    binding.recyclerViewOotd.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    binding.recyclerViewOotd.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                    }
                }
            }
        }
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



    //이미지 서버에 저장
    private void addImage(ArrayList<Uri> data) throws IOException {

        RequestBody k = null;
        String ext = null;
        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        final MediaType MEDIA_TYPE_GIF = MediaType.parse("image/gif");
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

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
        Call<ApiResponse> call = RetrofitClient.getInstance(getContext()).getJsonPlaceHolderApi().uploadImages(files, requestBody);

        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("22성공");
                }else{
                    System.out.println("tldkfjdlsfj실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                t.printStackTrace();
                System.out.println("22에러");
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

    ////
    //데이터 서버에 저장
    private void addData(BoardFileVo boardFileVo)
    {
        Call<ApiResponse> call = RetrofitClient.getInstance(getApplicationContext()).getJsonPlaceHolderApi().uploadData(boardFileVo);

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
