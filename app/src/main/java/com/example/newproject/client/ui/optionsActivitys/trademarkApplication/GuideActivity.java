package com.example.newproject.client.ui.optionsActivitys.trademarkApplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.domain.SegmentationDomain;


public class GuideActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "GuideActivity";

    private final static String File_Path = "guides";

    private ListView listView;
    private String[] fileNames = null;

    @Override
    protected void deal(SegmentationDomain segmentationDomain) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fileNames = FilesActivity.getFileNames(this, File_Path);

        listView = (ListView)findViewById(R.id.guide_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ShowHtmlActivity.class);
        String fileName = ((TextView)view).getText().toString();
        String newFilePath = File_Path + "/" + fileName;
        intent.putExtra("FilePath", newFilePath);
        startActivity(intent);
    }


    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

}
