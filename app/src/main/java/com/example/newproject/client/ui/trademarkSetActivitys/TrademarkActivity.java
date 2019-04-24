package com.example.newproject.client.ui.trademarkSetActivitys;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;

import com.example.newproject.R;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.web.core.TrademarkList;
import com.example.newproject.web.domain.TrademarkListDomain;

import java.util.List;

public class TrademarkActivity extends BaseActivity {

    private List<TrademarkListDomain> trademarkListDomains;
    private Layout mLayout;


    private TrademarkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initTrademarkListDomains();
//        initLibraries();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TrademarkAdapter(trademarkListDomains);
        recyclerView.setAdapter(adapter);

    }

    private void initTrademarkListDomains() {

        trademarkListDomains = TrademarkList.getTrademarkListDomains();

    }


}
