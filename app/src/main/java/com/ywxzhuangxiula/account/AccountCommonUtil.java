package com.ywxzhuangxiula.account;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

import com.ywxzhuangxiula.module.NetworkUtils;


public class AccountCommonUtil {

    static String ConverDateToString(long date) {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdformat.format(date);
        Log.i(Constants.TAG, "-----------ConverDateToString----sDate----" + sDate);
        return sDate;
    }

    static String ConverWholeDateToString(long date) {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sDate = sdformat.format(date);
        Log.i(Constants.TAG, "-----------ConverDateToString----whole sDate----" + sDate);
        return sDate;
    }

    //yyyy-MM-dd hh:mm:ss
    public static Long ConverStringToDate(String date) {
        Log.i(Constants.TAG, "-----------ConverDateToString---before convert ----" + date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long time = 0;
        try {
            time = format.parse(date).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.i(Constants.TAG, "-----------ConverDateToString---- check Date----" + ConverDateToString(time));

        return time;
    }

    //yyyy-MM-dd
    public static Long ConverStringToDateWithoutTime(String date) {
        Log.i(Constants.TAG, "-----------ConverDateToString---before convert ----" + date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long time = 0;
        try {
            time = format.parse(date).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.i(Constants.TAG, "-----------ConverDateToString---- check Date----" + ConverDateToString(time));

        return time;
    }

    /**
     * dpתpx
     *
     * @param context
     * @param val
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    public static boolean IsLogin(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getBoolean(
                "is_login", false);
    }

	    public static boolean SetLogin(Context context, boolean login) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putBoolean("is_login", login)
                .apply();
        return true;
    }

    public static boolean IsShowLikePopup(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getBoolean(
                "is_like", false);
    }

    //like popup just show one time
    public static boolean SetShowLikePopup(Context context, boolean show) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putBoolean("is_like", show)
                .apply();
        return true;
    }

    public static boolean IsFirstEnter(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getBoolean(
                "is_first", true);
    }

    public static boolean SetNotFirstEnter(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putBoolean("is_first", false)
                .apply();
        return true;
    }

    public static boolean IsSupportSync(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getBoolean(
                "is_support_sync", false);
    }

    public static void setSupportSync(Context context, boolean support) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putBoolean("is_support_sync", support)
                .apply();
    }


    public static boolean IsOnlyWifi(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getBoolean(
                "only_wifi", true);
    }

    public static boolean IsQuickStart(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getBoolean(
                "quick_add", false);
    }

    public static String GetToken(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "token", "");
    }

    public static boolean SetToken(Context context, String token) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putString("token", token)
                .apply();
        return true;
    }

    public static String GetGudieStyle(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "guide_style", "");
    }

    public static boolean SetGudieStyle(Context context, String style) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        Log.d(Constants.TAG, "----SetGudieStyle----"+style);

        pSharedPreferences.edit().putString("guide_style", style)
                .apply();
        return true;
    }

    public static String GetGudieCompany(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "guide_company", "");
    }

    public static boolean SetGudieCompany(Context context, String company) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        Log.d(Constants.TAG, "----SetGudieCompany----"+company);

        pSharedPreferences.edit().putString("guide_company", company)
                .apply();
        return true;
    }

    public static String GetGudieBudget(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "guide_budget", "");
    }

    public static boolean SetGudieBudget(Context context, String budget) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putString("guide_budget", budget)
                .apply();
        return true;
    }

    public static String GetGudieArea(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "guide_area", "");
    }

    public static String GetUserName(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "user_name", "");
    }


    public static boolean SetGudieArea(Context context, String area) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putString("guide_area", area)
                .apply();
        return true;
    }

    public static long GetUserProfileId(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getLong(
                "user_profile", 0);
    }

    public static boolean SetUserProfileId(Context context, long id) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putLong("user_profile", id)
                .apply();
        return true;
    }

    public static String GetCurrentCity(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "city", "");
    }

    public static boolean SetCurrentCity(Context context, String city) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putString("city", city)
                .apply();
        return true;
    }

    public static boolean SetServerUrl(Context context, String url) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        pSharedPreferences.edit().putString("url", url)
                .apply();
        return true;
    }

    public static String GetServerUrl(Context context) {
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return pSharedPreferences.getString(
                "url", "");
    }

    public static void sendBroadcastForAccountDataChange(Context context) {
        Intent intent = new Intent(Constants.INTENT_NOTIFY_ACCOUNT_CHANGE);
        context.sendBroadcast(intent);
    }

	public static void sendBroadcastForAccountInvalidToken(Context context) {
        Intent intent = new Intent(Constants.INTENT_NOTIFY_INVALID_TOKEN);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcastForAccountStartSync(Context context) {
        Intent intent = new Intent(Constants.INTENT_NOTIFY_START_SYNC);
        context.sendBroadcast(intent);
    }


    public static boolean isActivityRunning(String packagename, Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = am.getRunningTasks(1);
        String cmpNameTemp = null;
        if(null != runningTaskInfos){
            cmpNameTemp = runningTaskInfos.get(0).topActivity.toString();
        }
        if(null != cmpNameTemp){
            return cmpNameTemp.contains(packagename);
        }
        return false;
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static boolean CanSyncNow(Context context)
    {
        if (AccountCommonUtil.IsLogin(context)) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                if (AccountCommonUtil.IsOnlyWifi(context)) {
                    if (NetworkUtils.isWifiConnected(context)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return  false;
        }
    }


    public static void openDownLoadService(Context context, String downurl,
                                           String tilte) {
        Log.i(Constants.TAG, "---openDownLoadService--downurl---" + downurl);
        Log.i(Constants.TAG, "---openDownLoadService--tilte---" + tilte);
        /*
        final ICallbackResult callback = new ICallbackResult() {

            @Override
            public void OnBackResult(Object s) {}
        };
        */

        ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {}

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AccountDownloadService.DownloadBinder binder = (AccountDownloadService.DownloadBinder) service;
                //binder.addCallback(callback);
                binder.start();

            }
        };
        Intent intent = new Intent(context, AccountDownloadService.class);
        intent.putExtra(AccountDownloadService.BUNDLE_KEY_DOWNLOAD_URL, downurl);
        intent.putExtra(AccountDownloadService.BUNDLE_KEY_TITLE, tilte);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public static void installAPK(Context context, File file) {
        if (file == null || !file.exists()) {
            Log.i(Constants.TAG, "---installAPK file == null or file not exist---");
            return;
        }

        Log.i(Constants.TAG, "--start to -installAPK file !!!---");

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
