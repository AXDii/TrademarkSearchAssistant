package com.example.newproject.client.ui.optionsActivitys.trademarkApplication;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.domain.SegmentationDomain;

import java.io.IOException;
import java.io.InputStream;

public class FlowChartActivity extends BaseActivity {


    public static final String FILE_PATH = "flowChart/注册流程图.png";

    private ImageView imageView;

    @Override
    protected void deal(SegmentationDomain segmentationDomain) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.flow_chart);
        try {
            showImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showImage() throws IOException {

        AssetManager assetManager = this.getAssets();
        InputStream is = assetManager.open(FILE_PATH);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        imageView.setImageBitmap(bitmap);

    }
}
