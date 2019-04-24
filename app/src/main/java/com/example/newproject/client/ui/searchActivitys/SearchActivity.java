package com.example.newproject.client.ui.searchActivitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.trademarkInfoActivitys.TrademarkInfoActivity;
import com.example.newproject.web.core.TrademarkList;
import com.example.newproject.web.domain.TrademarkListDomain;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";

    @BindView(R.id.searchView) SearchView mSearchView;
    @BindView(R.id.listView) ListView mListView;
    @BindView(R.id.search_close_down) ImageButton closeButton;
    @BindView(R.id.search_tips) TextView searchTips;

    private List<TrademarkListDomain> trademarkListDomains;

    private String[] mStrs;
    private String[] tips;
    private List<String> tipLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    @OnClick(R.id.search_close_down)
    public void close(){
        finish();
    }



    private void init(){
        ButterKnife.bind(this);
        searchTips.setText("热门商标：");

        trademarkListDomains = TrademarkList.getTrademarkListDomains();
        if (trademarkListDomains == null) {
            Log.d(TAG, "init: this is null");
            return;
        }

//        tipLists = new ArrayList<>();
//        initTestData();

        tips = new String[3];
        mStrs = new String[trademarkListDomains.size()];
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tips));
        mListView.setTextFilterEnabled(true);
//        mListView.setVisibility(View.INVISIBLE);//隐藏TextView

        for (int i = 0; i < 3; i++){
//            tips[i] = libraries[i].getName();
            tips[i] = trademarkListDomains.get(i).getTrademarkName();
        }

        for (int i=0;i<mStrs.length;i++){
            mStrs[i] = "";
        }
        for (int i=0;i<trademarkListDomains.size();i++){
            mStrs[i] = trademarkListDomains.get(i).getTrademarkName();

        }

//         设置搜索文本监听


        mSearchView.setQueryHint("请输入搜索内容");//搜索框内文字
        mSearchView.setSubmitButtonEnabled(true);//显示提交按钮
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private boolean sign = false;

            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
//                mListView.setVisibility(View.VISIBLE);
                mListView.setAdapter(new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, mStrs));
                mSearchView.clearFocus();
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    if (!sign){
                        searchTips.setText("搜索结果：");
                        mListView.setAdapter(new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, mStrs));
                        sign = true;
                    }
//                    Toast.makeText(MainActivity.this,newText,Toast.LENGTH_SHORT).show();

                    mListView.setFilterText(newText);
                }else{
                    sign = false;
                    mListView.setAdapter(new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, tips));
                    mListView.clearTextFilter();
                }
                return false;
            }
        });
        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                mListView.setVisibility(View.GONE);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //传递name和图片路径到Imageactivity
//                int ImagePath = libraries[position].getImageId();
//                String ImageName = mListView.getItemAtPosition(position).toString();
                int ImagePath = -1;
                String ImageName = "";
                String trademarkName = ((TextView) view).getText().toString();
                TrademarkListDomain trademarkListDomain = TrademarkList.getTrademarkListDomainByTrademarkName(trademarkName);
                if (trademarkListDomain == null) {
                    Toast.makeText(SearchActivity.this, "系统暂未收录该商标", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(SearchActivity.this, TrademarkInfoActivity.class);
                intent.putExtra("trademarkId",trademarkListDomain.getTrademarkId());
                startActivity(intent);
            }
        });
    }
}
