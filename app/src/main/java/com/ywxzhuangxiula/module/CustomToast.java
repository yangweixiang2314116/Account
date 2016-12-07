package com.ywxzhuangxiula.module;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.ywxzhuangxiula.account.AccountCommonUtil;

public class CustomToast {
    private static Toast mToast = null;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };
    public static void showToast(Context context, String text, int duration) {
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        mHandler.postDelayed(r, duration);

        if(AccountCommonUtil.isActivityRunning(context.getPackageName(), context)){
            mToast.show();
        }
    }
    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getResources().getString(resId), duration);
    }
}
