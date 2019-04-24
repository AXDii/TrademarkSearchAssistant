package com.example.newproject.client.ui.mainActivitys.results;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.irecyclerview.IRecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newproject.R;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.mainActivitys.MainActivity;
import com.example.newproject.client.ui.mainActivitys.results.widget.footer.LoadMoreFooterView;
import com.example.newproject.client.ui.newsDetailActivity.NewsDetailActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.domain.NewsDomain;
import com.example.newproject.web.util.JSONStringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends BaseFragment {

    private static final String TAG = "NewsFragment";

    IRecyclerView recyclerView;
    private LoadMoreFooterView loadMoreFooterView;
    private List<NewsDomain.News> newsList;
    private RecyclerView.Adapter adapter;
    private MainActivity mainActivity;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = view.findViewById(R.id.news_recycler_view);
        init();
        return view;
    }

    private class NewsHolder extends RecyclerView.ViewHolder{

        private TextView newsTitle;
        private TextView newsFrom;
        private TextView newsTime;
        private ImageView newsFirstImg;
        private int newsId;
        private NewsDomain.News news;

        public NewsHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_news, parent, false));
            newsTitle = itemView.findViewById(R.id.list_item_news_newsTitle);
            newsFrom = itemView.findViewById(R.id.list_item_news_from);
            newsTime = itemView.findViewById(R.id.list_item_news_time);
            newsFirstImg = itemView.findViewById(R.id.list_item_news_image);

        }

        public void bind(NewsDomain.News news) {
            this.newsId = news.getNewsId();
            newsTitle.setText(news.getTitle());
            newsFrom.setText(news.getAuthor());
            newsTime.setText(news.getTime());
            this.news = news;

            Log.d(TAG, "bind: " + newsId);
            Log.d(TAG, "bind: " + news.getTitle());
            Log.d(TAG, "bind: " + news.getFirstImgUrl());

            String newsDetailPath = DirManager.getFilePath(String.valueOf(newsId), CommonConstant.NEWS_DETAIL_DIR_FILE, getContext());
            String imageFilePath = newsDetailPath+ "/image.jpg";
            File newsDetailFile = new File(newsDetailPath);
            File imageFile = new File(imageFilePath);
            if (!newsDetailFile.exists()) {

                //显示loading的gif图片
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                Glide.with(getContext()).load(R.drawable.loading).into(newsFirstImg);
                boolean sign = newsDetailFile.mkdir();

                if (sign) {
                    Log.d(TAG, "bind: 创建具体新闻文件夹成功");
                } else {
                    Log.d(TAG, "bind: 创建具体新闻文件夹失败");
                    return;
                }
                
                Request request = OkHttpsFactory.createRequest(news.getFirstImgUrl());
                OkHttpClient client = OkHttpsFactory.getOkHttpClient();

                if (newsFirstImg == null || newsFirstImg.equals("")) {
                    Log.d(TAG, "bind: 图片地址为空");
                    return;
                }

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 新闻获取简略图片失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream inputStream = client.newCall(request).execute().body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageFile.createNewFile();
                        //创建文件输出流对象用来向文件中写入数据
                        FileOutputStream out = new FileOutputStream(imageFile);
                        //将bitmap存储为jpg格式的图片
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();

                        new Thread(() -> getActivity().runOnUiThread(() -> newsFirstImg.setImageBitmap(bitmap))).start();
                    }
                });


            }else {

                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                newsFirstImg.setImageBitmap(bitmap);
                
            }


        }

    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

        private Context context;
        private List<NewsDomain.News> newsList;

        public NewsAdapter(Context context, List<NewsDomain.News> newsList) {
            this.context = context;
            this.newsList = newsList;
        }

        @NonNull
        @Override
        public NewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            NewsHolder newsHolder = new NewsHolder(layoutInflater, viewGroup);
            newsHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                intent.putExtra("newsId", newsHolder.news.getNewsId());
                intent.putExtra("author", newsHolder.news.getAuthor());
                intent.putExtra("time", newsHolder.news.getTime());
                intent.putExtra("title", newsHolder.news.getTitle());
                intent.putExtra("summaryContent", newsHolder.news.getSummaryContent());
                intent.putIntegerArrayListExtra("recommendIds", (ArrayList<Integer>) newsHolder.news.getRecommendNewsList());
                getActivity().startActivity(intent);
            });
            return newsHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder newsHolder, int i) {
            newsHolder.bind(newsList.get(i));
        }

        @Override
        public int getItemCount() {
            if (newsList == null) {
                return 0;
            }
            return newsList.size();
        }
    }

    public void initRecyclerView(){

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        loadMoreFooterView = (LoadMoreFooterView)recyclerView.getLoadMoreFooterView();

    }

    public void initData(){

        Log.d(TAG, "initData: 1");
        if (newsList == null) {
            newsList = new ArrayList<>();
            Log.d(TAG, "initData: 2");
        } else {
            update();
            Log.d(TAG, "initData: 3");
            return;
        }
        mainActivity = (MainActivity) getActivity();
        String getTopTenSimpleNewsApi = CommonConstant.API_GET_TOP_TEN_SIMPLE_NEWS;
        Request request = OkHttpsFactory.createRequest(getTopTenSimpleNewsApi);
        OkHttpClient client = OkHttpsFactory.getOkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "获取新闻失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonStr = response.body().string();
                Log.d(TAG, "onResponse: " + jsonStr);
                NewsDomain newsDomain = (NewsDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, NewsDomain.class);
                if (newsDomain == null || !newsDomain.getStatusCode().equals(CommonConstant.OPERATE_SUCCESS)) {
                    Toast.makeText(getActivity(), "获取新闻失败", Toast.LENGTH_LONG).show();
                    return;
                }
                newsList.addAll(newsDomain.getNewsList());
                update();


                Log.d(TAG, "onResponse: 获取到新闻");
            }
        });
    }

    public void update(){
        new Thread(()->{
            getActivity().runOnUiThread(()->{

                if (newsList == null) {
                    Log.d(TAG, "update: 哈哈哈");
                }
                
                if (adapter == null) {
                    Log.d(TAG, "update: ???");
                    adapter = new NewsAdapter(getActivity(), newsList);
                    recyclerView.setIAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();

                    //处理退出界面失效的情况
                    if (recyclerView.getAdapter() == null) {
                        recyclerView.setIAdapter(adapter);
                    }

                }

            });
        }).start();

    }


    public void init() {

        initRecyclerView();//初始化recyclerView
        initListener();//初始化监听器
        initData();//初始化数据

    }

    public void initListener(){

        recyclerView.setOnRefreshListener(()->{
            recyclerView.setRefreshing(true);
            //发送向上更新的东西

            NewsDomain requestDomain = new NewsDomain();
            requestDomain.setOldId(newsList.get(0).getNewsId());
            Request request = OkHttpsFactory.createRequest(CommonConstant.API_GET_LAST_NEWS_FROM_OLD_NEWS, requestDomain);
            OkHttpClient client = OkHttpsFactory.getOkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: 获取新闻失败");
                    recyclerView.setRefreshing(false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    Log.d(TAG, "onResponse: " + jsonStr);
                    NewsDomain newsDomain = (NewsDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, NewsDomain.class);
                    if (newsDomain == null || !newsDomain.getStatusCode().equals(CommonConstant.OPERATE_SUCCESS)) {
                        Log.d(TAG, "onResponse: 获取新闻失败！！");
                        recyclerView.setRefreshing(false);
                        return;
                    }

                    if (newsDomain.getNewsList().size() == 0) {
                        Log.d(TAG, "onResponse: 暂无更多新闻");

                        new Thread(()->{
                            getActivity().runOnUiThread(()->{
                                recyclerView.setRefreshing(false);
                            });
                        }).start();
                        return;
                    }

                    List<NewsDomain.News> list = newsDomain.getNewsList();
                    int count = 0;
                    for (NewsDomain.News news : list) {
                        if (!newsList.contains(news)) {
                            newsList.add(count++, news);
                        }
                    }

                    update();
                    recyclerView.setRefreshing(false);

                    Log.d(TAG, "onResponse: 获取到新闻：" + count + "条");
                }
            });


        });

        recyclerView.setOnLoadMoreListener(()->{
            Log.d(TAG, "上拉加载：initListener: ");
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);

            NewsDomain requestDomain = new NewsDomain();
            requestDomain.setOldId(newsList.get(newsList.size() - 1).getNewsId());
            Request request = OkHttpsFactory.createRequest(CommonConstant.API_GET_OLD_NEWS_fROM_OLD_NEWS, requestDomain);
            OkHttpClient client = OkHttpsFactory.getOkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(getActivity(), "获取新闻失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    Log.d(TAG, "onResponse:往上拉加载的数据 " + jsonStr);
                    NewsDomain newsDomain = (NewsDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, NewsDomain.class);
                    if (newsDomain == null || !newsDomain.getStatusCode().equals(CommonConstant.OPERATE_SUCCESS)) {
                        Log.d(TAG, "onResponse: 暂无更多数据");
                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                        return;
                    }
                    List<NewsDomain.News> list = newsDomain.getNewsList();

                    if (list.size() == 0) {
                        Log.d(TAG, "onResponse: 没有加载更新新闻");
                        new Thread(()->{
                            getActivity().runOnUiThread(()->{
                                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                            });
                        }).start();
                        return;
                    }

                    int count = 0;

                    for (int i = list.size() - 1; i >= 0; i--) {
                        newsList.add(newsList.size(), list.get(i));
                    }

                    update();
                    new Thread(()->{
                        getActivity().runOnUiThread(()->{
                            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                        });
                    }).start();

                    Log.d(TAG, "onResponse: 获取到新闻：" + count + "条");
                }
            });



        });

//        recyclerView.post(()->{
//            recyclerView.setRefreshing(true);
//        });
//        recyclerView.setRefreshing(false);
    }


}
