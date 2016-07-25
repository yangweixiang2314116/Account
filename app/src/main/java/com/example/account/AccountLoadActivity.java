package com.example.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;


public class AccountLoadActivity extends Activity {

    private Runnable runnable = new Runnable( ) {

        public void run ( ) {
            m_CheckFirst();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Constants.TAG, "The AccountLoadActivity---->onCreate");

        setContentView(R.layout.activity_account_load);
        MobclickAgent.setDebugMode( true );
        mHandler.postDelayed(runnable, 1000);
    }

    private void m_CheckFirst()
    {
        Log.i(Constants.TAG, "The AccountLoadActivity---->m_CheckFirst");

        boolean mFirst = AccountCommonUtil.IsFirstEnter(this);

        if(mFirst) {
             mHandler.sendEmptyMessageDelayed(SWITCH_GUIDECTIVITY, 100);
            AccountCommonUtil.SetNotFirstEnter(this);
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
}