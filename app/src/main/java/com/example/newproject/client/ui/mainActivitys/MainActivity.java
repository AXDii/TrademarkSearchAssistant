package com.example.newproject.client.ui.mainActivitys;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.newproject.R;
import com.example.newproject.client.baiduASR.ScreenSizeUtils;
import com.example.newproject.client.core.ActivityControl;
import com.example.newproject.client.core.InitApp;
import com.example.newproject.client.ui.BaseActivity;
import com.example.newproject.client.ui.loginActivity.LoginActivity;
import com.example.newproject.client.ui.mainActivitys.results.NewsFragment;
import com.example.newproject.client.ui.mainActivitys.results.SimilarTrademarkFragment;
import com.example.newproject.client.ui.mainActivitys.results.TrademarkAttributeFragment;
import com.example.newproject.client.ui.mainActivitys.results.TrademarkFileFragment;
import com.example.newproject.client.ui.mainActivitys.results.TrademarkFragment;
import com.example.newproject.client.ui.optionsActivitys.OptionsActivity;
import com.example.newproject.client.ui.searchActivitys.SearchActivity;
import com.example.newproject.client.ui.settingActivitys.SettingActivity;
import com.example.newproject.client.ui.similarTrademarkEnquiries.SimilarTrademarkEnquiriesActivity;
import com.example.newproject.client.ui.trademarkSetActivitys.TrademarkActivity;
import com.example.newproject.client.ui.userInfoActivitys.UserInfoActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.UserInfo;
import com.example.newproject.web.domain.SegmentationDomain;
import com.example.newproject.web.domain.User;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.WordSegCommonConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements  NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private static final String TAG = "MainActivity";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static int fragmentType = -1;

    private CircleImageView userInfoImageView;

    private TextView userName = null;

    private TextView userEmail = null;

    private static FragmentManager fm;

    private static Map<String, Fragment> fragmentMap;

    private static User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityManager.setMainActivity(this);
        init();

    }

    /**
     * 初始化类
     */
    private void init() {
        ButterKnife.bind(this);  //绑定View
        setSupportActionBar(toolbar);  //设置标题栏

        fm = getSupportFragmentManager();
        NewsFragment newsFragment = new NewsFragment();
        fm.beginTransaction().add(R.id.container, newsFragment).commit();
        fragmentType = 1;
        fragmentMap = new HashMap<>();

        fragmentMap.put(WordSegCommonConstant.NEWS_DOMAIN, newsFragment);
        fragmentMap.put(WordSegCommonConstant.TRADEMARK_DOMAIN, new TrademarkFragment());
        fragmentMap.put(WordSegCommonConstant.FILE_DOMAIN, new TrademarkFileFragment());
        fragmentMap.put(WordSegCommonConstant.ATTRIBUTE_DOMAIN, new TrademarkAttributeFragment());
        fragmentMap.put(WordSegCommonConstant.SIMILARITY_TEXT_DOMAIN, new SimilarTrademarkFragment());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        ActivityControl.setActivity(this);

        View headView = navView.getHeaderView(0);

        userInfoImageView = (CircleImageView) headView.findViewById(R.id.user_icon_button);
        userName = (TextView) headView.findViewById(R.id.main_activity_username);
        userEmail = (TextView) headView.findViewById(R.id.main_activity_user_email);

        if (UserInfo.isIsUserOnline()) {
            user = UserInfo.getUser();
            userName.setText(user.getUserName() + "");
            userEmail.setText(user.getUserEmail() + "");
        }


        initListener();
        initPermission();
        if (!InitApp.isInitComplete()) {
            InitApp.init(this);//初始化程序数据
        }

        String jsonStr = getIntent().getStringExtra(CommonConstant.SEGMENTATION_DOMAIN);
        if (jsonStr != null) {
            SegmentationDomain segmentationDomain = (SegmentationDomain) JSONStringUtil.getObjectFromJSONString(jsonStr, SegmentationDomain.class);
            deal(segmentationDomain);
        }

    }

    /**
     * 初始化监听器
     */
    private void initListener() {

        userInfoImageView.setOnClickListener(this);

        //搜索栏部分
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                v.setFocusable(false);
                searchView.setFocusable(false);
                searchView.clearFocus();
                startActivity(intent);
            }
        });

        //菜单部分
        navView.setNavigationItemSelectedListener(this);
        navView.setCheckedItem(R.id.nav_call);

        //浮动按钮部分
        fab.setOnLongClickListener(view -> {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
            fab.setEnabled(false);
            openAsrAndStopWakeup();
            return false;
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
        }
        return true;
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
//                Toast.makeText(this, "没有权限，将无法使用程序", Toast.LENGTH_LONG).show();
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Menu模块
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.nav_location:
                intent = new Intent(MainActivity.this, TrademarkActivity.class);
                break;
            case R.id.nav_search:
                intent = new Intent(MainActivity.this, SearchActivity.class);
                break;
            case R.id.nav_trademark:
                intent = new Intent(this, OptionsActivity.class);
                break;
            case R.id.nav_similar_trademark_enquiries:
                intent = new Intent(this, SimilarTrademarkEnquiriesActivity.class);
                break;
            case R.id.nav_task:
                intent = new Intent(this, SettingActivity.class);
                break;
            case R.id.nav_call:
                mDrawerLayout.closeDrawers();
                break;

        }
        if (intent != null) {
            startActivity(intent);
            mDrawerLayout.closeDrawers();
        }

        return false;
    }

    /**
     * 自定义对话框
     */
    private void customDialog(final Context context) {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.item_dialog_normal, null);
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
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.user_icon_button:
                if (UserInfo.isIsUserOnline()) {
                    intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    userName.setText(user.getUserName());
                } else {
                    customDialog(this);
                }
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void deal(SegmentationDomain segmentationDomain) {
        SegmentationDomain.Results results = segmentationDomain.getResults();
        Map<String, String> obj = results.getObject();
        Map<String, String> replenish = results.getReplenish();
        Map<String, String> intent = results.getIntent();//这里的intent是动作的意思
        Fragment fragment;
        Log.d(TAG, "deal: " + results.getDomain());
        switch (segmentationDomain.getResults().getDomain()) {
            case WordSegCommonConstant.TRADEMARK_DOMAIN:
                if (fragmentMap.get(WordSegCommonConstant.TRADEMARK_DOMAIN) != null) {
                    fragment = fragmentMap.get(WordSegCommonConstant.TRADEMARK_DOMAIN);
                    fm.beginTransaction().addToBackStack("1").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    ((TrademarkFragment) fragment).update(obj);
                    fragmentType = 2;
                } else {
                    fragment = fragmentMap.get(WordSegCommonConstant.NEWS_DOMAIN);
                    fm.beginTransaction().addToBackStack("0").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    fragmentType = 1;
                }
                break;
            case WordSegCommonConstant.FILE_DOMAIN:
                if (fragmentMap.get(WordSegCommonConstant.FILE_DOMAIN) != null) {
                    fragment = fragmentMap.get(WordSegCommonConstant.FILE_DOMAIN);
                    fm.beginTransaction().addToBackStack("2").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    fragmentType = 3;
                } else {
                    fragment = fragmentMap.get(WordSegCommonConstant.NEWS_DOMAIN);
                    fm.beginTransaction().addToBackStack("0").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    fragmentType = 1;
                }

                break;
            case WordSegCommonConstant.ATTRIBUTE_DOMAIN:
                if (fragmentMap.get(WordSegCommonConstant.ATTRIBUTE_DOMAIN) != null) {
                    fragment = fragmentMap.get(WordSegCommonConstant.ATTRIBUTE_DOMAIN);
                    fm.beginTransaction().addToBackStack("3").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    ((TrademarkAttributeFragment) fragment).update(obj);
                    fragmentType = 4;

                } else {
                    fragment = fragmentMap.get(WordSegCommonConstant.NEWS_DOMAIN);
                    fm.beginTransaction().addToBackStack("0").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    fragmentType = 1;
                }
                break;
            case WordSegCommonConstant.SIMILARITY_TEXT_DOMAIN:
                if (fragmentMap.get(WordSegCommonConstant.SIMILARITY_TEXT_DOMAIN) != null) {
                    fragment = fragmentMap.get(WordSegCommonConstant.SIMILARITY_TEXT_DOMAIN);
                    fm.beginTransaction().addToBackStack("4").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    ((SimilarTrademarkFragment) fragment).update(obj);
                    fragmentType = 5;

                } else {
                    fragment = fragmentMap.get(WordSegCommonConstant.NEWS_DOMAIN);
                    fm.beginTransaction().addToBackStack("0").replace(R.id.container, fragment).commitAllowingStateLoss();
                    fm.executePendingTransactions();
                    fragmentType = 1;
                }
                break;
            case WordSegCommonConstant.OTHERS_DOMAIN:
                break;
            case WordSegCommonConstant.ERROR_DOMAIN:
                break;
            default:
                speak("不好意思，未能找到相应的结果");
                break;
        }
    }

    @Override
    protected void endOfAsr(){
        fab.setEnabled(true);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_start));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragmentType != 1) {
            Fragment fragment = fragmentMap.get(WordSegCommonConstant.NEWS_DOMAIN);
            fm.beginTransaction().addToBackStack("0").replace(R.id.container, fragment).commitAllowingStateLoss();
            fm.executePendingTransactions();
            fragmentType = 1;
        } else {
            this.finish();
        }

        Log.d(TAG, "onBackPressed: 返回");
    }


    public static class MainActivityManager{

        private static MainActivity mainActivity;

        private MainActivityManager(){}

        public static MainActivity getMainActivity() {
            return mainActivity;
        }

        public static void setMainActivity(MainActivity mainActivity) {
            MainActivityManager.mainActivity = mainActivity;
        }

    }

    public void dealByOthersActivity(SegmentationDomain segmentationDomain){
        if (segmentationDomain != null) {
            deal(segmentationDomain);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = UserInfo.getUser();
        if (user != null && user.getUserName() != null) {
            userName.setText(user.getUserName());
        }

    }
}
