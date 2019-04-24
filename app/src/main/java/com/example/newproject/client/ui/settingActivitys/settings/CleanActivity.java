package com.example.newproject.client.ui.settingActivitys.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.newproject.R;
import com.example.newproject.client.baiduASR.ScreenSizeUtils;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.cons.CommonConstant;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CleanActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.size_clean_news_cache)
    TextView newsCacheSize;

    @BindView(R.id.size_clean_trademark_img)
    TextView trademarkImgSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        updateNewsCacheSzie();
        updateTrademarkImgSize();

    }

    public void init(){



    }

    public void updateTrademarkImgSize() {

        File trademarkImgFile = DirManager.getDirFile(CommonConstant.TRADEMARK_IMG_DIR, this);
        File trademarkTbImgFile = DirManager.getDirFile(CommonConstant.TRADEMARK_TB_IMG_DIR, this);
        long trademarkImgSizeLong = DirManager.getFolderSize(trademarkImgFile) + DirManager.getFolderSize(trademarkTbImgFile);
        runOnUiThread(()->{
            trademarkImgSize.setText("商标图片缓存占用：" + DirManager.getFormatSize((double)trademarkImgSizeLong));
//            newsCacheSize.setText("新闻缓存占用：" + DirManager.getFormatSize((double)newsCacheSizeLong));

        });


    }

    public void updateNewsCacheSzie(){


        File newsFile = DirManager.getDirFile(CommonConstant.NEWS_DIR, this);
        long newsCacheSizeLong = DirManager.getFolderSize(newsFile);

        runOnUiThread(()->{
//            trademarkImgSize.setText("商标图片缓存占用：" + DirManager.getFormatSize((double)trademarkImgSizeLong));
            newsCacheSize.setText("新闻缓存占用：" + DirManager.getFormatSize((double)newsCacheSizeLong));

        });


    }

    @OnClick(R.id.clean_trademark_img)
    public void cleanTrademarkImg(){

        String trademarkImgFile = DirManager.getDirFile(CommonConstant.TRADEMARK_IMG_DIR, this).getPath();
        String trademarkTbImgFile = DirManager.getDirFile(CommonConstant.TRADEMARK_TB_IMG_DIR, this).getPath();
        customDialog(this,1, trademarkImgFile, trademarkTbImgFile);

    }

    @OnClick(R.id.clean_news_cache)
    public void cleanNewsCache(){

        String newsFile = DirManager.getDirFile(CommonConstant.NEWS_DIR, this).getPath();
        customDialog(this, 2, newsFile);


    }


    /**
     * 自定义对话框
     */
    private void customDialog(final Context context, int type, String... filePaths) {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.item_dialog_cache_clean, null);
        TextView cancel = view.findViewById(R.id.dialog_cancel);
        TextView confirm = view.findViewById(R.id.dialog_confirm);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {

            for (String filePath : filePaths) {
                DirManager.deleteFolderFile(filePath, false);
            }
            toast.showShortTime("清理完成");
            dialog.dismiss();

            if (type == 1) {
                updateTrademarkImgSize();
            } else if (type == 2) {
                updateNewsCacheSzie();
            }

        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        update();
    }
}
