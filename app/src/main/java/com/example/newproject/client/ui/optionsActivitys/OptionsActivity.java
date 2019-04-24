package com.example.newproject.client.ui.optionsActivitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.FilesActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.FlowChartActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.GoodsAndServiceActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.GraphicElementActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.GuideActivity;
import com.example.newproject.client.ui.optionsActivitys.trademarkApplication.RatesActivity;
import com.example.newproject.web.domain.SegmentationDomain;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OptionsActivity extends BaseActivity {

    @BindView(R.id.trademark_Application_file) LinearLayout filesButton;
    @BindView(R.id.trademark_Application_guide) LinearLayout guideButton;
    @BindView(R.id.trademark_Application_flow_chart) LinearLayout flowChartButton;
    @BindView(R.id.trademark_Application_goods_and_service) LinearLayout goodsAndSeviceButton;
    @BindView(R.id.trademark_Application_guide_graphic_elements) LinearLayout graphicElementButton;
    @BindView(R.id.trademark_Application_rates) LinearLayout ratesButton;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void deal(SegmentationDomain segmentationDomain) {

    }

    public void myStartActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @OnClick(R.id.trademark_Application_file)
    public void file(){
        myStartActivity(FilesActivity.class);
    }

    @OnClick(R.id.trademark_Application_guide)
    public void guide(){
        myStartActivity(GuideActivity.class);
    }

    @OnClick(R.id.trademark_Application_flow_chart)
    public void flowChart(){
        myStartActivity(FlowChartActivity.class);
    }

    @OnClick(R.id.trademark_Application_goods_and_service)
    public void goodsAndService(){
        myStartActivity(GoodsAndServiceActivity.class);
    }

    @OnClick(R.id.trademark_Application_guide_graphic_elements)
    public void guideGraphicElements(){
        myStartActivity(GraphicElementActivity.class);
    }

    @OnClick(R.id.trademark_Application_rates)
    public void retes(){
        myStartActivity(RatesActivity.class);
    }

}
