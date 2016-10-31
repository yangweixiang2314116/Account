package com.example.module;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.account.AccountApiConnector;
import com.example.account.AccountCommonUtil;
import com.example.account.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UpdateManager {

    private Update mUpdate;

    private Context mContext;

    private boolean isShow = false;

    private Handler mHandler = new Handler();;

    private ProgressDialog _waitDialog;

    private JsonHttpResponseHandler mCheckUpdateHandle = new JsonHttpResponseHandler() {

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.i(Constants.TAG, "---getVersionInfo--onFailure--statusCode---" + statusCode);
            Log.i(Constants.TAG, "---getVersionInfo--onFailure--responseString---" + responseString);

            hideCheckDialog();
            if (isShow) {
                showFaileDialog();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(Constants.TAG, "---getVersionInfo--onSuccess--statusCode---" + statusCode);
            Log.i(Constants.TAG, "---getVersionInfo--onSuccess--responseString---" + response);
            hideCheckDialog();

            mUpdate = Update.build(response);
            onFinishCheck();

        }
    };

    public UpdateManager(Context context, boolean isShow) {
        this.mContext = context;
        this.isShow = isShow;
    }

    public boolean haveNew() {
        if (this.mUpdate == null) {
            return false;
        }
        boolean haveNew = false;
        int curVersionCode = AccountCommonUtil.getVersionCode(mContext);

        Log.i(Constants.TAG, "---haveNew--curVersionCode---" + curVersionCode);
        Log.i(Constants.TAG, "---haveNew--version on server---" +  mUpdate.getVersionCode());

        if (curVersionCode < mUpdate.getVersionCode()) {
            haveNew = true;
        }
        return haveNew;
    }

    public class AccountGetVersionRunable implements Runnable {

        public AccountGetVersionRunable() {

        }

        public void run() {
            Log.i(Constants.TAG, "----getVersionInfo ---run----");

            AccountApiConnector.instance(mContext).getVersionInfo(mCheckUpdateHandle);
        }

    }
    public void checkUpdate() {
        if (isShow) {
            showCheckDialog();
            mHandler.postDelayed(new AccountGetVersionRunable(), 3000);
        }
        else {
            mHandler.post(new AccountGetVersionRunable());
            Log.i(Constants.TAG, "----checkUpdate ---end----");
        }
    }

    private void onFinishCheck() {
        if (haveNew()) {
            showUpdateInfo();
        } else {
            if (isShow) {
                showLatestDialog();
            }
            else {
                AccountCommonUtil.sendBroadcastForAccountStartSync(mContext);
            }
        }
    }

    private void showCheckDialog() {
        if (_waitDialog == null) {
            _waitDialog = DialogHelp.getWaitDialog((Activity) mContext, "正在获取新版本信息...");
        }
        _waitDialog.show();
    }

    private void hideCheckDialog() {
        if (_waitDialog != null) {
            _waitDialog.dismiss();
        }
    }

    private void showUpdateInfo() {
        if (mUpdate == null) {
            return;
        }
        AlertDialog.Builder dialog = DialogHelp.getMessageDialog(mContext, mUpdate.getUpdateLog(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountCommonUtil.openDownLoadService(mContext, mUpdate.getDownloadUrl(), mUpdate.getVersionName());
            }
        });
        dialog.setTitle("发现新版本");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showLatestDialog() {
        DialogHelp.getMessageDialog(mContext, "已经是新版本了").show();
    }

    private void showFaileDialog() {
        DialogHelp.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
    }
}