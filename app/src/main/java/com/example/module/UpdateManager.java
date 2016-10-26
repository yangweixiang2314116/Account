package com.example.module;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.account.AccountApiConnector;
import com.example.account.AccountCommonUtil;
import com.example.account.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UpdateManager {

    private Update mUpdate;

    private Context mContext;

    private boolean isShow = false;

    private ProgressDialog _waitDialog;

    private JsonHttpResponseHandler mCheckUpdateHandle = new JsonHttpResponseHandler() {

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {

            Log.i(Constants.TAG, "---getVersionInfo--onFailure--statusCode---" + statusCode);
            Log.i(Constants.TAG, "---getVersionInfo--onFailure--responseString---" + response);

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

    public void checkUpdate() {
        if (isShow) {
            showCheckDialog();
        }

        AccountApiConnector.instance(mContext).getVersionInfo(mCheckUpdateHandle);
    }

    private void onFinishCheck() {
        if (haveNew()) {
            showUpdateInfo();
        } else {
            if (isShow) {
                showLatestDialog();
            }
            AccountCommonUtil.sendBroadcastForAccountStartSync(mContext);
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