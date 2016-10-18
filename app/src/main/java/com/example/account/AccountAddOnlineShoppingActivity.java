package com.example.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.module.BaseActivity;
import com.example.module.OnlineHistory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class AccountAddOnlineShoppingActivity extends BaseActivity {

    private FlowLayout mHotFlowLayout;

    private Intent mIntent = null;
    private Context mContext = null;
    private String  mOnline = "";
    protected ArrayList<OnlineHistory> mOnlineHistoryDataSource = new ArrayList<OnlineHistory>();
    private RelativeLayout mOnlineHistoryLayout = null;
    private Button mSubmitButton = null;
    private EditText mOnlineEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_push_right_to_left, R.anim.in_stable);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.activity_account_online_shopping);

        Toolbar toolbar = (Toolbar) findViewById(R.id.online_toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(getString(R.string.add_position_app_name));
        }

        mHotFlowLayout = (FlowLayout) findViewById(R.id.online_history_content);

        mOnlineHistoryLayout = (RelativeLayout)findViewById(R.id.account_recently_online_part);

        mOnlineEditText = (EditText)findViewById(R.id.account_add_online);

        mSubmitButton = (Button) findViewById(R.id.commit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current = mOnlineEditText.getText().toString();
                if(current.isEmpty() == false) {

                    Log.i(Constants.TAG, "----online--" + current);
                    mIntent.putExtra("online", current);

                    if (false == OnlineHistory.IsExistOnlineContent(current)) {
                        OnlineHistory item = new OnlineHistory();
                        item.Content = current;
                        item.save();
                    } else {
                        Log.i(Constants.TAG, "------mCurSearchContent already on DB--------" + current);
                        OnlineHistory item = OnlineHistory.GetOnlineitemByContent(current);
                        item.LastUseTime = System.currentTimeMillis();
                        item.save();
                    }

                    //hide soft input
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mOnlineEditText.getWindowToken(), 0);

                    setResult(Activity.RESULT_OK, mIntent);
                }

                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
            }
        });

        mIntent = getIntent();
        mOnlineEditText.setText(mOnline);
        mOnlineEditText.setSelection(mOnline.length());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mContext = this;

        TextView clearOnlineButton  = (TextView) findViewById(R.id.clear_online_history);

        Log.i(Constants.TAG, "------setOnClickListener---clear_brand_history--");
        clearOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Constants.TAG, "------enter into onClick---clearCategoryButton--");
                OnlineHistory.deleteAll();
                Toast.makeText(mContext, getString(R.string.accout_search_clear_history_success), Toast.LENGTH_SHORT).show();
                mOnlineHistoryLayout.setVisibility(View.INVISIBLE);
            }
        });

        init();
        MobclickAgent.onEvent(mContext, "enter_online"); //TODO
    }

    public void init() {

        mOnlineHistoryDataSource = (ArrayList<OnlineHistory>) OnlineHistory.GetHistoryItems();

        Log.i(Constants.TAG, "------mSearchHistoryDataSource.size()--------" + mOnlineHistoryDataSource.size());

        if (mOnlineHistoryDataSource.size() > 0) {
            mOnlineHistoryLayout.setVisibility(View.VISIBLE);
            for (int index = 0; index < mOnlineHistoryDataSource.size(); index++) {
                addTextView(mOnlineHistoryDataSource.get(index).Content);
            }
        } else {
            mOnlineHistoryLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void addTextView(String tvName) {

        TextView brandTag = (TextView) LayoutInflater.from(this).inflate(R.layout.flow_layout_item, mHotFlowLayout, false);
        brandTag.setText(tvName);
        brandTag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView tag = (TextView) v;

                String current = tag.getText().toString();

                Log.i(Constants.TAG, "----online--" + current);

                mIntent.putExtra("online", current);
                OnlineHistory item = OnlineHistory.GetOnlineitemByContent(current);
                item.LastUseTime = System.currentTimeMillis();
                item.save();

                //hide soft input
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mOnlineEditText.getWindowToken(), 0);

                setResult(Activity.RESULT_OK, mIntent);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);

            }
        });

        mHotFlowLayout.addView(brandTag);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //hide soft input
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mOnlineEditText.getWindowToken(), 0);

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
}