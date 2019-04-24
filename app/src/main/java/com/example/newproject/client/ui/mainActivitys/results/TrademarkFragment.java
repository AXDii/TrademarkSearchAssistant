package com.example.newproject.client.ui.mainActivitys.results;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newproject.R;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.trademarkInfoActivitys.TrademarkInfoActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.core.TrademarkList;
import com.example.newproject.web.core.TrademarkSet;
import com.example.newproject.web.domain.TrademarkDomain;
import com.example.newproject.web.domain.TrademarkListDomain;
import com.example.newproject.web.util.APICreator;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.WordSegCommonConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrademarkFragment extends BaseFragment{

    private static final String TAG = "TrademarkFragment";

    private static RecyclerView recyclerView;

    public TrademarkFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trademark, container, false);
        recyclerView = view.findViewById(R.id.trademark_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        return view;
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
            Glide.with(getActivity()).load(R.drawable.loading).into(trademarkImg);


        }

        public void bind(String trademarkNameStr) {


            TrademarkListDomain trademarkListDomain = TrademarkList.getTrademarkListDomainByTrademarkName(trademarkNameStr);

            if (trademarkListDomain == null) {
                return;
            }

            trademarkId = trademarkListDomain.getTrademarkId();
            String imgFilePath = DirManager.getFilePath(String.valueOf(trademarkId), CommonConstant.TRADEMARK_IMG_FILE, getActivity());
            Log.d(TAG, "init: " + imgFilePath);
            final File imgFile = new File(imgFilePath);
            new Thread(() -> {


                if (!imgFile.exists()) {


                    String imgFileApi = APICreator.createGetTrademarkImgApi(trademarkId);
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
                            getActivity().runOnUiThread(() -> trademarkImg.setImageBitmap(bitmap));
                        }
                    });


                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
                    getActivity().runOnUiThread(()->{
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
                            getActivity().runOnUiThread(() -> {
                                TrademarkDomain trademarkDomain1 = (TrademarkDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, TrademarkDomain.class);
                                trademarkName.setText(trademarkDomain1.getTrademarkName());
                                filingDate.setText(trademarkDomain1.getTimeOfApplication());
                                petitioner.setText(trademarkDomain1.getPetitioner());
                                useTime.setText(trademarkDomain1.getUseTime());
                            });
                        }
                    });


                } else {
                    getActivity().runOnUiThread(() -> {
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
            Intent intent = new Intent(getContext(), TrademarkInfoActivity.class);
            intent.putExtra("trademarkId", trademarkId);
            getActivity().startActivity(intent);
        }
    }

    private class TrademarkAdapter extends RecyclerView.Adapter<TrademarkHolder> {

        private List<String> trademarkNameList;

        public TrademarkAdapter(List<String> trademarkNameList) {
            this.trademarkNameList = trademarkNameList;
        }

        @NonNull
        @Override
        public TrademarkHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TrademarkHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull TrademarkHolder trademarkHolder, int i) {
            trademarkHolder.bind(trademarkNameList.get(i));
        }

        @Override
        public int getItemCount() {
            return trademarkNameList.size();
        }
    }


    public void update(Map<String, String> objMap) {
        List<String> trademarkNameList = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, String> entry : objMap.entrySet()) {
            if (entry.getValue().equals(WordSegCommonConstant.TRADEMARK_NAME_WORD)) {
                trademarkNameList.add(entry.getKey());
                count++;
            }
        }
        TrademarkAdapter trademarkAdapter = new TrademarkAdapter(trademarkNameList);

        if (recyclerView == null) {
            Log.d(TAG, "update: recyclerView is null!!!");
            return;
        }

        recyclerView.setAdapter(trademarkAdapter);
        speak("为您查找到" + count + "条商标信息");
    }

}
