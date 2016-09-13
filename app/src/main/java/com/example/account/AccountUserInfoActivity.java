package com.example.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AccountUserInfoActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{
    private Context mContext = null;
    private Button mSubmitButton = null;
    private ListView mInfoList = null;
    private Resources mResources = null;
    private List<Map<String, Object>> mDataList  = new ArrayList<Map<String, Object>>();
    private SimpleAdapter mAdapter = null;

    public static final int ACCOUNT_USER_INFO_CITY = 0;
    public static final int ACCOUNT_USER_INFO_STYLE = 1;
    public static final int ACCOUNT_USER_INFO_BUGET = 2;
    public static final int ACCOUNT_USER_INFO_AREA = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_push_right_to_left, R.anim.in_stable);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.activity_account_my_user_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_info_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(getString(R.string.account_my_user_info));
        }

        mResources = this.getResources();
        mSubmitButton = (Button) findViewById(R.id.commit);
        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
        }});

        mContext = this;
        mInfoList = (ListView)findViewById(R.id.account_my_info_list);

        getData();


        String [] from ={"label","value"};
        int [] to = {R.id.user_info_item_label, R.id.user_info_item_value};
        mAdapter = new SimpleAdapter(mContext, mDataList, R.layout.activity_account_user_info_list_item, from, to);
        mInfoList.setAdapter(mAdapter);
        mInfoList.setOnItemClickListener((AdapterView.OnItemClickListener) this);

            //TODO add to Mobclick
        MobclickAgent.onEvent(mContext, "enter_userinfo");
    }

    public List<Map<String, Object>> getData(){
        TypedArray infoItems = mResources.obtainTypedArray(R.array.user_info);

        mDataList.clear();
        for(int i=0;i<infoItems.length() ;i++){
            Map<String, Object> map = new HashMap<String, Object>();

            String value = getString(R.string.account_my_user_info_unknow);
            switch(i)
            {
                case ACCOUNT_USER_INFO_CITY:
                    //TODO
                    break;
                case ACCOUNT_USER_INFO_STYLE:
                    value = AccountCommonUtil.GetGudieStyle(mContext);
                    break;
                case ACCOUNT_USER_INFO_BUGET:
                    value = AccountCommonUtil.GetGudieBudget(mContext);
                    break;
                case ACCOUNT_USER_INFO_AREA:
                    value = AccountCommonUtil.GetGudieArea(mContext);
                    break;
                default:
                    break;
            }
            map.put("label", infoItems.getString(i));
            map.put("value",value);
            mDataList.add(map);
        }

        return mDataList;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}