package com.ywxzhuangxiula.module;

import android.util.Log;

import com.ywxzhuangxiula.account.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/20.
 */
public class Update {
    private int versionCode;
    private String versionName;
    private String downloadUrl;
    private String updateLog;
    //private String coverUpdate;
    //private String coverStartDate;
    //private String coverEndDate;
    //private String coverURL;

    private Update(int code, String name, String url,
                    String log) {
        versionCode = code;
        versionName = name;
        downloadUrl = url;
        updateLog  = log;
    }

    int getVersionCode()
    {
        return versionCode;
    }

    String getVersionName()
    {
        return versionName;
    }

    String getDownloadUrl()
    {
        return downloadUrl;
    }

    String getUpdateLog()
    {
        return updateLog;
    }

    /*
    String getCoverUpdate()
    {
        return coverUpdate;
    }

    String getCoverStartDate()
    {
        return coverStartDate;
    }

    String getCoverEndDate()
    {
        return coverEndDate;
    }

    String getCoverURL()
    {
        return coverURL;
    }
    */

    public static Update build(JSONObject response) {

        Update item = null;
        try {
            int versionCode = response.isNull("version_code") ? 0 : response.getInt("version_code");
            String versionName = response.isNull("version_name") ? "" : response.getString("version_name");
            String downloadUrl = response.isNull("download_url") ? "" : response.getString("download_url");
            String updateLog = response.isNull("update_log") ? "" : response.getString("update_log");

            Log.i(Constants.TAG, "--Update--build-versionCode-" + versionCode);
            Log.i(Constants.TAG, "--Update--build-versionName-" + versionName);
            Log.i(Constants.TAG, "--Update--build-downloadUrl-" + downloadUrl);
            Log.i(Constants.TAG, "--Update--build-updateLog-" + updateLog);
            item = new Update(versionCode, versionName, downloadUrl, updateLog);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.i(Constants.TAG, "--Update--build-JSONException-" + e);
            e.printStackTrace();
        } finally {
            return item;
        }
    }
}
