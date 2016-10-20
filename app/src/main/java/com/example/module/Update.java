package com.example.module;

/**
 * Created by Administrator on 2016/10/20.
 */
public class Update {
    private int versionCode;
    private String versionName;
    private String downloadUrl;
    private String updateLog;
    private String coverUpdate;
    private String coverStartDate;
    private String coverEndDate;
    private String coverURL;

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
}
