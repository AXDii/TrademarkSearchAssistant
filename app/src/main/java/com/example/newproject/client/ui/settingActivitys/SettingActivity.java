package com.example.newproject.client.ui.settingActivitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.example.newproject.R;
import com.example.newproject.client.core.ActivityControl;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.mainActivitys.MainActivity;
import com.example.newproject.client.ui.settingActivitys.settings.AccountAndSecurityActivity;
import com.example.newproject.client.ui.settingActivitys.settings.CleanActivity;
import com.example.newproject.client.ui.settingActivitys.settings.FeedbackActivity;
import com.example.newproject.client.ui.settingActivitys.settings.FunctionalIntroductionActivity;
import com.example.newproject.web.core.UserInfo;
import com.example.newproject.web.util.MyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {


    @BindView(R.id.account_and_security_setting)
    LinearLayout accountAndSecurityButton;

    @BindView(R.id.check_update_setting)
    LinearLayout checkUpdateSettingButton;

    @BindView(R.id.feedback_setting)
    LinearLayout feedbackSettingButton;

    @BindView(R.id.function_introduction_setting)
    LinearLayout functionIntroductionButton;

    @BindView(R.id.logout_setting)
    LinearLayout logoutButton;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MyToast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init(){
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (UserInfo.isIsUserOnline()) {
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            logoutButton.setVisibility(View.GONE);
        }

        myToast = new MyToast(this);
    }

    @OnClick(R.id.account_and_security_setting)
    public void accountAndSecurity(){
        if (UserInfo.isIsUserOnline()) {
            startActivity(getIntent(AccountAndSecurityActivity.class));
        } else {
            myToast.showShortTime("请登录后再操作");
        }
    }

    @OnClick(R.id.clean_setting)
    public void cleanCache() {
        startActivity(getIntent(CleanActivity.class));
    }

    @OnClick(R.id.check_update_setting)
    public void checkUpdate(){
        toast.showShortTime("当前版本已是最新版本");
    }

    @OnClick(R.id.function_introduction_setting)
    public void functionIntroductionSetting(){
        startActivity(getIntent(FunctionalIntroductionActivity.class));
    }

    @OnClick(R.id.feedback_setting)
    public void feedback(){
       startActivity(getIntent(FeedbackActivity.class));
    }

    @OnClick(R.id.logout_setting)
    public void logout(){

        UserInfo.changeToOfflineStatus();
        logoutButton.setVisibility(View.GONE);
        UserInfo.setUser(null);

        if (ActivityControl.getActivity() != null) {
            ActivityControl.getActivity().finish();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }


}
