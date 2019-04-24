package com.example.newproject.client.ui.trademarkInfoActivitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newproject.R;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.core.TrademarkSet;
import com.example.newproject.web.domain.TrademarkDomain;
import com.example.newproject.web.util.APICreator;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.MessageFactory;
import com.example.newproject.web.util.MyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrademarkInfoActivity extends BaseActivity {

    private static final String TAG = "TrademarkInfoActivity";

    @BindView(R.id.image_name) TextView mTextview;

    @BindView(R.id.image_view) ImageView mImageView;

    @BindView(R.id.petitioner_trademark_info) TextView petitioner;

    @BindView(R.id.registered_address_trademark_info) TextView registeredAddress;

    @BindView(R.id.similar_id_trademark_info) TextView similarId;

    @BindView(R.id.time_of_application_trademark_info) TextView timeOfApplication;

    @BindView(R.id.use_time_trademark_info) TextView useTime;

    @BindView(R.id.trademark_description) TextView trademarkDescription;

    @BindView(R.id.trademark_type_trademark_info) TextView trademarkType;

    @BindView(R.id.number_of_image_trademark_info) TextView numberOfImage;

    private MyHandler handler;


    static class MyHandler extends Handler {

        WeakReference<Activity> weakReference;


        public MyHandler(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            TrademarkInfoActivity trademarkInfoActivity = (TrademarkInfoActivity) weakReference.get();

            switch (msg.what) {
                case 1:
                    String imgFilePath = data.getString("imgFilePath");
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
                    trademarkInfoActivity.mImageView.setImageBitmap(bitmap);

                    break;
                case 2:

                    String jsonString = data.getString(CommonConstant.JSON_STRING);
                    Log.d(TAG, "handleMessage: " + jsonString);

                    TrademarkDomain returnTrademarkDomain = (TrademarkDomain) JSONStringUtil.getObjectFromJSONString(jsonString, TrademarkDomain.class);
                    Context context = weakReference.get();
                    MyToast myToast = new MyToast(context);

                    Log.d(TAG, "handleMessage: " + returnTrademarkDomain.getTrademarkType());
                    
                    TrademarkSet.addTrademarkDomain(returnTrademarkDomain);


                    trademarkInfoActivity.mTextview.setText(returnTrademarkDomain.getTrademarkName());
                    trademarkInfoActivity.petitioner.setText(returnTrademarkDomain.getPetitioner());
                    trademarkInfoActivity.registeredAddress.setText(returnTrademarkDomain.getRegisteredAddress());
                    trademarkInfoActivity.similarId.setText(returnTrademarkDomain.getSimilarId());
                    trademarkInfoActivity.timeOfApplication.setText(returnTrademarkDomain.getTimeOfApplication());
                    trademarkInfoActivity.useTime.setText(returnTrademarkDomain.getUseTime());
                    trademarkInfoActivity.trademarkDescription.setText(returnTrademarkDomain.getBriefDescription());
                    trademarkInfoActivity.trademarkType.setText(String.valueOf(returnTrademarkDomain.getTrademarkType()));
                    trademarkInfoActivity.numberOfImage.setText(String.valueOf(returnTrademarkDomain.getTrademarkId()));


                    break;
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trademark_info);
        init();
    }

    public void init(){

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //显示loading的gif图片
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(this).load(R.drawable.loading).into(mImageView);

        handler = new MyHandler(this);
        Intent intent = getIntent();
        final int trademarkId = intent.getIntExtra("trademarkId",1);
        String imgFilePath = DirManager.getFilePath(String.valueOf(trademarkId), CommonConstant.TRADEMARK_IMG_FILE, this);
        Log.d(TAG, "init: " + imgFilePath);
        final File imgFile = new File(imgFilePath);

        //如果图像文件不存在
        if (!imgFile.exists()) {
            String imgFileApi = APICreator.createGetTrademarkImgApi(trademarkId);
            Log.d(TAG, "init: " + imgFileApi);
            Request request = OkHttpsFactory.createRequest(imgFileApi);
            OkHttpClient client = OkHttpsFactory.getOkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(TrademarkInfoActivity.this, "获取图片失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream=response.body().byteStream();
                    //将输入流数据转化为Bitmap位图数据
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    imgFile.createNewFile();
                    //创建文件输出流对象用来向文件中写入数据
                    FileOutputStream out=new FileOutputStream(imgFile);
                    //将bitmap存储为jpg格式的图片
                    if (bitmap == null) {
                        Log.d(TAG, "onResponse: 关注这里！！！！！！1");
                        return;
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                    //刷新文件流
                    out.flush();
                    out.close();

                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("imgFilePath", imgFile.getPath());
                    message.setData(data);
                    message.what = 1;
                    handler.sendMessage(message);

                }
            });
        } else {
            Log.d(TAG, "init: 图片已存在");
            Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
            mImageView.setImageBitmap(bitmap);
        }

        TrademarkDomain trademarkDomain = TrademarkSet.getTrademarkDomainById(trademarkId);
        
        if (trademarkDomain == null) {
            trademarkDomain = new TrademarkDomain();
            trademarkDomain.setTrademarkId(trademarkId);

            Request request = OkHttpsFactory.createRequest(CommonConstant.API_GET_TRADEMARK_BY_ID, trademarkDomain);
            OkHttpClient client = OkHttpsFactory.getOkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(TrademarkInfoActivity.this, "获取商标信息错误", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseDate = response.body().string();
                    TrademarkDomain returnTrademarkDomain = (TrademarkDomain) JSONStringUtil.getObjectFromJSONString(responseDate, TrademarkDomain.class);
                    Message message = MessageFactory.createMessageFromObject(returnTrademarkDomain);
                    message.what = 2;
                    handler.sendMessage(message);

                }
            });
        } else {
            mTextview.setText(trademarkDomain.getTrademarkName());
            petitioner.setText(trademarkDomain.getPetitioner());
            registeredAddress.setText(trademarkDomain.getRegisteredAddress());
            similarId.setText(trademarkDomain.getSimilarId());
            timeOfApplication.setText(trademarkDomain.getTimeOfApplication());
            useTime.setText(trademarkDomain.getUseTime());
            trademarkType.setText(String.valueOf(trademarkDomain.getTrademarkType()));
            trademarkDescription.setText(trademarkDomain.getBriefDescription());
            numberOfImage.setText(String.valueOf(trademarkDomain.getTrademarkId()));
        }
    }

}
