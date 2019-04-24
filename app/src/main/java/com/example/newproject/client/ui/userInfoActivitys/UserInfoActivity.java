package com.example.newproject.client.ui.userInfoActivitys;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.core.UserInfo;
import com.example.newproject.web.domain.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity {


    @BindView(R.id.user_info_user_icon)
    ImageView userIcon;

    @BindView((R.id.user_info_user_nickname))
    TextView userNickname;

    @BindView(R.id.user_info_user_realname)
    TextView userRealName;

    @BindView(R.id.user_info_user_phone)
    TextView userPhone;

    @BindView(R.id.user_info_user_email)
    TextView userEmail;

    @BindView(R.id.user_info_user_address)
    TextView userAddress;

    @BindView(R.id.user_info_more)
    LinearLayout more;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
    }


    private void init(){
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (UserInfo.isIsUserOnline()) {
            setUserInfo();
        } else {
            initUserInfo();
        }
    }

    private void initUserInfo(){
        userNickname.setText("");
        userRealName.setText("");
        userPhone.setText("");
        userEmail.setText("");
        userAddress.setText("");
    }

    private void setUserInfo(){
        User user = UserInfo.getUser();
        userNickname.setText(user.getUserName());
        userRealName.setText(user.getUserRealName());
        userPhone.setText(user.getUserPhone());
        userEmail.setText(user.getUserEmail());
        userAddress.setText(user.getUserAddress());
    }

    @OnClick(R.id.user_info_more)
    public void more(){
        Toast.makeText(this, "暂无更多信息", Toast.LENGTH_SHORT).show();
    }

}
