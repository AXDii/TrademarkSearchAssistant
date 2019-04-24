package com.example.newproject.client.ui.newsDetailActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newproject.R;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.domain.NewsDomain;
import com.example.newproject.web.util.JSONStringUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsDetailActivity extends BaseActivity {

    private static final String TAG = "NewsDetailActivity";

    private String id = null;
    private View view;
    @BindView(R.id.news_content)
    WebView webView;
    @BindView(R.id.news_detail_recycler_view)
    RecyclerView recyclerView;
    String author;
    String time;
    String title;
    String summaryContent;
    List<Integer> recommendIds;
    List<NewsDomain.News> newsList;
    NewsAdapter adapter;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//
            loadWebView(msg.getData().getString("contentPath"));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        init();
    }

    public void init() {

        ButterKnife.bind(this);
        Intent intent = getIntent();
        int newsId = intent.getIntExtra("newsId", -1);
        author = intent.getStringExtra("author");
        time = intent.getStringExtra("time");
        title = intent.getStringExtra("title");
        summaryContent = intent.getStringExtra("summaryContent");
        recommendIds = intent.getIntegerArrayListExtra("recommendIds");

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        NewsDomain recommendNewsDomain = new NewsDomain();
        recommendNewsDomain.setRequestIds(recommendIds);
        Request requestTmp = OkHttpsFactory.createRequest(CommonConstant.API_GET_NEWS_BY_IDS, recommendNewsDomain);
        OkHttpClient clientTmp = OkHttpsFactory.getOkHttpClient();
        clientTmp.newCall(requestTmp).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 获取数据失败！！！！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String jsonStr = response.body().string();
                Log.d(TAG, "onResponse: " + jsonStr);
                NewsDomain returnNewsDomain = (NewsDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, NewsDomain.class);
                if (returnNewsDomain == null || !returnNewsDomain.getStatusCode().equals(CommonConstant.OPERATE_SUCCESS)) {
                    Log.d(TAG, "onResponse: 获取新闻失败");
                    return;
                }
                newsList = returnNewsDomain.getNewsList();
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        update();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });


        String contentPath = DirManager.getFilePath(String.valueOf(newsId), CommonConstant.NEWS_DETAIL_DIR_CONTENT_FILE, this);
        File contentFile = new File(contentPath);
        if (!contentFile.exists()) {

            NewsDomain newsDomain = new NewsDomain();
            newsDomain.setOldId(newsId);
            Request request = OkHttpsFactory.createRequest(CommonConstant.API_GET_NEWS_BY_ID, newsDomain);
            OkHttpClient client = OkHttpsFactory.getOkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: 获取新闻文本失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    NewsDomain returnNewsDomain = (NewsDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, NewsDomain.class);
                    String content = returnNewsDomain.getNewsList().get(0).getContent();
//                    Log.d(TAG, "onResponse: " + content);
                    if (content == null || content.equals("")) {
                        Log.d(TAG, "onResponse: 新闻文本为空");
                        return;
                    }

                    BufferedReader bufferedReader = null;
                    BufferedWriter bufferedWriter = null;

                    try {

                        bufferedReader = new BufferedReader(new StringReader(content));
                        bufferedWriter = new BufferedWriter(new FileWriter(contentFile));
                        char buf[] = new char[1024];         //字符缓冲区
                        int len;
                        while ((len = bufferedReader.read(buf)) != -1) {
                            bufferedWriter.write(buf, 0, len);
                        }
                        bufferedWriter.flush();
                        bufferedReader.close();
                        bufferedWriter.close();

//                        loadWebView(contentPath);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("contentPath", contentPath);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: 新闻文本转文件失败");
                    } finally {
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            });


        } else {

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("contentPath", contentPath);
            message.setData(bundle);
            handler.sendMessage(message);
        }

    }

    private void loadWebView(String path) {
        String data = getContentPath(path);
        data = replace(data);
        System.out.println(data);
        webView.loadDataWithBaseURL("http://webhost.net", data, "text/html", "UTF-8", null);
    }

    private String getContentPath(String contentPath) {


        try {
            return textFileToString(contentPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String replace(String data) {
        int low = 0;
        int pick = 0;
        String insert_str = "  width=\"100%\"  \"height=\\\"auto\\\"\" ";
        while ((low = data.indexOf("<img", low)) != -1) {
            pick = data.indexOf(">", low);
            data = data.substring(0, pick) + insert_str + data.substring(pick, data.length());
            low = pick;
        }
        data = setHeader() + data;
        return data;
    }

    private String setHeader() {
        String[] info = loadInfo();
        String css = "<style type=\"text/css\">\n" +
                "span{font-size: 16px;}\n" +
                "</style>\n";
        return css +
                "<p><span style=\"font-size: 26px;\"><strong>" + info[0] + "</strong></span></p>" +
                "<p><span style=\"font-color: #bbb;\">作者：" + info[1] + "\t\t<strong>·</strong>\t\t" + info[2] + "</span></p><br/>";
    }

    private String[] loadInfo() {
        return new String[]{title, author, time};
    }

    public String textFileToString(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        //将file文件内容转成字符串
        BufferedReader bf = new BufferedReader(isr);

        String content = "";
        StringBuilder sb = new StringBuilder();
        while (true) {
            content = bf.readLine();
            if (content == null) {
                break;
            }
            sb.append(content).append("\n");
        }
        bf.close();
        return sb.toString();
    }

    private class NewsHolder extends RecyclerView.ViewHolder {

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

            String newsDetailPath = DirManager.getFilePath(String.valueOf(newsId), CommonConstant.NEWS_DETAIL_DIR_FILE, NewsDetailActivity.this);
            String imageFilePath = newsDetailPath + "/image.jpg";
            File newsDetailFile = new File(newsDetailPath);
            File imageFile = new File(imageFilePath);
            if (!newsDetailFile.exists()) {

                //显示loading的gif图片
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                Glide.with(NewsDetailActivity.this).load(R.drawable.loading).into(newsFirstImg);
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

                        new Thread(() -> runOnUiThread(() -> newsFirstImg.setImageBitmap(bitmap))).start();
                    }
                });


            } else {

                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                newsFirstImg.setImageBitmap(bitmap);

            }


        }

    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsDetailActivity.NewsHolder> {

        private Context context;
        private List<NewsDomain.News> newsList;

        public NewsAdapter(Context context, List<NewsDomain.News> newsList) {
            this.context = context;
            this.newsList = newsList;
        }

        @NonNull
        @Override
        public NewsDetailActivity.NewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(NewsDetailActivity.this);
            NewsDetailActivity.NewsHolder newsHolder = new NewsDetailActivity.NewsHolder(layoutInflater, viewGroup);
            newsHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(NewsDetailActivity.this, NewsDetailActivity.class);
                intent.putExtra("newsId", newsHolder.news.getNewsId());
                intent.putExtra("author", newsHolder.news.getAuthor());
                intent.putExtra("time", newsHolder.news.getTime());
                intent.putExtra("title", newsHolder.news.getTitle());
                intent.putExtra("summaryContent", newsHolder.news.getSummaryContent());
                intent.putIntegerArrayListExtra("recommendIds", (ArrayList<Integer>) newsHolder.news.getRecommendNewsList());
                startActivity(intent);
                Log.d(TAG, "onClick: 点击");
            });
            return newsHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull NewsDetailActivity.NewsHolder newsHolder, int i) {
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

    public void update() {

        runOnUiThread(() -> {

            if (adapter == null) {
                adapter = new NewsAdapter(this, newsList);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }
    }
}
