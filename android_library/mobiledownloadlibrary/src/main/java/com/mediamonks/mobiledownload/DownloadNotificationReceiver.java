package com.mediamonks.mobiledownload;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mediamonks.mobiledownload.model.DownloadGroup;
import com.mediamonks.mobiledownload.utils.AppConf;

import java.util.List;

/**
 * BroadcastReceiver implementation to handle {@link DownloadManager.ACTION_NOTIFICATION_CLICKED}
 * and {@link DownloadManager.ACTION_DOWNLOAD_COMPLETE} events.
 * Created by raphael on 14/04/16.
 */
public class DownloadNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = DownloadNotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                try {
                    // Try to find default launch Activity for our package and launch the App/Game.
                    // Don't directly reference it because we don't know package name or class.
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    if (launchIntent != null) {
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(launchIntent);
                    } else {
                        Log.d("MobileDownloadAndroid", "Receiver no launch activity found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                // Check if Unity3d App/Game is currently running. If yes will process the
                // result and send proper events if necessary.
                if (MobileDownloadAndroid.getInstance(context).isEnabled()) {
                    Log.d("MobileDownloadAndroid", "Receiver ACTION_DOWNLOAD_COMPLETE: instance running");
                    if(AppConf.validDownload(context,intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0))) {
                        MobileDownloadAndroid.getInstance(context).downloadComplete(context, intent.getLongExtra(
                                DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    }else {
                        Log.d(TAG,"onReceive ACTION_DOWNLOAD_COMPLETE: not validDownload");
                    }
                } else {
                    if(!AppConf.validDownload(context,intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0))) {
                        Log.d(TAG,"onReceive ACTION_DOWNLOAD_COMPLETE: not validDownload");
                        return;
                    }
                    // Process the result without any events. if result for group success then
                    // show 'download complete' Notification.
                    List<DownloadGroup> completedGroups = MobileDownloadAndroid.getInstance(context).downloadCompleteNotEnabled(context,intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    if(completedGroups!=null&&!completedGroups.isEmpty()){
                        //we usually show info specifically for each of the groups, so we can get it from array if needed in future.
                        generateNotification(completedGroups.get(0).mNotificationMessage, context);
                        Log.d("MobileDownloadAndroid", "Receiver ACTION_DOWNLOAD_COMPLETE: not enabled full groups downloaded:"+completedGroups.size());
                    }
                }
                break;
        }
    }

    private void generateNotification(String notificationMessage, Context context) {
        Log.d("MobileDownloadAndroid", "generateNotification:" + notificationMessage);
        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentTitle((notificationMessage!=null)?notificationMessage:context.getString(AppConf.getStringResourceIDFromState(context, "notification_complete")))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);
//                        .setContentText("file: "+downloadId);//todo show some text
        // Creates an explicit intent for an Activity in your app
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

        if (launchIntent != null) {
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(404, mBuilder.build());
    }

}