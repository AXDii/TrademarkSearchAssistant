package com.example.newproject.client.ui.similarTrademarkEnquiries;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newproject.R;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.trademarkInfoActivitys.TrademarkInfoActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.core.TrademarkSet;
import com.example.newproject.web.domain.SimilarTrademarkEnquiriesDomain;
import com.example.newproject.web.domain.TrademarkDomain;
import com.example.newproject.web.util.APICreator;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.MyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SimilarTrademarkEnquiriesActivity extends BaseActivity {

    private static final String TAG = "SimilarTrademarkEnquiri";

    private TrademarkAdapter adapter;

    private final int TEXT_TYPE = 1;
    private final int IMAGE_TYPE = 2;


    @BindView(R.id.trademark_type_similar_enquiries)
    EditText trademarkTypeEdit;

    @BindView(R.id.text_similar_enquiries)
    RadioButton textEnquiriesRadio;

    @BindView(R.id.image_similar_enquiries)
    RadioButton imageEnquiriesRadio;

    @BindView(R.id.enquiries_type_similar_enquiries)
    TextView enquiriesTypeText;

    @BindView(R.id.enquiries_value_similar_enquiries)
    EditText enquiriesTypeEdit;

    @BindView(R.id.search_button_similar_enquiries)
    Button searchButton;

    @BindView(R.id.radio_group_similar_enquiries)
    RadioGroup enquiriesTypeRadioGroup;

    @BindView(R.id.similar_trademark_enquiries_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MyToast myToast;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_trademark_enquiries);
        ButterKnife.bind(this);
        init();

    }

    public void init(){

        initView();
        initListener();

    }

    public void initView(){

        setSupportActionBar(toolbar);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        myToast = new MyToast(this);

    }


    public void initListener(){
        
        imageEnquiriesRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked){
                changeSearchType(IMAGE_TYPE);
            }

        });

        textEnquiriesRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                changeSearchType(TEXT_TYPE);
            }
        });


    }

    public void changeSearchType(int searchType){
        runOnUiThread(()->{
            switch (searchType) {
                case TEXT_TYPE:
                    enquiriesTypeText.setText("商标名称");
                    enquiriesTypeEdit.setHint("请输入1-20个字符");
                    break;
                case IMAGE_TYPE:
                    enquiriesTypeText.setText("图形编码");
                    enquiriesTypeEdit.setHint("请输入图像编码");
                    break;
            }
        });
    }


    @OnClick(R.id.search_button_similar_enquiries)
    public void search(){

        if (!judgeFormat()) {
            return;
        }

        SimilarTrademarkEnquiriesDomain similarTrademarkEnquiriesDomain = new SimilarTrademarkEnquiriesDomain();
        int trademarkType = Integer.parseInt(trademarkTypeEdit.getText().toString().trim());
        similarTrademarkEnquiriesDomain.setTrademarkType(trademarkType);
        Request request;

        if (textEnquiriesRadio.isChecked()) {

            String trademarkName = enquiriesTypeEdit.getText().toString().trim();
            similarTrademarkEnquiriesDomain.setTrademarkName(trademarkName);
            request = OkHttpsFactory.createRequest(CommonConstant.API_TEXT_SIMILAR_ENQUIRIES, similarTrademarkEnquiriesDomain);

        } else if (imageEnquiriesRadio.isChecked()) {

            int trademarkId = Integer.parseInt(enquiriesTypeEdit.getText().toString().trim());
            similarTrademarkEnquiriesDomain.setTrademarkId(trademarkId);
            request = OkHttpsFactory.createRequest(CommonConstant.API_IMAGE_SIMILAR_ENQUIRIES, similarTrademarkEnquiriesDomain);

        } else {
            return;
        }

        OkHttpClient client = OkHttpsFactory.getOkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 获取相似商标失败！！！！！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String jsonStr = response.body().string();

                Log.d(TAG, "onResponse: " + jsonStr);

                SimilarTrademarkEnquiriesDomain returnDomain = (SimilarTrademarkEnquiriesDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, SimilarTrademarkEnquiriesDomain.class);
                if (returnDomain.getReturnTrademarkIds().size() == 0) {
                    Log.d(TAG, "onResponse: 查不到相应数据");
                } else {
                    update(returnDomain.getReturnTrademarkIds());
                }


            }
        });


    }

    public boolean judgeFormat() {

        if (trademarkTypeEdit.getText().toString().trim().equals("") || enquiriesTypeEdit.getText().toString().trim().equals("")) {
            myToast.showShortTime("搜索条件文本框不能为空");
            return false;
        }

        int trademarkType = parseInt(trademarkTypeEdit.getText().toString().trim(), -1);

        if (trademarkType > 42 || trademarkType < 1) {
            myToast.showShortTime("国际类型须为1-42之间的正整数");
            return false;
        }

        if (imageEnquiriesRadio.isChecked() && parseInt(enquiriesTypeEdit.getText().toString().trim(), -1) < 0) {
            myToast.showShortTime("图像编码须为大于等于0的整数");
            return false;
        }

        return true;
    }

    public int parseInt(String str, int defaultNumber) {
        try{
            return Integer.parseInt(str);
        }catch (NumberFormatException e) {
            return -1;
        }
    }

    private class TrademarkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView trademarkImg;
        private TextView trademarkName;
        private TextView petitioner;
        private TextView filingDate;
        private TextView useTime;
        private int trademarkId;


        public TrademarkHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_trademark, parent, false));
            itemView.setOnClickListener(this);
            trademarkImg = (ImageView) itemView.findViewById(R.id.list_item_image_view);
            trademarkName = (TextView) itemView.findViewById(R.id.list_item_image_name);
            filingDate = (TextView) itemView.findViewById(R.id.list_item_filing_date);
            petitioner = (TextView) itemView.findViewById(R.id.list_item_petitioner);
            useTime = (TextView) itemView.findViewById(R.id.list_item_use_time);

            //显示loading的gif图片
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(SimilarTrademarkEnquiriesActivity.this).load(R.drawable.loading).into(trademarkImg);

        }

        public void bind(int trademarkId) {

            String imgFilePath = DirManager.getFilePath(String.valueOf(trademarkId), CommonConstant.TRADEMARK_TB_IMG_FILE, SimilarTrademarkEnquiriesActivity.this);
            Log.d(TAG, "init: " + imgFilePath);
            this.trademarkId = trademarkId;
            final File imgFile = new File(imgFilePath);
            new Thread(() -> {


                if (!imgFile.exists()) {


                    String imgFileApi = APICreator.createGetTrademarkTbImgApi(trademarkId);
                    Request request = OkHttpsFactory.createRequest(imgFileApi);
                    OkHttpClient client = OkHttpsFactory.getOkHttpClient();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: 获取图片数据失败");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            InputStream inputStream = client.newCall(request).execute().body().byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgFile.createNewFile();
                            //创建文件输出流对象用来向文件中写入数据
                            FileOutputStream out = new FileOutputStream(imgFile);
                            //将bitmap存储为jpg格式的图片
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            new Thread(() -> runOnUiThread(() -> trademarkImg.setImageBitmap(bitmap))).start();
                        }
                    });


                } else {

                    runOnUiThread(()->{
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
                        trademarkImg.setImageBitmap(bitmap);
                    });


                }

                final TrademarkDomain trademarkDomain = TrademarkSet.getTrademarkDomainById(trademarkId);
                if (trademarkDomain == null) {
                    TrademarkDomain tmp = new TrademarkDomain();
                    tmp.setTrademarkId(trademarkId);
                    Request request = OkHttpsFactory.createRequest(CommonConstant.API_GET_TRADEMARK_BY_ID, tmp);
                    OkHttpClient client = OkHttpsFactory.getOkHttpClient();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: 获取商标信息失败");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String jsonStr = response.body().string();
                            new Thread(() -> runOnUiThread(() -> {
                                TrademarkDomain trademarkDomain1 = (TrademarkDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, TrademarkDomain.class);
                                trademarkName.setText(trademarkDomain1.getTrademarkName());
                                filingDate.setText(trademarkDomain1.getTimeOfApplication());
                                petitioner.setText(trademarkDomain1.getPetitioner());
                                useTime.setText(trademarkDomain1.getUseTime());
                            })).start();
                        }
                    });


                } else {
                    runOnUiThread(() -> {
                        trademarkName.setText(trademarkDomain.getTrademarkName());
                        filingDate.setText(trademarkDomain.getTimeOfApplication());
                        petitioner.setText(trademarkDomain.getPetitioner());
                        useTime.setText(trademarkDomain.getUseTime());
                    });
                }


            }).start();
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SimilarTrademarkEnquiriesActivity.this, TrademarkInfoActivity.class);
            intent.putExtra("trademarkId", trademarkId);
            SimilarTrademarkEnquiriesActivity.this.startActivity(intent);
        }
    }

    private class TrademarkAdapter extends RecyclerView.Adapter<TrademarkHolder> {

        private List<Integer> trademarkIds;

        public TrademarkAdapter(List<Integer> trademarkIds) {
            this.trademarkIds = trademarkIds;
        }

        @NonNull
        @Override
        public TrademarkHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(SimilarTrademarkEnquiriesActivity.this);
            return new TrademarkHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull TrademarkHolder trademarkHolder, int i) {
            trademarkHolder.bind(trademarkIds.get(i));
        }

        @Override
        public int getItemCount() {
            return trademarkIds.size();
        }
    }

    public void update(List<Integer> trademarkIds){

        runOnUiThread(()->{
            adapter = new TrademarkAdapter(trademarkIds);
            recyclerView.setAdapter(adapter);
        });

    }

}
