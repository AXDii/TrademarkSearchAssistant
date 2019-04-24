package com.example.newproject.client.ui.optionsActivitys.trademarkApplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.domain.SegmentationDomain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FilesActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "FilesActivity";

    private final static String File_Path = "trademarkApplicationFiles";

    private ListView listView;
    private String[] fileNames = null;

    @Override
    protected void deal(SegmentationDomain segmentationDomain) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fileNames = getFileNames(this, File_Path);

        listView = (ListView)findViewById(R.id.files_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);


    }


    public static String[] getFileNames(Context context, String filePath) {

        AssetManager assetManager = context.getAssets();
        String[] names = null;
        try {
            names = assetManager.list(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return names;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        AssetManager assetManager = this.getAssets();
        String fileName = ((TextView)view).getText().toString();
        String filePath = File_Path + "/" + fileName;
        Context context = this.getApplicationContext();
        String newFilePath = getExternalCacheDir() + "/" + fileName;

        File file = new File(newFilePath);
        if (!file.exists() || file.length() == 0) {

            try {
                InputStream inputStream = assetManager.open(filePath);
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                inputStream.close();
                fos.close();
                Log.d(TAG, "onItemClick: 模型复制完成");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.newproject.FileProvider", file);
            //Uri uri = Uri.fromFile(file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/msword");
            intent.addCategory("android.intent.category.DEFAULT");
        } else {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/msword");
        }

        try {
            startActivity(intent);
        }catch (Exception e) {
            Toast.makeText(this, "没有打开该文件的应用", Toast.LENGTH_SHORT).show();
        }


    }
}
