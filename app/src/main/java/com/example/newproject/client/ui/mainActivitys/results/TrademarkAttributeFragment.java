package com.example.newproject.client.ui.mainActivitys.results;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.newproject.R;
import com.example.newproject.client.core.DirManager;
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

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrademarkAttributeFragment extends BaseFragment {

    private static final String TAG = "TrademarkAttributeFragm";

    TextView trademarkName;

    @BindView(R.id.image_view)
    ImageView trademarkView;

    @BindView(R.id.petitioner)
    TextView petitioner;

    @BindView(R.id.registered_address)
    TextView registeredAddress;

    @BindView(R.id.similar_id)
    TextView similarId;

    @BindView(R.id.time_of_application)
    TextView timeOfApplication;

    @BindView(R.id.use_time)
    TextView useTime;

    @BindView(R.id.trademark_description)
    TextView trademarkDescription;

    @BindView(R.id.trademark_type)
    TextView trademarkType;

    @BindView(R.id.trademark_name_trademark_attribute)
    LinearLayout trademarkNameLayout;

    @BindView(R.id.image_view_trademark_attribute)
    LinearLayout trademarkViewLayout;

    @BindView(R.id.petitioner_trademark_attribute)
    LinearLayout petitionerLayout;

    @BindView(R.id.registered_address_trademark_attribute)
    LinearLayout registeredAddressLayout;

    @BindView(R.id.similar_id_trademark_attribute)
    LinearLayout similarIdLayout;

    @BindView(R.id.time_of_application_trademark_attribute)
    LinearLayout timeOfApplicationLayout;

    @BindView(R.id.use_time_trademark_attribute)
    LinearLayout useTimeLayout;

    @BindView(R.id.trademark_description_trademark_attribute)
    LinearLayout trademarkDescriptionLayout;

    @BindView(R.id.trademark_type_trademark_attribute)
    LinearLayout trademarkTypeLayout;


    public TrademarkAttributeFragment() {
    }


    public static TrademarkAttributeFragment newInstance() {
        TrademarkAttributeFragment fragment = new TrademarkAttributeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trademark_attribute, container, false);
        Log.d(TAG, "onCreateView: 在这里，我们。。");
        trademarkName = view.findViewById(R.id.trademark_name);
        trademarkView = view.findViewById(R.id.image_view);
        petitioner = view.findViewById(R.id.petitioner);
        registeredAddress = view.findViewById(R.id.registered_address);
        similarId = view.findViewById(R.id.similar_id);
        timeOfApplication = view.findViewById(R.id.time_of_application);
        useTime = view.findViewById(R.id.use_time);
        trademarkDescription = view.findViewById(R.id.trademark_description);
        trademarkType = view.findViewById(R.id.trademark_type);
        trademarkNameLayout = view.findViewById(R.id.trademark_name_trademark_attribute);
        trademarkViewLayout = view.findViewById(R.id.image_view_trademark_attribute);
        petitionerLayout = view.findViewById(R.id.petitioner_trademark_attribute);
        registeredAddressLayout = view.findViewById(R.id.registered_address_trademark_attribute);
        similarIdLayout = view.findViewById(R.id.similar_id_trademark_attribute);
        timeOfApplicationLayout = view.findViewById(R.id.time_of_application_trademark_attribute);
        useTimeLayout = view.findViewById(R.id.use_time_trademark_attribute);
        trademarkDescriptionLayout = view.findViewById(R.id.trademark_description_trademark_attribute);
        trademarkTypeLayout = view.findViewById(R.id.trademark_type_trademark_attribute);
        return view;
    }

    public void update(Map<String, String> objMap) {
        init();
        List<String> trademarkNameList = new ArrayList<>();
        List<String> trademarkAttributeList = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, String> entry : objMap.entrySet()) {
            if (entry.getValue().equals(WordSegCommonConstant.TRADEMARK_NAME_WORD)) {
                trademarkNameList.add(entry.getKey());
                count++;
            }
            if (entry.getValue().equals(WordSegCommonConstant.TRADEMARK_ATTRIBUTE_WORD)) {
                trademarkAttributeList.add(entry.getKey());
            }
        }
        if (count > 1) {
            speak("暂不支持查找多个商标的商标，现在显示" + trademarkNameList.get(0) + "商标的信息");
        } else {
            speak("为您查找到了" + trademarkNameList.get(0) + "商标的信息");
        }
        trademarkName.setText(trademarkNameList.get(0));
        TrademarkListDomain trademarkListDomain = TrademarkList.getTrademarkListDomainByTrademarkName(trademarkNameList.get(0));

        if (trademarkListDomain == null) {
            return;
        }

        int trademarkId = trademarkListDomain.getTrademarkId();
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
                        getActivity().runOnUiThread(() -> {
                            if (objMap.containsKey("商标图形") || objMap.containsKey("商标图案") || objMap.containsKey("商标形状") || objMap.containsKey("图形") || objMap.containsKey("图像")) {
                                trademarkView.setImageBitmap(bitmap);
                            }
                        });
                    }
                });


            } else {
                if (objMap.containsKey("商标图形") || objMap.containsKey("商标图案") || objMap.containsKey("商标形状") || objMap.containsKey("图形") || objMap.containsKey("图像")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
                    trademarkView.setImageBitmap(bitmap);
                }
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
                        new Thread(() -> getActivity().runOnUiThread(() -> {
                            TrademarkDomain trademarkDomain1 = (TrademarkDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, TrademarkDomain.class);
                            setAttributes(objMap, trademarkDomain1);
                        })).start();
                    }
                });


            } else {
                getActivity().runOnUiThread(() -> setAttributes(objMap, trademarkDomain));

            }


        }).start();

    }

    private void setAttributes(Map<String, String> objMap, TrademarkDomain trademarkDomain) {
        for (String key : objMap.keySet()) {
            switch (key) {
                case "商标名称":
                case "商标名":
                    setAttribute(1, trademarkDomain.getTrademarkName());
                    break;
                case "申请日期":
                case "申请日":
                case "申请时间":
                    setAttribute(2, trademarkDomain.getTimeOfApplication());
                    break;
                case "申请人名称":
                case "申请者名称":
                case "申请人":
                    setAttribute(3, trademarkDomain.getPetitioner());
                    break;
                case "申请人地址":
                case "申请人的地址":
                case "申请人住址":
                case "申请人的住址":
                    setAttribute(4, trademarkDomain.getRegisteredAddress());
                    break;
                case "专用权":
                case "专用权期限":
                case "使用期限":
                case "使用时长":
                case "使用期间":
                    setAttribute(5, trademarkDomain.getUseTime());
                    break;
                case "国际分类":
                case "分类":
                case "类型":
                    setAttribute(6, Integer.toString(trademarkDomain.getTrademarkType()));
                    break;
                case "类似群":
                case "相似群":
                    setAttribute(7, trademarkDomain.getSimilarId());
                    break;
                case "简介":
                case "简单介绍":
                case "简要介绍":
                case "简要描述":
                case "描述":
                case "简单描述":
                case "相关描述":
                case "信息":
                case "介绍":
                    setAttribute(8, trademarkDomain.getBriefDescription());
                    break;
                default:
                    break;

            }
        }
    }

    private void setAttribute(int type, String text) {

        switch (type) {
            case 1:
//                trademarkNameLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
                timeOfApplicationLayout.setVisibility(View.VISIBLE);
                timeOfApplication.setText(text);
                break;
            case 3:
                petitionerLayout.setVisibility(View.VISIBLE);
                petitioner.setText(text);
                break;
            case 4:
                registeredAddressLayout.setVisibility(View.VISIBLE);
                registeredAddress.setText(text);
                break;
            case 5:
                useTimeLayout.setVisibility(View.VISIBLE);
                useTime.setText(text);
                break;
            case 6:
                trademarkTypeLayout.setVisibility(View.VISIBLE);
                trademarkType.setText(text);
                break;
            case 7:
                similarIdLayout.setVisibility(View.VISIBLE);
                similarId.setText(text);
                break;
            case 8:
                trademarkDescriptionLayout.setVisibility(View.VISIBLE);
                trademarkDescription.setText(text);
                break;
        }
    }

    private void init(){
//        trademarkNameLayout.setVisibility(View.GONE);
        petitionerLayout.setVisibility(View.GONE);
        registeredAddressLayout.setVisibility(View.GONE);
        similarIdLayout.setVisibility(View.GONE);
        timeOfApplicationLayout.setVisibility(View.GONE);
        trademarkDescriptionLayout.setVisibility(View.GONE);
        trademarkViewLayout.setVisibility(View.GONE);
        trademarkViewLayout.setVisibility(View.GONE);
        useTimeLayout.setVisibility(View.GONE);
        trademarkTypeLayout.setVisibility(View.GONE);
    }

}
