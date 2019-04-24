package com.example.newproject.client.ui.registerActivitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.loginActivity.LoginActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
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
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    private Handler myHandler;

    private User user;


    @BindView(R.id.register_close) ImageButton closeButton;

    @BindView(R.id.register_goto_login) TextView gotoLoginButton;

    @BindView(R.id.register_phone_edit) EditText phoneEditText;

    @BindView(R.id.register_code_edit) EditText codeEditText;

    @BindView(R.id.register_gain_code) TextView gainCodeButton;

    @BindView(R.id.register_password_edit) EditText passwordEditText;

    @BindView(R.id.register_show_password_checkbox) CheckBox showPasswordButton;

    @BindView(R.id.register_show_password) TextView showPasswordText;

    @BindView(R.id.register_next_step) Button nextStepButton;

    @BindView(R.id.register_meeting_problem) TextView meetingProblemButton;


    static class MyHandler extends Handler{

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
            switch (returnUser.getStatusCode()) {
                case CommonConstant.SERVER_ERROR:
                    myToast.showLongTime("服务器出错");
                    break;
                case CommonConstant.USER_EXISTS:
                    myToast.showLongTime("该手机号码已注册");
                    break;
                case CommonConstant.OPERATE_SUCCESS:
                    myToast.showShortTime("注册成功");
                    Intent intent = new Intent(context, CompleteInfoPageOneActivity.class);
                    intent.putExtra(CommonConstant.JSON_STRING, JSONStringUtil.getJSONStringFromObject(returnUser));
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    break;
            }
        }
    }


    //Mob使用的东西
    EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理成功得到验证码的结果
                            // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                            Toast.makeText(RegisterActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                        } else {
                            // TODO 处理错误的结果
                            Log.e(TAG, "handleMessage: " + ((Throwable)data).getMessage());
                            Toast.makeText(RegisterActivity.this, "验证码发送出错", Toast.LENGTH_SHORT).show();
//                            ((Throwable) data).printStackTrace();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理验证码验证通过的结果
                            //在这里进行注册
                            register();

                        } else {
                            // TODO 处理错误的结果
                            Log.e(TAG, "handleMessage: " +  ((Throwable) data).getMessage());
                            Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                    return false;
                }
            }).sendMessage(msg);
        }
    };

    public void register() {

        if (user == null || user.getUserPhone().equals("")) {
            Toast.makeText(this, "出现错误", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = OkHttpsFactory.createRequest(CommonConstant.API_REGISTER, user);
        OkHttpClient client = OkHttpsFactory.getOkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(RegisterActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                User returnUser = (User)JSONStringUtil.getObjectFromJSONString(jsonString, User.class);
                Message message = MessageFactory.createMessageFromObject(returnUser);
                myHandler.sendMessage(message);

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        initListener();
        user = new User();
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
        myHandler = new MyHandler(this);
    }

    @OnClick(R.id.register_goto_login)
    public void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.register_close)
    public void closeThisActivity(){
        finish();
    }

    @OnClick(R.id.register_gain_code)
    public void gainCode(){
        //检查是否为空，检查是否格式正确之类的
        String phoneNumber = phoneEditText.getText().toString().trim();
        Toast toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        if (phoneNumber.equals("")){
            passwordEditText.setText("");
            toast.setText("手机号码不能为空！");
            toast.show();
            return;
        } else if (!CheckFormat.isPhoneNumberFormat(phoneNumber)) {
            passwordEditText.setText("");
            toast.setText("手机号码格式不正确！");
            toast.show();
            return;
        }

        // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
        SMSSDK.getVerificationCode("86", phoneNumber);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                gainCodeButton.setEnabled(false);
                gainCodeButton.setTextColor(Color.argb(80, 255, 255, 255));
                for (int i = 60; i > 0; i--) {
                    gainCodeButton.setText(i + "S");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                gainCodeButton.setEnabled(true);
                gainCodeButton.setText("重新发送");
                gainCodeButton.setTextColor(Color.WHITE);
            }
        });
        thread.start();
    }

    @OnClick(R.id.register_next_step)
    public void nextStep(){
        String phoneNumber = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String code = codeEditText.getText().toString().trim();
        MyToast myToast = new MyToast(this);
        if (phoneNumber.equals("")){
            passwordEditText.setText("");
            myToast.showLongTime("手机号码不能为空！");
            return;
        } else if (!CheckFormat.isPhoneNumberFormat(phoneNumber)) {
            passwordEditText.setText("");
            myToast.showLongTime("手机号码格式不正确！");
            return;
        } else if (password.equals("")) {
            passwordEditText.setText("");
            myToast.showLongTime("密码不能为空！");
            return;
        } else if (!CheckFormat.isCorrectPasswordFormat(password)) {
            passwordEditText.setText("");
            myToast.showLongTime("密码格式不正确!");
            return;
        } else if (code.equals("")) {
//            passwordEditText.setText("");
            myToast.showLongTime("验证码不能为空！");
            return;
        }

        // 提交验证码，其中的code表示验证码，如“1357”
        user.setUserPhone(phoneNumber);
        user.setUserPassword(password);
        passwordEditText.setText("");
        SMSSDK.submitVerificationCode("86", phoneNumber, code);


    }





    private void initListener() {
        showPasswordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = phoneEditText.getText().toString().trim();
                if (CheckFormat.isPhoneNumberFormat(phoneNumber)) {

                    gainCodeButton.setEnabled(true);
                    gainCodeButton.setTextColor(Color.WHITE);

                } else {
                    gainCodeButton.setEnabled(false);
                    gainCodeButton.setTextColor(Color.argb(80, 255, 255, 255));
                }
            }

        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordEditText.getText().toString().trim().equals("")) {
                    showPasswordButton.setEnabled(false);
                    showPasswordText.setTextColor(Color.argb(80, 255, 255, 255));
                } else {
                    showPasswordButton.setEnabled(true);
                    showPasswordText.setTextColor(Color.WHITE);
                }
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

}
