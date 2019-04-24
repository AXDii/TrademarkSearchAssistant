package com.example.newproject.client.ui.optionsActivitys.trademarkApplication;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.domain.SegmentationDomain;


public class ShowHtmlActivity extends BaseActivity {


    private WebView webView;

    @Override
    protected void deal(SegmentationDomain segmentationDomain) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_html);
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("FilePath");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showHtml(filePath);
    }

    public void showHtml(String FILE_PATH) {

        webView = (WebView)findViewById(R.id.show_html);

        AssetManager assetManager = this.getAssets();
        webView.setWebViewClient(new WebViewClient());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        String url = "file:///android_asset/" + FILE_PATH;
        webView.loadUrl(url);


    }
}
