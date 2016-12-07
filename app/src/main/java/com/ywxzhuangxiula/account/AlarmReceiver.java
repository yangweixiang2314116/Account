package com.ywxzhuangxiula.account;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by Administrator on 2016/11/8.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private static final int NOTIFY_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG, "--AlarmReceiver --onReceive start-----");
        mNotificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        Intent newIntent = new Intent(context, AccountTotalActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("记录一下今天的装修支出吧")
                .setContentText("点击开始记账")
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }
}
