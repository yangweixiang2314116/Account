package com.example.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.module.BaseActivity;
import com.example.module.NetworkUtils;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class AccountUserInfoActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private Context mContext = null;
    private Button mSubmitButton = null;
    private ListView mInfoList = null;
    private Resources mResources = null;
    private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
    private SimpleAdapter mAdapter = null;
    private LayoutInflater mLayoutInflater = null;
    private WheelView mWheelArea = null;
    private int mWheelType = 0;
    private String mWheelChange = "";
    private ArrayList<String> mWheelListDataSource = new ArrayList<String>();

    private String mCity = "";
    private String mStyle = "";
    private String mBudget = "";
    private String mArea = "";
    private String mCompany = "";

    public static final int ACCOUNT_USER_INFO_CITY = 0;
    public static final int ACCOUNT_USER_INFO_STYLE = 1;
    public static final int ACCOUNT_USER_INFO_BUGET = 2;
    public static final int ACCOUNT_USER_INFO_AREA = 3;
    public static final int ACCOUNT_USER_INFO_COMPANY= 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_push_right_to_left, R.anim.in_stable);
        //setTheme(R.style.MIS_NO_ACTIONBAR);
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

                AccountCommonUtil.SetGudieArea(mContext, mArea);
                AccountCommonUtil.SetGudieBudget(mContext, mBudget);
                AccountCommonUtil.SetGudieStyle(mContext, mStyle);

                if (NetworkUtils.isNetworkAvailable(mContext)) {
                    m_ProcessUserInfoContent();
                } else {
                    finish();
                    overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
                }
            }
        });

        mContext = this;
        mInfoList = (ListView) findViewById(R.id.account_my_info_list);

        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        getData();


        String[] from = {"label", "value"};
        int[] to = {R.id.user_info_item_label, R.id.user_info_item_value};
        mAdapter = new SimpleAdapter(mContext, mDataList, R.layout.activity_account_user_info_list_item, from, to);
        mInfoList.setAdapter(mAdapter);
        mInfoList.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        //TODO add to Mobclick
        MobclickAgent.onEvent(mContext, "enter_userinfo");
    }

    public List<Map<String, Object>> getData() {
        TypedArray infoItems = mResources.obtainTypedArray(R.array.user_info);

        mDataList.clear();
        for (int i = 0; i < infoItems.length(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();

            String value = getString(R.string.account_my_user_info_unknow);
            switch (i) {
                case ACCOUNT_USER_INFO_CITY:
                    mCity = AccountCommonUtil.GetCurrentCity(mContext);
                    value = mCity;
                    break;
                case ACCOUNT_USER_INFO_STYLE:
                    mStyle = AccountCommonUtil.GetGudieStyle(mContext);
                    value = mStyle;
                    break;
                case ACCOUNT_USER_INFO_BUGET:
                    mBudget = AccountCommonUtil.GetGudieBudget(mContext);
                    value = mBudget;
                    break;
                case ACCOUNT_USER_INFO_AREA:
                    mArea = AccountCommonUtil.GetGudieArea(mContext);
                    value = mArea;
                    break;
                case ACCOUNT_USER_INFO_COMPANY:
                    mCompany = AccountCommonUtil.GetGudieCompany(mContext);
                    if(mCompany.equals("") == false)
                    {
                        value = mCompany;
                    }
                    break;
                default:
                    break;
            }
            map.put("label", infoItems.getString(i));
            map.put("value", value);
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

        Log.i(Constants.TAG, "---onItemClick-----" + position);

        if(position == ACCOUNT_USER_INFO_CITY)
        {
            Log.i(Constants.TAG, "---onItemClick----ACCOUNT_USER_INFO_CITY-- do nothing");
            Toast.makeText(mContext, R.string.account_user_city_cannot_change, Toast.LENGTH_SHORT).show();
            //do nothing
        }
        else if (position == ACCOUNT_USER_INFO_COMPANY){
            Log.i(Constants.TAG, "---onItemClick----ACCOUNT_USER_INFO_COMPANY---");
            m_ShowGuideCompany();
        }
        else {
            m_ShowWheelPopup(position);
        }
    }

    public boolean m_ProcessUserInfoContent() {
        Log.i(Constants.TAG, "---m_ProcessUserInfoContent-----");
        AccountApiConnector.instance(mContext).updateUserInfo(mCity,
                mStyle, mBudget, mArea, mCompany, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        Log.i(Constants.TAG, "---postUserInfo--onSuccess--response---" + response);

                        Toast.makeText(mContext, R.string.account_feedback_success, Toast.LENGTH_SHORT).show();

                        finish();
                        overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.i(Constants.TAG, "---postUserInfo--onFailure--statusCode---" + statusCode);

                        Toast.makeText(mContext, R.string.account_feedback_failed, Toast.LENGTH_SHORT).show();

                        finish();
                        overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
                    }

                    @Override
                    public void onFinish() {
                        Log.i(Constants.TAG, "---postUserInfo--onFinish-----");
                        super.onFinish();
                    }

                });

        return true;
    }

    void m_ShowWheelPopup(int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View messageContent = mLayoutInflater.inflate(
                R.layout.dialog_wheel_type, null);

        mWheelType = type;
        mWheelArea = (WheelView) messageContent.findViewById(R.id.dialog_wheel_view_area);

        TypedArray dataItems = null;

        switch (mWheelType) {
            case ACCOUNT_USER_INFO_AREA:
                dataItems = mResources.obtainTypedArray(R.array.guide_chose_area_text);
                break;
            case ACCOUNT_USER_INFO_BUGET:
                dataItems = mResources.obtainTypedArray(R.array.guide_chose_budget_text);
                break;
            case ACCOUNT_USER_INFO_STYLE:
                dataItems = mResources.obtainTypedArray(R.array.guide_chose_style);
                break;
            default:
                break;
        }

        mWheelListDataSource.clear();
        for (int i = 0; i < dataItems.length(); i++) {
            mWheelListDataSource.add(dataItems.getString(i));
        }

        mWheelArea.setOffset(2);

        int  nFocusIndex = m_GetSelectionIndex();
        Log.d(Constants.TAG, "----nFocusIndex----"+nFocusIndex);
        mWheelArea.setSeletion(nFocusIndex);
        Log.d(Constants.TAG, "onCreateView: setSeletion !");

        mWheelArea.setItems(mWheelListDataSource);
        Log.d(Constants.TAG, "onCreateView: setItems !");

        mWheelArea.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(Constants.TAG, "selectedIndex: " + selectedIndex + ", item: " + item);

                mWheelChange = item;
            }
        });

        builder.setView(messageContent);
        builder.setPositiveButton(R.string.give_up_sure,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (mWheelType) {
                            case ACCOUNT_USER_INFO_AREA:
                                AccountCommonUtil.SetGudieArea(mContext, mWheelChange);
                                mArea = mWheelChange;
                                m_UpdateUserInfoList(mWheelType, mWheelChange);
                                break;
                            case ACCOUNT_USER_INFO_BUGET:
                                AccountCommonUtil.SetGudieBudget(mContext, mWheelChange);
                                mBudget = mWheelChange;
                                m_UpdateUserInfoList(mWheelType, mWheelChange);
                                break;
                            case ACCOUNT_USER_INFO_STYLE:
                                mStyle = mWheelChange;
                                AccountCommonUtil.SetGudieStyle(mContext, mWheelChange);
                                m_UpdateUserInfoList(mWheelType, mWheelChange);
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.give_up_cancel, null)
                .create().show();

    }

    private boolean m_UpdateUserInfoList(int index, String value) {

        Log.d(Constants.TAG, "m_UpdateUserInfoList: index !"+index+ "--value--"+value);

        /*
        View InfoItem = mInfoList.getChildAt(index);
        TextView itemValue = (TextView) InfoItem
                .findViewById(R.id.user_info_item_value);
        Log.d(Constants.TAG, "TextView itemValue !"+itemValue.getText());
        itemValue.setText(value);
        InfoItem.invalidate();
        Log.d(Constants.TAG, "TextView itemValue !"+itemValue.getText());
        */

        getData();
        String[] from = {"label", "value"};
        int[] to = {R.id.user_info_item_label, R.id.user_info_item_value};
        mAdapter = new SimpleAdapter(mContext, mDataList, R.layout.activity_account_user_info_list_item, from, to);

        mInfoList.setAdapter(mAdapter);
        mInfoList.setOnItemClickListener((AdapterView.OnItemClickListener) this);
        mAdapter.notifyDataSetChanged();


        return true;
    }

    private int m_GetSelectionIndex()
    {
        TypedArray dataItems = null;
        String lastValue = "";
        switch (mWheelType) {
            case ACCOUNT_USER_INFO_AREA:
                dataItems = mResources.obtainTypedArray(R.array.guide_chose_area_text);
                lastValue = AccountCommonUtil.GetGudieArea(mContext);
                break;
            case ACCOUNT_USER_INFO_BUGET:
                dataItems = mResources.obtainTypedArray(R.array.guide_chose_budget_text);
                lastValue = AccountCommonUtil.GetGudieBudget(mContext);
                break;
            case ACCOUNT_USER_INFO_STYLE:
                dataItems = mResources.obtainTypedArray(R.array.guide_chose_style);
                lastValue = AccountCommonUtil.GetGudieStyle(mContext);
                break;
            default:
                break;
        }

        for (int i = 0; i < dataItems.length(); i++) {
            if(lastValue.equals(dataItems.getString(i)))
            {
                return i;
            }
        }

        return 0;
    }

    private void m_ShowGuideCompany() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        //AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        final View messageContent = mLayoutInflater.inflate(
                R.layout.dialog_setting_url, null);
        builder.setView(messageContent);
        builder.setTitle(R.string.setting_company);

        builder.setPositiveButton(R.string.btn_name_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText content = (EditText) messageContent.findViewById(R.id.account_setting_url);

                Log.i(Constants.TAG, "-----account_company-------" + content.getText().toString());

                AccountCommonUtil.SetGudieCompany(mContext, content.getText().toString());

                m_UpdateUserInfoList(ACCOUNT_USER_INFO_COMPANY, content.getText().toString());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_name_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}