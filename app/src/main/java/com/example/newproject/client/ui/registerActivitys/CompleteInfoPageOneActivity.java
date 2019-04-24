package com.example.newproject.client.ui.registerActivitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.mainActivitys.MainActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.core.UserInfo;
import com.example.newproject.web.domain.User;
import com.example.newproject.web.util.CheckFormat;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.MessageFactory;
import com.example.newproject.web.util.MyToast;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompleteInfoPageOneActivity extends BaseActivity {

    private Handler myHandler;

    private static final String TAG = "CompleteInfoPageOneActi";

    private User user;

    @BindView(R.id.complete_info_skip)
    TextView skipButton;

    @BindView(R.id.complete_info_username_edit)
    EditText userNameEditText;

    @BindView(R.id.complete_info_email_edit)
    EditText emailEditText;

    @BindView(R.id.complete_info_real_name_edit)
    EditText realNameEditText;

    @BindView(R.id.complete_info_complete)
    Button completeButton;

    @BindView(R.id.complete_info_meeting_problem)
    TextView meetingProblemButton;

    static class MyHandler extends Handler {

        WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String jsonString = data.getString(CommonConstant.JSON_STRING);
            User returnUser = (User) JSONStringUtil.getObjectFromJSONString(jsonString, User.class);
            Context context = weakReference.get();
            MyToast myToast = new MyToast(context);

            switch (msg.what) {
                case CommonConstant.MESSAGE_WHAT_BY_UPDATE_USER_INFO:{

                    switch (returnUser.getStatusCode()) {
                        case CommonConstant.SERVER_ERROR:
                            myToast.showShortTime("网络出错");
                            break;
                        case CommonConstant.UPDATE_FAULT:
                            myToast.showShortTime("完善消息失败");
                            break;
                        case CommonConstant.OPERATE_SUCCESS:
                            User user = new User();
                            user.setUserPhone(returnUser.getUserPhone());
                            user.setUserPassword(returnUser.getUserPassword());
                            ((CompleteInfoPageOneActivity)context).userLogin(context, user);
                            break;
                    }

                    break;
                }

                case CommonConstant.MESSAGE_WHAT_BY_LOGIN:{

                    switch (returnUser.getStatusCode()) {
                        case CommonConstant.SERVER_ERROR:
                            myToast.showLongTime("服务器出错");
                            break;
                        case CommonConstant.USER_NOT_EXISTS:
                            myToast.showLongTime("该手机号码还未注册");
                            break;
                        case CommonConstant.OPERATE_SUCCESS:
                            myToast.showShortTime("登陆成功");
                            UserInfo.setUser(returnUser);
                            UserInfo.changeToOnlineStatus();
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            break;
                    }
                    break;
                }
                default:break;
            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info_page_one);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        setUserFromIntent();
        myHandler = new MyHandler(this);
    }

    private void setUserFromIntent() {
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra(CommonConstant.JSON_STRING);
        user = (User)JSONStringUtil.getObjectFromJSONString(jsonString, User.class);
    }

    @OnClick(R.id.complete_info_skip)
    public void skip() {
        userLogin(this, user);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.complete_info_complete)
    public void complete() {

        String userName = userNameEditText.getText().toString().trim();
        String userEmail = emailEditText.getText().toString().trim();
        String userRealName = realNameEditText.getText().toString().trim();

        MyToast myToast = new MyToast(this);

        if (!userName.equals("") && !CheckFormat.isCorrectNameFormat(userName)) {
            myToast.showShortTime("用户昵称格式不正确！");
            return;
        } else {
            user.setUserName(userName);
        }

        if (!userEmail.equals("") && !CheckFormat.isCorrectEmailFormat(userEmail)) {
            myToast.showShortTime("用户邮政格式不正确！");
            return;
        } else {
            user.setUserEmail(userEmail);
        }

        user.setUserRealName(userRealName);
//        userLogin(this, user);
        userUpdateInfo(user);
    }

    @OnClick(R.id.complete_info_meeting_problem)
    public void meetingProblem() {

    }

    private void userUpdateInfo(final User user){
        OkHttpClient client = OkHttpsFactory.getOkHttpClient();
        Request request = OkHttpsFactory.createRequest(CommonConstant.API_UPDATE_USER_INFO, user);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                Toast.makeText(CompleteInfoPageOneActivity.this, "网络出现错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseDate = response.body().string();
                User responseUser = (User) JSONStringUtil.getObjectFromJSONString(responseDate, User.class);
                Message message = MessageFactory.createMessageFromObject(responseUser);
                message.what = CommonConstant.MESSAGE_WHAT_BY_UPDATE_USER_INFO;
                myHandler.sendMessage(message);
            }
        });
    }

    private void userLogin(final Context context, final User user) {

        OkHttpClient okHttpClient = OkHttpsFactory.getOkHttpClient();
        Request request = OkHttpsFactory.createRequest(CommonConstant.API_LOGIN, user);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                Toast.makeText(CompleteInfoPageOneActivity.this, "网络出现错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseDate = response.body().string();
                User responseUser = (User) JSONStringUtil.getObjectFromJSONString(responseDate, User.class);
                Message message = MessageFactory.createMessageFromObject(responseUser);
                message.what = CommonConstant.MESSAGE_WHAT_BY_LOGIN;
                myHandler.sendMessage(message);
            }
        });

    }



}
