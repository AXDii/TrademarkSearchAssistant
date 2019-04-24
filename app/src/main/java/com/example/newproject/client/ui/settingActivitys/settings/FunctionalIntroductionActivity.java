package com.example.newproject.client.ui.settingActivitys.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by AXD on 2017/12/3.
 */

public class FunctionalIntroductionActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functional_ntroduction);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

}
