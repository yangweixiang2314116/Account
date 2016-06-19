package com.example.account;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;
import com.example.module.AccountRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

import cz.msebera.android.httpclient.Header;


public class AccountFeedbackActivity extends ActionBarActivity {
    private EditText mFeedback;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_feedback);
        mContext = this;
        mFeedback = (EditText) findViewById(R.id.suggestion);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_feedback) {
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
                        Log.i(Constants.TAG, "---postFeedback--onSuccess--response---" + response);

                        Toast.makeText(mContext, R.string.account_feedback_thanks, Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        super.onFailure(statusCode, headers, throwable, response);
                        Log.i(Constants.TAG, "---postFeedback--onFailure--statusCode---" + statusCode);

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
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

