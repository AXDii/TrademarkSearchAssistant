package com.example.newproject.client.ui.settingActivitys.settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {


    @BindView(R.id.feedback_button)
    Button submitButton;

    @BindView(R.id.phone_feedback_setting)
    EditText phoneEdit;

    @BindView(R.id.problem_feedback_setting)
    EditText problemEdit;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }


    @OnClick(R.id.feedback_button)
    public void submit(){

        phoneEdit.setText("");
        problemEdit.setText("");
        toast.showShortTime("已接受到您的反馈！");

    }
}
