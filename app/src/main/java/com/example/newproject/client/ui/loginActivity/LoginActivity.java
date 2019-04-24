package com.example.newproject.client.ui.loginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.mainActivitys.MainActivity;
import com.example.newproject.client.ui.registerActivitys.RegisterActivity;
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

/**
 * 登陆页面
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private Handler handler;

    @BindView(R.id.login_close) ImageButton closeButton;
    @BindView(R.id.login_phone_edit) EditText userPhoneEditText;
    @BindView(R.id.login_password_edit) EditText userPasswordEditText;
    @BindView(R.id.login_button) Button loginButton;
    @BindView(R.id.login_goto_register) TextView gotoRegisterButton;


    static class MyHandler extends Handler{

        WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String jsonString = data.getString(CommonConstant.JSON_STRING);
            Log.d(TAG, "handleMessage: " + jsonString);
            User returnUser = (User) JSONStringUtil.getObjectFromJSONString(jsonString, User.class);
            Context context = weakReference.get();

            MyToast myToast = new MyToast(context);
            switch (returnUser.getStatusCode()) {
                case CommonConstant.SERVER_ERROR:
                    myToast.showLongTime("服务器出错");
                    break;
                case CommonConstant.USER_NOT_EXISTS:
                    myToast.showLongTime("该手机号码还未注册");
                    break;
                case CommonConstant.PASSWORD_WRONG:
                    myToast.showShortTime("密码错误");
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
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        handler = new MyHandler(this);
    }


    private void userLogin(final Context context, final String phone, final String password) {

        final User user = new User();
        user.setUserPhone(phone);
        user.setUserPassword(password);
        OkHttpClient okHttpClient = OkHttpsFactory.getOkHttpClient();
        Request request = OkHttpsFactory.createRequest(CommonConstant.API_LOGIN, user);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                Toast.makeText(LoginActivity.this, "网络出现错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseDate = response.body().string();
                User responseUser = (User) JSONStringUtil.getObjectFromJSONString(responseDate, User.class);
                Message message = MessageFactory.createMessageFromObject(responseUser);
                handler.sendMessage(message);
            }
        });

    }

    @OnClick(R.id.login_button)
    public void login() {
        String phone = userPhoneEditText.getText().toString().trim();
        String password = userPasswordEditText.getText().toString().trim();
        MyToast toast = new MyToast(this);
        if (phone.equals("")) {
            toast.showShortTime("手机号码不能为空！");
        } else if (password.equals("")) {
            toast.showShortTime("密码不能为空！");
        } else if (!CheckFormat.isPhoneNumberFormat(phone)) {
            toast.showShortTime("手机号码格式不正确!");
        } else if (!CheckFormat.isCorrectPasswordFormat(password)) {
            toast.showShortTime("密码格式不正确！");
        } else {
            userLogin(this, phone, password);
        }
    }

    @OnClick(R.id.login_close)
    public void closeThisActivity(){
        finish();
    }

    @OnClick(R.id.login_goto_register)
    public void gotoRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

}
