package com.example.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.module.AccountRestClient;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/6/21.
 */
public class AccountSettingActivity  extends ActionBarActivity implements
        OnClickListener, OnCheckedChangeListener{

    private SharedPreferences mSharedPreferences;

    private View mSuggestion;
    private View mRateForUs;
    private ToggleButton mSwitchOnlyWifi;
    private ToggleButton mSwitchQuickAdd;
    private RelativeLayout mRecommand;
    private LayoutInflater mLayoutInflater = null;
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
       mRecommand = (RelativeLayout)findViewById(R.id.recommend);
        mSuggestion = findViewById(R.id.suggestion);
        //mFocusUs = findViewById(R.id.focus_us);
        //mCancelAuth = findViewById(R.id.cancel_auth);
        mRateForUs = findViewById(R.id.rate_for_us);
        //mClearCache = findViewById(R.id.clear_cache);

        mSwitchOnlyWifi = (ToggleButton) findViewById(R.id.switch_wifi);
        mSwitchQuickAdd = (ToggleButton) findViewById(R.id.switch_quick_add);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        mRecommand.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_ShowSettingUrlWindow();
            }
        });
        mSuggestion.setOnClickListener(this);
       // mFocusUs.setOnClickListener(this);
        mSwitchOnlyWifi.setOnCheckedChangeListener(this);
        mSwitchQuickAdd.setOnCheckedChangeListener(this);
        //mCancelAuth.setOnClickListener(this);
        mRateForUs.setOnClickListener(this);
        //mClearCache.setOnClickListener(this);

        mSwitchOnlyWifi.setChecked(mSharedPreferences.getBoolean("only_wifi",
                true));
        mSwitchQuickAdd.setChecked(mSharedPreferences.getBoolean("quick_add", true));

        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        MobclickAgent.onEvent(mContext, "enter_setting");
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_wifi:
                mSharedPreferences.edit().putBoolean("only_wifi", isChecked)
                        .apply();
                break;
            case R.id.switch_quick_add:
                mSharedPreferences.edit().putBoolean("quick_add", isChecked).apply();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suggestion:
                Intent intent = new Intent(mContext, AccountFeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.rate_for_us:
                Uri uri = Uri.parse("market://details?id="
                        + mContext.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                    //MobclickAgent.onEvent(mContext, "rate");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, R.string.can_not_open_market,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(Constants.TAG, "-----AccountStartActivity- onOptionsItemSelected-------");
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

    private void m_ShowSettingUrlWindow()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        final View messageContent = mLayoutInflater.inflate(
                R.layout.dialog_setting_url, null);
        builder.setView(messageContent);
        builder.setTitle(R.string.recommend_to_friend);

        builder.setPositiveButton(R.string.btn_name_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText content = (EditText)messageContent.findViewById(R.id.account_setting_url);

                Log.i(Constants.TAG, "-----account_setting_url-------" + content.getText().toString());

                AccountRestClient.instance(mContext).setServerUrl(content.getText().toString());

                Toast.makeText(AccountSettingActivity.this, R.string.account_feedback_success, Toast.LENGTH_SHORT).show();

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
