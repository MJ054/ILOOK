package com.example.ilook.Fragments;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

   /* NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    HomeAdapter adapter;
    Bitmap bitmap;
    ProgressBar progressBar;

    ArrayList<Bitmap> dataArrayList = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();

    int page=1, limit=4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home, container, false);

        nestedScrollView = view.findViewById(R.id.home_scrollView);
        recyclerView = view.findViewById(R.id.recyclerView_ootd);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton_ootd);
        progressBar = view.findViewById(R.id.progress_bar_ootd);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //플로팅 버튼 클릭시
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });
        System.out.println("aa");;
        getData(page, limit);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {
                    Log.i(TAG, "Top of list");
                } else if (!recyclerView.canScrollVertically(1)) {
                    progressBar.setVisibility(View.VISIBLE);
                    getData(page, limit);
                } else {
                    Log.i(TAG, "idle");
                }
            }
        });

        return view;
    }

        ////선

    private void getData(int page, int limit)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<ApiResponse> call = jsonPlaceHolderApi.getMainImages("PICK",page,limit);
        System.out.println("aaaaaa");
        call.enqueue(new Callback<ApiResponse>()
        {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response)
            {System.out.println("aaaa");
                System.out.println(response);
                if (response.isSuccessful())
                {
                    System.out.println("aaa");
                    progressBar.setVisibility(View.GONE);
                    System.out.println("aaa1");
                    System.out.println(response.toString());
                    JSONArray jsonArray;
                    System.out.println(response.body().getData());
                    System.out.println("aaa2");
                    //System.out.println("data"+jsonArray);
                    // JsonObject jsonObject = new JsonObject(response.body().getData());;
                    ArrayList<JSONObject> array = (ArrayList) response.body().getData();
                    System.out.println(array);
                        System.out.println(array.get(0));
                    parseResult(array);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t)
            {
                System.out.println("에러");
            }
        });

    }

    private void parseResult(ArrayList<JSONObject> array)
    {
            Thread uThread = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < array.size(); i++) {

                            JSONObject jsonObject = new JSONObject((Map) array.get(i));
                            System.out.println(jsonObject.get("thumbnail"));
                            URL url = new URL(jsonObject.get("thumbnail").toString());

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                            conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)
                            InputStream is = conn.getInputStream(); //inputStream 값 가져오기

                            bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 반환
                            dataArrayList.add(bitmap);
                        }
                    } catch (MalformedURLException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            uThread.start(); // 작업 Thread 실행

            try{
                uThread.join();

                adapter = new HomeAdapter(dataArrayList2);
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }*/
    }

