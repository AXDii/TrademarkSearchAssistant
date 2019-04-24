package com.example.newproject.client.ui.settingActivitys.settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.core.UserInfo;
import com.example.newproject.web.domain.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountAndSecurityActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.username_edit_account_and_security)
    TextView usernameEdit;

    @BindView(R.id.phone_edit_account_and_security)
    TextView phoneEdit;

    @BindView(R.id.name_edit_account_and_security)
    TextView nameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_security);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        if (UserInfo.isIsUserOnline()) {
            User user = UserInfo.getUser();
            usernameEdit.setText(user.getUserName());
            phoneEdit.setText(user.getUserPhone());
            nameEdit.setText(user.getUserRealName());
        }

    }

    @OnClick(R.id.edit_personal_information_account_and_security)
    public void editPersonalInfo(){
        toast.showShortTime("暂未支持该功能");
    }

    @OnClick(R.id.find_account_account_and_security)
    public void findAccount(){
        toast.showShortTime("暂未支持该功能");
    }

    @OnClick(R.id.edit_password_account_and_security)
    public void editPassword() {
        toast.showShortTime("暂未支持该功能");
    }

}
