package com.example.newproject.client.ui.optionsActivitys.trademarkApplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.domain.SegmentationDomain;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GoodsAndServiceActivity extends BaseActivity{

    @BindView(R.id.trademark_csdt) LinearLayout csdtButton;
    @BindView(R.id.accessory) LinearLayout attachedFilesButton;
    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void deal(SegmentationDomain segmentationDomain) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_and_service);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

    }

    @OnClick(R.id.trademark_csdt)
    public void csdt(){
        Intent intent = new Intent(this, CSDTActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.accessory)
    public void accessory(){
        Intent intent = new Intent(this, AttachedFilesActivity.class);
        startActivity(intent);
    }

}
