package com.example.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class AccountLoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //boolean mFirst = AccountCommonUtil.IsFirstEnter(this);

       // if(mFirst) {
       //     mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 100);
       //     AccountCommonUtil.SetNotFirstEnter(this);
        //}
        //else {
            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 100);
        //}
    }


    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SWITCH_MAINACTIVITY:
                    Intent mIntent = new Intent();
                    mIntent.setClass(AccountLoadActivity.this, AccountTotalActivity.class);
                    startActivity(mIntent);
                    finish();
                    break;
                case SWITCH_GUIDACTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(AccountLoadActivity.this, AccountGuideActivity.class);
                    startActivity(mIntent);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}