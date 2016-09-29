package com.example.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.example.module.Account;
import com.example.module.BrandHistory;
import com.example.module.CategoryHistory;
import com.example.module.OnlineHistory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;


public class AccountLoadActivity extends Activity {

    private Runnable runnable = new Runnable( ) {

        public void run ( ) {
            m_CheckFirst();
        }

    };
    private Resources mResources = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Constants.TAG, "The AccountLoadActivity---->onCreate");

        setContentView(R.layout.activity_account_load);
        MobclickAgent.setDebugMode(true);

        AccountCommonUtil.setSupportSync(this, true);
        mResources = this.getResources();

        boolean mFirst = AccountCommonUtil.IsFirstEnter(this);
        if(mFirst) {
            new InitialDataTask().execute();
        }
        else
        {
            mHandler.postDelayed(runnable, 1000);
        }
    }

    private void m_CheckFirst()
    {
        Log.i(Constants.TAG, "The AccountLoadActivity---->m_CheckFirst");

        boolean mFirst = AccountCommonUtil.IsFirstEnter(this);

       // mFirst = true;//TODO delete
        if(mFirst) {
            if(AccountCommonUtil.IsSupportSync(this)) {
                mHandler.sendEmptyMessageDelayed(SWITCH_GUIDECTIVITY, 100);
                //AccountCommonUtil.SetNotFirstEnter(this);
            }
            else{
                mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 100);
            }
        }
        else {
          mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 100);
        }
    }

    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDECTIVITY = 1001;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SWITCH_MAINACTIVITY:
                    Intent mIntent = new Intent();
                    mIntent.setClass(AccountLoadActivity.this, AccountTotalActivity.class);
                    startActivity(mIntent);
                    finish();
                    break;
                case SWITCH_GUIDECTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(AccountLoadActivity.this, AccountGuideActivity.class);
                    startActivity(mIntent);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(Constants.TAG, "The AccountLoadActivity---->onDestroy");

        mHandler.removeCallbacks(runnable);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void m_InitialHotLabel()
    {
        Log.i(Constants.TAG, "------m_InitialHotLabel-----start---");
        ActiveAndroid.beginTransaction();
        try {

            TypedArray infoItems = mResources.obtainTypedArray(R.array.hot_label_history_text);

            for(int i=0;i<infoItems.length() ;i++){

                CategoryHistory item = new CategoryHistory();
                item.Content = infoItems.getString(i);
                item.LastUseTime = System.currentTimeMillis();
                item.save();
            }

            infoItems.recycle();

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

    }

    private void m_InitialHotBrand()
    {
        Log.i(Constants.TAG, "------m_InitialHotBrand-----start---");
        ActiveAndroid.beginTransaction();
        try {

            TypedArray infoItems = mResources.obtainTypedArray(R.array.hot_brand_history_text);

            for(int i=0;i<infoItems.length() ;i++){

                BrandHistory item = new BrandHistory();
                item.Content = infoItems.getString(i);
                item.LastUseTime = System.currentTimeMillis();
                item.save();
            }

            infoItems.recycle();

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

    }

    private void m_InitialOnlineShopping()
    {
        Log.i(Constants.TAG, "------m_InitialOnlineShopping-----start---");
        ActiveAndroid.beginTransaction();
        try {

            TypedArray infoItems = mResources.obtainTypedArray(R.array.buy_online_website_text);

            for(int i=0;i<infoItems.length() ;i++){

                OnlineHistory item = new OnlineHistory();
                item.Content = infoItems.getString(i);
                item.LastUseTime = System.currentTimeMillis();
                item.save();
            }

            infoItems.recycle();

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

    }

    private class InitialDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Log.i(Constants.TAG, "------start get all account from DB--------");

            m_InitialHotLabel();

            m_InitialHotBrand();

            m_InitialOnlineShopping();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(Constants.TAG, "------onPostExecute--------");
            super.onPostExecute(result);
            mHandler.postDelayed(runnable, 1000);
        }
    }
}