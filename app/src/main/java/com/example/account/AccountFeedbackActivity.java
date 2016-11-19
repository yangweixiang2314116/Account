package com.example.account;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

import com.example.module.BaseActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;



public class AccountFeedbackActivity extends BaseActivity {
    private EditText mFeedback = null;
    private Context mContext ;
    private Button mSubmitButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_push_right_to_left, R.anim.in_stable);
        setTheme(R.style.MIS_NO_ACTIONBAR);

        setContentView(R.layout.activity_account_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.feedback_toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(getString(R.string.account_comment));
        }

        mContext = this;
        mFeedback = (EditText) findViewById(R.id.suggestion);
        mSubmitButton = (Button) findViewById(R.id.commit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_ProcessFeedBackContent();
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
            }
        });
        MobclickAgent.onEvent(mContext, "enter_feedback");
    }


     boolean m_ProcessFeedBackContent()
    {
            String feedback = mFeedback.getText().toString();
            if (feedback.length() == 0) {
                Toast.makeText(mContext, R.string.account_feedback_empty, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(Constants.TAG, "---postFeedback--feedback---" + feedback);
                AccountApiConnector.instance(mContext).postFeedback(feedback, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        Log.i(Constants.TAG, "---postFeedback--onS" +
                                "uccess--response---" + response);

                        Toast.makeText(mContext, R.string.account_feedback_thanks, Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.i(Constants.TAG, "---postUserInfo--onFailure--statusCode---" + statusCode);
                        Log.i(Constants.TAG, "---postUserInfo--onFailure--responseString---" + responseString);
                        Toast.makeText(mContext, R.string.account_feedback_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Log.i(Constants.TAG, "---postFeedback--onFinish-----" );
                        super.onFinish();
                    }

                });
            }
            return true;
        }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //hide soft input
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mFeedback.getWindowToken(), 0);

                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

