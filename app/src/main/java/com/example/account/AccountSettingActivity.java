package com.example.account;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by Administrator on 2016/6/21.
 */
public class AccountSettingActivity  extends ActionBarActivity implements
        OnClickListener, OnCheckedChangeListener{

    private SharedPreferences mSharedPreferences;

    private ToggleButton mSwitchOnlyWifi;
    private ToggleButton mSwitchQuickAdd;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //ShareSDK.initSDK(mContext);
        setContentView(R.layout.activity_account_setting);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
       // mRecommand = findViewById(R.id.recommend);
        //mSuggestion = findViewById(R.id.suggestion);
        //mFocusUs = findViewById(R.id.focus_us);
        //mCancelAuth = findViewById(R.id.cancel_auth);
        //mRateForUs = findViewById(R.id.rate_for_us);
        //mClearCache = findViewById(R.id.clear_cache);

        mSwitchOnlyWifi = (ToggleButton) findViewById(R.id.switch_wifi);
        mSwitchQuickAdd = (ToggleButton) findViewById(R.id.switch_quick_add);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        //mRecommand.setOnClickListener(this);
        //mSuggestion.setOnClickListener(this);
       // mFocusUs.setOnClickListener(this);
        mSwitchOnlyWifi.setOnCheckedChangeListener(this);
        mSwitchQuickAdd.setOnCheckedChangeListener(this);
        //mCancelAuth.setOnClickListener(this);
        //mRateForUs.setOnClickListener(this);
        //mClearCache.setOnClickListener(this);

        mSwitchOnlyWifi.setChecked(mSharedPreferences.getBoolean("only_wifi",
                true));
        mSwitchQuickAdd.setChecked(mSharedPreferences.getBoolean("quick_add", true));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onClick(View v) {

    }
}
