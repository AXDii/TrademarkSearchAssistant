package com.example.newproject.client.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.newproject.R;
import com.example.newproject.client.baiduASR.ScreenSizeUtils;
import com.example.newproject.client.baiduASR.asrfinishjson.AsrFinishJsonData;
import com.example.newproject.client.baiduASR.asrpartialjson.AsrPartialJsonData;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.mainActivitys.MainActivity;
import com.example.newproject.client.ui.settingActivitys.settings.AccountAndSecurityActivity;
import com.example.newproject.client.ui.settingActivitys.settings.CleanActivity;
import com.example.newproject.client.ui.settingActivitys.settings.FeedbackActivity;
import com.example.newproject.client.ui.settingActivitys.settings.FunctionalIntroductionActivity;
import com.example.newproject.client.ui.similarTrademarkEnquiries.SimilarTrademarkEnquiriesActivity;
import com.example.newproject.client.ui.loginActivity.LoginActivity;
import com.example.newproject.client.ui.optionsActivitys.OptionsActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.FilesActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.FlowChartActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.GoodsAndServiceActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.GraphicElementActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.GuideActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.RatesActivity;
import com.example.newproject.client.ui.registerActivitys.RegisterActivity;
import com.example.newproject.client.ui.searchActivitys.SearchActivity;
import com.example.newproject.client.ui.settingActivitys.SettingActivity;
import com.example.newproject.client.ui.trademarkSetActivitys.TrademarkActivity;
import com.example.newproject.client.util.TTS;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.core.UserInfo;
import com.example.newproject.web.domain.SegmentationDomain;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.MessageFactory;
import com.example.newproject.web.util.MyToast;
import com.example.newproject.web.util.WordSegCommonConstant;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseActivity extends AppCompatActivity implements EventListener {

    private static final String TAG = "BaseActivity";
    protected MyToast toast;
    private EventManager wakeup;
    private EventManager asr;
    private String final_result;
    protected Dialog dialog;
    private TextView dialogContent;
    private MyHandler myHandler;
    private LineWaveVoiceView lineWaveVoiceView;

    static class MyHandler extends Handler {

        WeakReference<Activity> weakReference;

        MyHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String jsonString = data.getString(CommonConstant.JSON_STRING);
            SegmentationDomain segmentationDomain = (SegmentationDomain) JSONStringUtil.getObjectFromJSONString(jsonString, SegmentationDomain.class);
            Context context = weakReference.get();
            BaseActivity baseActivity = (BaseActivity) context;

            switch (segmentationDomain.getStatusCode()) {
                case CommonConstant.OPERATE_SUCCESS:
                    Log.d(TAG, "handleMessage: 分词结果“：" + segmentationDomain.getParsedText());
                    baseActivity.deal(segmentationDomain);
                    break;
                default:
                    baseActivity.speak("网路出错");
                    break;
            }
        }
    }

    //联网处理方法
    protected void deal(SegmentationDomain segmentationDomain){
        SegmentationDomain.Results results = segmentationDomain.getResults();
        String domain = results.getDomain();
        if (domain != null && !domain.equals("") && !domain.equals(WordSegCommonConstant.MESS_DOMAIN) && !domain.equals(WordSegCommonConstant.ERROR_DOMAIN)) {
            pageSwitching(MainActivity.class, null);
            MainActivity.MainActivityManager.getMainActivity().dealByOthersActivity(segmentationDomain);
            return;
        }

        speak("不好意思，未能找到相应的结果");

    };

    //结束语音识别时执行的方法
    protected void endOfAsr(){}

    //开始语音识别时执行的方法
    protected void beginOfAsr(){}

    protected boolean detailSimpleDeal(){
        return false;
    }


    public void myStartActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAsrAndWakeUp();
    }

    //初始化语音识别及唤醒
    protected void initAsrAndWakeUp() {
        toast = new MyToast(this);
        myHandler = new MyHandler(this);
//        initPermission();
        initWakeup();
        initAsr();
        openWakeup();
    }


    //初始化语音模块
    protected void initAsr() {
        //百度语音模块
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
    }

    //相应事件
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }

        if (name.equals("wp.ready")) {
//            toast.showShortTime("开启语音唤醒");
            return;
        }

        if (name.equals("wp.data")) {
//            toast.showShortTime("开启语音识别");
            openAsrAndStopWakeup();

            return;
        }

        String result = "";

        if (length > 0 && data.length > 0) {
            result += ", 语义解析结果：" + new String(data, offset, length);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            Log.d(TAG, "onEvent: 引擎准备就绪，可以开始说话");
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            Log.d(TAG, "onEvent: 检测到用户的已经开始说话");
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            Log.d(TAG, "onEvent: 检测到用户的已经停止说话");

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 临时识别结果, 长语音模式需要从此消息中取出结果

            Log.d(TAG, "onEvent: 识别临时识别结果");
            Log.d(TAG, "Temp Params:" + params);
            new Thread(() -> runOnUiThread(() -> {
                Gson gson = new Gson();
                AsrPartialJsonData jsonData = gson.fromJson(params, AsrPartialJsonData.class);
                Log.d(TAG, "onEvent: " + jsonData.getBest_result());

                if (dialogContent != null) {
                    dialogContent.setText(jsonData.getBest_result());
                }

            })).start();

            parseAsrPartialJsonData(params);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束， 最终识别结果或可能的错误
            result += "识别结束";
            endOfAsr();

            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            Log.d(TAG, "Result Params:" + params);
            parseAsrFinishJsonData(params);
        }


        Log.d(TAG, "onEvent: 识别到" + logTxt);

    }

    //初始化唤醒
    protected void initWakeup() {
        // 基于SDK唤醒词集成1.1 初始化EventManager
        wakeup = EventManagerFactory.create(this, "wp");
        // 基于SDK唤醒词集成1.3 注册输出事件
        wakeup.registerListener(this); //  EventListener 中 onEvent方法
    }

    //开启唤醒
    protected void openWakeup() {

        Log.d(TAG, "start: 开启语音唤醒");
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp_xiao_bai.bin");
        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        wakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);

    }


    //停止唤醒
    protected void stopWakeup() {
        wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
    }



    //开启语音识别
    protected void openAsr() {

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START;
        params.put(SpeechConstant.PID, 1536); // 默认1536
        params.put(SpeechConstant.DECODER, 0); // 纯在线(默认)
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN); // 语音活动检测
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 1000); // 不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。建议设置800ms-3000ms

        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, false);// 是否需要语音音频数据回调
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);// 是否需要语音音量数据回调

        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);

    }

    //停止语音识别
    protected void stopAsr() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }


    //调用语音合成函数
    public void speak(String str) {
        TTS.tts_saying(this, str);
    }

    //调用语音合成函数，放入Looper中
    public void speakWithLooper(String str) {
        Looper.prepare();
        speak(str);
        Looper.loop();
    }


    //解析数据
    protected void parseAsrPartialJsonData(String data) {
        Log.d(TAG, "parseAsrPartialJsonData data:" + data);
        Gson gson = new Gson();
        AsrPartialJsonData jsonData = gson.fromJson(data, AsrPartialJsonData.class);
        String resultType = jsonData.getResult_type();
        Log.d(TAG, "resultType:" + resultType);
        if (resultType != null && resultType.equals("final_result")) {
            final_result = jsonData.getBest_result();
            if (dialogContent != null) {
                dialogContent.setText(final_result);
            }
            Log.d(TAG, "parseAsrPartialJsonData: 解析结果" + final_result);
        }
    }

    //解析最终数据
    protected void parseAsrFinishJsonData(String data) {
        Log.d(TAG, "parseAsrFinishJsonData data:" + data);
        Gson gson = new Gson();
        AsrFinishJsonData jsonData = gson.fromJson(data, AsrFinishJsonData.class);
        String desc = jsonData.getDesc();

        if (desc != null && desc.equals("Speech Recognize success.")) {
            Log.d(TAG, "parseAsrFinishJsonData: " + final_result);
            if (dialogContent != null) {
                dialogContent.setText(final_result);
                new Thread(() -> {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    closeDialog();
                    openWakeupAndStopAsr();
                    segTextAndPreDeal(final_result);
                }).start();
            }

        } else {
            String errorCode = "\n错误码:" + jsonData.getError();
            String errorSubCode = "\n错误子码:" + jsonData.getSub_error();
            String errorResult = errorCode + errorSubCode;
            if (dialogContent != null) {
                dialogContent.setText("无法检测语音");
                speak("请输入语音");
                new Thread(() -> {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    closeDialog();
                }).start();
            }
            Log.d(TAG, "parseAsrFinishJsonData: 解释错误，原因是：" + desc + "\n" + errorResult);
            openWakeupAndStopAsr();

        }
    }

    //分词和处理
    protected void segTextAndPreDeal(String rawText) {

        if (simpleDeal(rawText)) {
            return;
        }

        SegmentationDomain rawDomains = new SegmentationDomain();
        rawDomains.setRawText(rawText);
        Request request = OkHttpsFactory.createRequest(CommonConstant.API_GEI_SEG_RESULT, rawDomains);
        OkHttpClient client = OkHttpsFactory.getOkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                toast.showShortTime("获取分词信息错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                Log.d(TAG, "onResponse: " + jsonString);
                SegmentationDomain segmentationDomain = (SegmentationDomain) JSONStringUtil.getObjectFromJSONString(jsonString, SegmentationDomain.class);
                if (segmentationDomain == null) {
                    speak("不好意思，未能找到相应的结果");
                    return;
                }
                Message message = MessageFactory.createMessageFromObject(segmentationDomain);
                myHandler.sendMessage(message);
            }
        });
    }

    //本地处理
    protected boolean simpleDeal(String rawText) {

        if (detailSimpleDeal()) {
            return true;
        }

        if (rawText.contains("打开") ||
                rawText.contains("进入") ||
                rawText.contains("前往") ||
                rawText.contains("开启") ||
                rawText.contains("open") ||
                rawText.contains("显示") ||
                rawText.contains("查看") ||
                rawText.contains("察看")) {

            if (rawText.contains("登陆界面") || rawText.contains("登陆页面") || rawText.contains("登陆") || rawText.contains("登录界面") || rawText.contains("登录页面") || rawText.contains("登录")) {

                return pageSwitching(LoginActivity.class, "已前往登陆界面");

            } else if (rawText.contains("注册页面") || rawText.contains("注册") || rawText.contains("注册界面")) {

                return pageSwitching(RegisterActivity.class, "已前往注册界面");

            } else if (rawText.contains("商标相关文件") || rawText.contains("商标注册帮助文件")) {

                return pageSwitching(OptionsActivity.class, "正在前往商标相关文件界面");

            } else if (rawText.contains("申请文件界面") || rawText.contains("申请文件") || rawText.contains("注册文件界面") || rawText.contains("注册文件页面") || rawText.contains("注册文件")) {

                return pageSwitching(FilesActivity.class, "正在前往申请文件界面");

            } else if (rawText.contains("申请指南界面") || rawText.contains("申请指南页面") || rawText.contains("申请指南") || rawText.contains("申请帮助") || rawText.contains("帮助文件")) {

                return pageSwitching(GuideActivity.class, "已前往申请指南界面");

            } else if (rawText.contains("注册流程图") || rawText.contains("注册流程") || rawText.contains("申请流程图") || rawText.contains("申请流程")) {

                return pageSwitching(FlowChartActivity.class, "正在前往注册流程图界面");

            } else if (rawText.contains("商品和服务分类") || rawText.contains("商品服务分类") || rawText.contains("商品分类") || rawText.contains("服务分类")) {

                return pageSwitching(GoodsAndServiceActivity.class, "正在前往商品和服务分类界面");

            } else if (rawText.contains("图形要素") || rawText.contains("商标要素") || rawText.contains("图形分类") || rawText.contains("图形类别") || rawText.contains("图形种类") || rawText.contains("国际分类表")) {

                return pageSwitching(GraphicElementActivity.class, "正在前往图形要素界面");

            } else if (rawText.contains("收费标准") || rawText.contains("费用标准") || rawText.contains("花费标准") || rawText.contains("收费表")) {

                return pageSwitching(RatesActivity.class, "正在前往收费标准界面");

            }

        }

        if (rawText.contains("打开") ||
                rawText.contains("进入") ||
                rawText.contains("前往") ||
                rawText.contains("开启") ||
                rawText.contains("open") ||
                rawText.contains("显示") ||
                rawText.contains("返回") ||
                rawText.contains("回到") ||
                rawText.contains("退回") ||
                rawText.contains("退到") ||
                rawText.contains("回")){

            if (rawText.contains("首页") ||
                rawText.contains("主页") ||
                rawText.contains("主界面") ||
                rawText.contains("开始界面") ||
                rawText.contains("开始页面") ||
                rawText.contains("主页面")){

                //打开主页，新闻那里
                return pageSwitching(MainActivity.class, "前往首页");


            }

        }

        if (rawText.contains("打开") ||
                rawText.contains("进入") ||
                rawText.contains("前往") ||
                rawText.contains("开启") ||
                rawText.contains("open") ||
                rawText.contains("显示")) {

            if (rawText.contains("商标精准查询") || rawText.contains("商标精确查询") || rawText.contains("商标准确查询") || rawText.contains("商标查询") || rawText.contains("查询界面") || rawText.contains("查询页面") ||
                    rawText.contains("商标精准查找") || rawText.contains("商标精确查找") || rawText.contains("商标准确查找") || rawText.contains("商标查找") || rawText.contains("查找界面") || rawText.contains("查找页面") ||
                    rawText.contains("商标精准搜索") || rawText.contains("商标精确搜索") || rawText.contains("商标准确搜索") || rawText.contains("商标搜索") || rawText.contains("搜索界面") || rawText.contains("搜索页面")) {

                return pageSwitching(SearchActivity.class, "已前往商标精准查询界面");

            } else if (rawText.contains("商标近似查找") || rawText.contains("商标近似查询") || rawText.contains("商标近似搜索") ||
                    rawText.contains("商标相似查找") || rawText.contains("商标相似查询") || rawText.contains("商标相似搜索") ||
                    rawText.contains("近似查找界面") || rawText.contains("近似查询界面") || rawText.contains("近似搜索界面") ||
                    rawText.contains("近似查找页面") || rawText.contains("近似查询页面") || rawText.contains("近似搜索页面")) {

                return pageSwitching(SimilarTrademarkEnquiriesActivity.class, "已前往商标近似查询界面");

            } else if (rawText.contains("商标大全") || rawText.contains("所有商标") || rawText.contains("全部商标")) {

                return pageSwitching(TrademarkActivity.class, "已前往商标大全界面");

            } else if (rawText.contains("设置") || rawText.contains("配置")) {

                return pageSwitching(SettingActivity.class, "已前往设置界面");

            } else if (rawText.contains("缓存界面") || rawText.contains("缓存清理界面") || rawText.contains("清理缓存") || rawText.contains("缓存清理") || rawText.contains("垃圾清理") || rawText.contains("清理垃圾")) {

                return pageSwitching(CleanActivity.class, "已前往缓存清理界面");

            } else if (rawText.contains("反馈") || rawText.contains("反馈界面") || rawText.contains("联系界面") || rawText.contains("联系页面")) {

                return pageSwitching(FeedbackActivity.class, "已前往反馈界面");

            } else if (rawText.contains("功能介绍") || rawText.contains("功能说明")) {

                return pageSwitching(FunctionalIntroductionActivity.class, "已前往功能介绍界面");

            } else if (rawText.contains("个人账户") || rawText.contains("账户与安全") || rawText.contains("个人安全界面")) {

                if (UserInfo.isIsUserOnline()) {

                    return pageSwitching(AccountAndSecurityActivity.class, "为您前往账户与安全界面");

                } else {

                    speakWithLooper("请登录后再进行操作");
                    return true;

                }


            }

        }

        if (rawText.contains("返回上一界面") ||
                rawText.contains("返回到上级界面") ||
                rawText.contains("退出当前界面") ||
                rawText.contains("退出当前页面") ||
                rawText.contains("返回上级页面") ||
                rawText.contains("返回上级界面") ||
                rawText.contains("离开当前界面") ||
                rawText.contains("回到上一界面") ||
                rawText.contains("退回上一界面") ||
                rawText.contains("退回上级界面") ||
                rawText.contains("结束当前页面") ||
                rawText.contains("结束当前页")) {
        if (this instanceof MainActivity) {
            return true;
        }

            speakWithLooper("返回上级界面");
            this.finish();
            return true;

        }

        if(rawText.contains("退出")) {
            if (rawText.contains("登陆") || rawText.contains("登录")) {
                Log.d(TAG, "simpleDeal: 退出登录");
                if (UserInfo.isIsUserOnline()) {
                    UserInfo.changeToOfflineStatus();
                    speakWithLooper("已为您退出登录");
                } else {
                    speakWithLooper("您已处于登出状态,无法继续登出");
                }
                return true;
            }
        }

        if (rawText.contains("清除") ||
            rawText.contains("删除") ||
            rawText.contains("清理") ||
            rawText.contains("去除") ||
            rawText.contains("删掉")) {

            if (rawText.contains("商标缓存") || rawText.contains("商标图片缓存") || rawText.contains("商标图片")) {

                if (rawText.contains("新闻缓存") || rawText.contains("新闻文本缓存") || rawText.contains("新闻图片缓存")) {
                    //全部清理
//                    pageSwitchingWithArgs(CleanActivity.class, "已帮您清理商标缓存和新闻缓存", "CLEAN","ALL");
                    DirManager.cleanTrademarkImgAndNewsCache(this);
                    speakWithLooper("已帮您清理商标缓存和新闻缓存");
                    return true;
                }

                //清理商标

//                pageSwitchingWithArgs(CleanActivity.class, "已帮您清理商标缓存", "CLEAN","TRADEMARK");
                DirManager.cleanTrademarkImg(this);
                speakWithLooper("已帮您清理商标缓存");
                return true;
            }

            if (rawText.contains("新闻缓存") || rawText.contains("新闻文本缓存") || rawText.contains("新闻图片缓存")) {

                if (rawText.contains("商标缓存") || rawText.contains("商标图片缓存") || rawText.contains("商标图片")) {

                    //全部清理
//                    pageSwitchingWithArgs(CleanActivity.class, "已帮您清理新闻缓存和商标缓存", "CLEAN","ALL");
                    DirManager.cleanTrademarkImgAndNewsCache(this);
                    speakWithLooper("已帮您清理新闻缓存和商标缓存");
                    return true;
                }

//                pageSwitchingWithArgs(CleanActivity.class, "已帮您清理商标缓存", "CLEAN","NEWS");
                DirManager.cleanNewsCache(this);
                speakWithLooper("已帮您清理新闻缓存");
                return true;
            }

            if (rawText.contains("所有缓存") || rawText.contains("所有垃圾") || rawText.contains("全部缓存") || rawText.contains("全部垃圾")) {

//                pageSwitchingWithArgs(CleanActivity.class, "已帮您清理所有缓存", "CLEAN","ALL");
                DirManager.cleanTrademarkImgAndNewsCache(this);
                speakWithLooper("已帮您清理所有缓存");
                return true;
            }

        }

        return false;
    }

    //开启界面
    protected boolean startIntent(Intent intent) {

        if (intent != null) {
            startActivity(intent);
            return true;
        }

        return false;
    }

    //获取界面intent
    protected Intent getIntent(Class clazz) {
        return new Intent(this, clazz);
    }

    /**
     * 显示语音识别界面
     */
    protected void asrDialog(final Context context) {
        dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.item_dialog_asr, null);
        TextView cancel = view.findViewById(R.id.dialog_cancel);
        dialogContent = view.findViewById(R.id.dialog_content);
        lineWaveVoiceView = view.findViewById(R.id.horvoiceview);
        lineWaveVoiceView.startRecord();
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.3f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.8f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(v -> {
            stopAsrAndWakeUp();
            closeDialog();
        });
        dialog.show();
    }

    //开启语音识别，关闭语音唤醒
    protected void openAsrAndStopWakeup() {

        stopWakeup();
        openAsr();
        asrDialog(this);
        speak("在");
        Log.d(TAG, "startAsr: 关闭唤醒，开启语音识别");

    }

    //开启语音唤醒，关闭语音识别
    protected void openWakeupAndStopAsr() {

        stopAsr();
        openWakeup();
        Log.d(TAG, "openWakeupAndStopAsr: 开启唤醒，关闭语音识别");

    }

    //页面跳转
    protected boolean pageSwitching(Class clazz, String speakText) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (speakText != null && !speakText.equals("")) {
            speakWithLooper(speakText);
        }
        return true;
    }

    //有参数的
    protected boolean pageSwitchingWithArgs(Class clazz, String speakText, String type, String str){
        Intent intent = new Intent(this, clazz);
        intent.putExtra(type, str);
        startActivity(intent);
        if (speakText != null) {
            speakWithLooper(speakText);
        }
        return true;
    }



    //关闭语音识别窗口
    protected void closeDialog() {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (lineWaveVoiceView != null) {
            lineWaveVoiceView.stopRecord();
        }

    }

    //关闭语音识别和语音唤醒
    protected void stopAsrAndWakeUp(){
        stopAsr();
        stopWakeup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openWakeup();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAsrAndWakeUp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAsrAndWakeUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeup.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0);
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }
}
