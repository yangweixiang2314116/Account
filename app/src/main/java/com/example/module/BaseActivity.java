package com.example.module;

/**
 * Created by Administrator on 2016/10/18.
 */
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.account.Constants;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "---BaseActivity--onDestroy---" );
        super.onDestroy();
        //结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "---BaseActivity--onCreate---");
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Constants.TAG, "---BaseActivity--onPause---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "---BaseActivity--onResume---");
    }

}
