package com.mediamonks.mobiledownload;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.mediamonks.mobiledownload.model.DownloadGroup;
import com.mediamonks.mobiledownload.model.DownloadItem;
import com.mediamonks.mobiledownload.model.DownloadPojo;
import com.mediamonks.mobiledownload.model.ErrorModel;
import com.mediamonks.mobiledownload.model.ProgressModel;
import com.mediamonks.mobiledownload.model.SuccessModel;
import com.mediamonks.mobiledownload.utils.AppConf;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * MobileDownloadAndroid
 * Created by Raphael Gilyazitdinov on 04.05.16.
 */
public class MobileDownloadAndroid {

    private static final String TAG = MobileDownloadAndroid.class.getSimpleName();

    private static MobileDownloadAndroid mInstance = null;

    private Context mContext = null;

    private DownloadManager mDownloadManager;

    private static boolean mEnabled = false;

    private boolean queriing = false;

    private DownloadManager.Query mDownloadQuery;

    private DownloadManager.Query mProgressQuery;

    private Gson mGson;

    private MobileDownloadAndroid(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        mGson = new Gson();
        mDownloadQuery = new DownloadManager.Query();
        mDownloadQuery.setFilterByStatus(
                DownloadManager.STATUS_RUNNING |
                        DownloadManager.STATUS_SUCCESSFUL |
                        DownloadManager.STATUS_FAILED |
                        DownloadManager.STATUS_PENDING |
                        DownloadManager.STATUS_PAUSED);
        mProgressQuery = new DownloadManager.Query();
        mProgressQuery.setFilterByStatus(
                DownloadManager.STATUS_FAILED |
                        DownloadManager.STATUS_RUNNING |
                        DownloadManager.STATUS_PENDING |
                        DownloadManager.STATUS_PAUSED);
        queriing = false;
    }

    public static MobileDownloadAndroid getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MobileDownloadAndroid(context);
        }
        return mInstance;
    }

    // get locale
    public String getLocale() {
        Log.d(TAG, "getLocale: " + Locale.getDefault().toString());
        return Locale.getDefault().toString();
    }

    //check files
    public synchronized void checkFile(Context context, String url) {
        //check no download
        if (url == null || url.isEmpty()) {
            sendErrorMessage(context.getString(AppConf.getStringResourceIDFromState(context, "error_download_url")), 0, null);
            return;
        }
        Log.d(TAG, "checkFile:" + url);
        String[] urls = mGson.fromJson(url, String[].class);
        Log.d(TAG, "urls size:" + urls.length);
        //importatnt next line we don't create new if not exist, only check
        DownloadGroup downloadGroup = DownloadGroup.fromUrls(context, mGson, urls, true, null);
        if (downloadGroup == null || downloadGroup.items.isEmpty()) {
            sendErrorMessage(mContext.getString(AppConf.getStringResourceIDFromState(context, "error_no_download_item")), 0, null);
            return;
        }
        if (downloadGroup != null && downloadGroup.isCompleted(context)) {
            Log.d(TAG, "downloadGroup size:" + downloadGroup.items.size());
            SuccessModel successModel = new SuccessModel();
            successModel.mSuccessType = 0;
            successModel.mSuccessMessage = context.getString(AppConf.getStringResourceIDFromState(context, "status_downloded"));
            successModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
            Log.d(TAG, "SuccessMessage:" + mGson.toJson(successModel));
            UnityPlayer.UnitySendMessage("MobileDownloadManager", "SuccessMessage", mGson.toJson(successModel));
        } else {
            //// TODO: 15/06/16 add check file existance or if it's not trusted ? 
            sendErrorMessage(mContext.getString(AppConf.getStringResourceIDFromState(context, "error_no_download_item")), 0, null);
        }
    }

    public String getDownloadDirectory(Context context) {
        if (AppConf.isExternalStorageWritable() || AppConf.isExternalStorageReadable()) {
            Log.d(TAG, "getDownloadDirectory" + AppConf.getDownloadDirPath());
            return AppConf.getDownloadDirPath();
        } else {
            Log.d(TAG, "getDownloadDirectory null");
            return null;
        }
    }

    //download
    public synchronized void downloadFile(Context context, String url) {
        //check no download
        if (url == null || url.isEmpty()) {
            sendErrorMessage(context.getString(AppConf.getStringResourceIDFromState(context, "error_download_url")), 0, null);
            return;
        }
        Log.d(TAG, "checkFile:" + url);
        DownloadPojo unityDownloadMessage = mGson.fromJson(url, DownloadPojo.class);
        //String[] urls = mGson.fromJson(url, DownloadPojo.class);
        Log.d(TAG, "urls size:" + unityDownloadMessage.files.length);
        DownloadGroup downloadGroup;

        downloadGroup = DownloadGroup.fromUrls(context, mGson, unityDownloadMessage.files, false, unityDownloadMessage.notificationMessage);
        //check
        if (downloadGroup != null && downloadGroup.isCompleted(context)) {
            SuccessModel successModel = new SuccessModel();
            successModel.mSuccessType = 0;
            successModel.mSuccessMessage = context.getString(AppConf.getStringResourceIDFromState(context, "status_downloded"));
            successModel.mFiles = (DownloadItem[]) downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
            Log.d(TAG, "SuccessMessage:" + mGson.toJson(successModel));
            UnityPlayer.UnitySendMessage("MobileDownloadManager", "SuccessMessage", mGson.toJson(successModel));
            return;
        }
        //end check
        //download importatnt next line we don't check we create new if not exist
        downloadGroup = DownloadGroup.fromUrls(context, mGson, unityDownloadMessage.files, false, unityDownloadMessage.notificationMessage);
        if (downloadGroup == null) {
            sendErrorMessage(context.getString(AppConf.getStringResourceIDFromState(context, "error_sdcard")), 0, null);
        } else {
            //download started
            startProgressQuery();
        }
    }

    //deletefile
    public synchronized void deleteFile(Context context, String url) {
        if (url == null || url.isEmpty()) {
            sendErrorMessage(mContext.getString(
                    AppConf.getStringResourceIDFromState(mContext, "error_download_url")), 0, null);
            return;
        }
        Log.d(TAG, "Android deleteFile:" + url);
        String[] urls = mGson.fromJson(url, String[].class);

        DownloadGroup downloadGroup = DownloadGroup.fromUrls(context, mGson, urls, true, null);
        if (downloadGroup != null) {
            downloadGroup.delete(context, mGson);
        }
        SuccessModel successModel = new SuccessModel();
        successModel.mSuccessType = 2;
        successModel.mSuccessMessage = mContext
                .getString(AppConf.getStringResourceIDFromState(mContext, "status_deleted"));
        successModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
        Log.d(TAG, "SuccessMessage:" + mGson.toJson(successModel));
        UnityPlayer.UnitySendMessage("MobileDownloadManager", "SuccessMessage",
                mGson.toJson(successModel));
    }

    //cancel all
    public synchronized void cancelAllDownloads(Context context) {
        Log.d(TAG, "cancelAllDownloads");
        stopProgress();
        Cursor cursor;
        try {
            cursor = mDownloadManager.query(mDownloadQuery);
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
                long ref = cursor.getLong(columnIndex);
                DownloadItem downloadItem = AppConf.getDownloadItem(context, mGson, ref);
                if (downloadItem != null) {
                    for (String urls : downloadItem.mGroupUUIDs) {
                        DownloadGroup downloadGroup = DownloadGroup.fromUuid(context, mGson, urls);
                        if (downloadGroup != null) {
                            downloadGroup.delete(context, mGson);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SuccessModel successModel = new SuccessModel();
            successModel.mSuccessType = 4;
            successModel.mSuccessMessage = mContext.getString(AppConf.getStringResourceIDFromState(mContext, "status_canceled"));
            successModel.mFiles = null;
            Log.d(TAG, "SuccessMessage:" + mGson.toJson(successModel));
            UnityPlayer.UnitySendMessage("MobileDownloadManager", "SuccessMessage", mGson.toJson(successModel));
        }
    }

    //pause
    public synchronized void pauseDownloadAndroid(Context context) {
    }

    public synchronized void resumeDownloadAndroid(Context context) {
    }

    //wifi only
    public boolean isWifiOnly(Context context) {
        Log.d(TAG, "isWifiOnly: " + PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(AppConf.WIFI_ONLY_PREF_KEY, true));
        return PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(AppConf.WIFI_ONLY_PREF_KEY, true);
    }

    public synchronized void setEnableWifiOnly(Context context, Boolean wifiOnly) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(AppConf.WIFI_ONLY_PREF_KEY, wifiOnly).apply();
        Log.d(TAG, "Android setEnableWifiOnly:" + wifiOnly);
    }

    //enable disable
    public synchronized void enable() {
        if (mEnabled) return;
        mEnabled = true;
        Log.d(TAG, "enable");
        //// TODO: 21/06/16 ask if we need to enable progress check here?
    }

    public static boolean isEnabled() {
        return mEnabled;
    }

    public synchronized void disable() {
        if (!mEnabled) return;
        mEnabled = false;
        Log.d(TAG, "disable");
        //// TODO: 21/06/16 ask if we need to disable progress check here?
    }

    /**
     * Notifies that download with provided reference number completed
     *
     * @param reference to download item.
     */
    public synchronized void downloadComplete(Context context, long reference) {
        DownloadItem item = AppConf.getDownloadItem(context, mGson, reference);
        if (item != null && item.mGroupUUIDs != null && !item.mGroupUUIDs.isEmpty()) {
            Log.d(TAG, "downloadComplete iterate through groups");
            for (String groupUuid : item.mGroupUUIDs) {
                DownloadGroup downloadGroup = DownloadGroup.fromUuid(context, mGson, groupUuid);
                if (downloadGroup != null && downloadGroup.isCompleted(context)) {
                    //
                    sendProgressSuccessFinish(item, downloadGroup);
                    //
                    SuccessModel successModel = new SuccessModel();
                    successModel.mSuccessType = 0;
                    successModel.mSuccessMessage = context.getString(AppConf.getStringResourceIDFromState(context, "status_downloded"));
                    successModel.mFiles = (DownloadItem[]) downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                    Log.d(TAG, "SuccessMessage:" + mGson.toJson(successModel));
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "SuccessMessage", mGson.toJson(successModel));
                }
            }
        } else {
            Log.d(TAG, "downloadComplete, we don't care we don't have item:" + reference);
        }
    }

    /**
     * Notifies that download with provided reference number completed when MobileDownloadAndroid is not active.
     *
     * @param reference to download item.
     */
    public synchronized List<DownloadGroup> downloadCompleteNotEnabled(Context context, long reference) {
        if (mGson == null) {
            mGson = new Gson();
        }
        DownloadItem item = AppConf.getDownloadItem(context, mGson, reference);
        if (item != null && item.mGroupUUIDs != null && !item.mGroupUUIDs.isEmpty()) {
            Log.d(TAG, "downloadCompleteNotEnabled iterate through groups");
            List<DownloadGroup> completetedGroups = new ArrayList<>();
            for (String groupUuid : item.mGroupUUIDs) {
                DownloadGroup downloadGroup = DownloadGroup.fromUuid(context, mGson, groupUuid);
                if (downloadGroup != null && downloadGroup.isCompleted(context)) {
                    completetedGroups.add(downloadGroup);
                }
            }
            return completetedGroups.isEmpty() ? null : completetedGroups;
        } else {
            return null;
        }
    }

    //uril methods
    private synchronized void sendErrorMessage(String message, int errorType, DownloadItem[] items) {
        ErrorModel errorModel = new ErrorModel();
        errorModel.mErrorType = errorType;
        errorModel.mErrorMessage = message;
        errorModel.mFiles = items;
        Log.d(TAG, "sendErrorMessage:" + mGson.toJson(errorModel));
        UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage", mGson.toJson(errorModel));
    }

    private synchronized void checkStatus(DownloadItem downloadItem, int status, int reason, int progress) {
        if (mContext == null || mGson == null) return;
        switch (status) {
            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_PAUSED:
            case DownloadManager.STATUS_RUNNING:
                for (String urls : downloadItem.mGroupUUIDs) {
                    DownloadGroup downloadGroup = DownloadGroup.fromUuid(mContext, mGson, urls);
                    if (downloadGroup != null && !downloadGroup.isCompleted(mContext)) {
                        ProgressModel progressModel = ProgressModel.empty();
                        progressModel.mProgress = progress;
                        progressModel.mFile = downloadItem;
                        progressModel.mGroupPosition = (downloadGroup.items.indexOf(downloadItem)) + 1;
                        progressModel.mGroupSize = downloadGroup.items.size();
                        Log.d(TAG, "ProgressMessage:" + mGson.toJson(progressModel));
                        progressModel.mProgressType = (status == DownloadManager.STATUS_RUNNING) ? 0
                                : 1;
                        try {
                            UnityPlayer.UnitySendMessage("MobileDownloadManager", "ProgressMessage",
                                    mGson.toJson(progressModel));
                        } catch (UnsatisfiedLinkError e) {
                            //everything is ok we can't call it from different process the in background
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case DownloadManager.STATUS_FAILED:
                for (String urls : downloadItem.mGroupUUIDs) {
                    DownloadGroup downloadGroup = DownloadGroup.fromUuid(mContext, mGson, urls);
                    if (downloadGroup != null && !downloadGroup.isCompleted(mContext)) {
                        downloadGroup.delete(mContext, mGson);
                        generateSendError(reason, downloadGroup);
                    }
                }
                break;
            default:
                Log.d(TAG, "checkStatus:" + status);
                break;
        }
    }

    private void stopProgress() {
        Log.d(TAG, "stopProgress:");
        queriing = false;
    }

    private synchronized void startProgressQuery() {
        if (queriing) return;
        queriing = true;
        if (!mEnabled) {
            queriing = false;
            return;
        }
        Log.d(TAG, "startProgressQuery:");
        new ProgressAsyncTask().execute();
    }

    private class ProgressAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (queriing) {

                boolean needQueryNext = false;
                Cursor cursor = null;
                try {
                    cursor = mDownloadManager.query(mProgressQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                if (cursor == null) {
                    break;
                }
                if (cursor.getCount() > 0) needQueryNext = true;
                while (cursor.moveToNext()) {
                    int columnRef = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
                    long ref = cursor.getLong(columnRef);
                    int columnStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnStatus);
                    //column for reason code if the download failed or paused
                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);

                    int tbytesDownloaded = cursor
                            .getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int tbytesTotal = cursor
                            .getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    int progress = (int) ((tbytesDownloaded * 100l) / tbytesTotal);

                    if (mContext == null || mGson == null) break;
                    DownloadItem downloadItem = AppConf.getDownloadItem(mContext, mGson, ref);
                    if (downloadItem != null) {
                        checkStatus(downloadItem, status, reason, progress);
                    }
                }
                if (needQueryNext) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    queriing = false;
                }
            }
            return null;
        }
    }

    private synchronized void sendProgressSuccessFinish(DownloadItem item, DownloadGroup downloadGroup) {
        ProgressModel sprogressModel = ProgressModel.empty();
        sprogressModel.mProgress = 100;
        sprogressModel.mFile = item;
        sprogressModel.mGroupPosition = (downloadGroup.items.indexOf(item)) + 1;
        sprogressModel.mGroupSize = downloadGroup.items.size();
        sprogressModel.mProgressType = 1;
        UnityPlayer.UnitySendMessage("MobileDownloadManager", "ProgressMessage",
                mGson.toJson(sprogressModel));
    }

    private void generateSendError(int reason, DownloadGroup downloadGroup) {
        ErrorModel errorModel = new ErrorModel();
        switch (reason) {
            case DownloadManager.ERROR_CANNOT_RESUME:
                errorModel.mErrorType = 0;
                errorModel.mErrorMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_fail_cannot_resume"));
                errorModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "ErrorMessage:" + mGson.toJson(errorModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage",
                            mGson.toJson(errorModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                errorModel.mErrorType = 0;
                errorModel.mErrorMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_fail_device_not_found"));
                errorModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "ErrorMessage:" + mGson.toJson(errorModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage",
                            mGson.toJson(errorModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                SuccessModel successModel = new SuccessModel();
                successModel.mSuccessType = 1;
                successModel.mSuccessMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_downloded_already"));
                successModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "SuccessMessage:" + mGson.toJson(successModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "SuccessMessage",
                            mGson.toJson(successModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
            case DownloadManager.ERROR_FILE_ERROR:
                errorModel.mErrorType = 0;
                errorModel.mErrorMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_fail_file_error"));
                errorModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "ErrorMessage:" + mGson.toJson(errorModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage",
                            mGson.toJson(errorModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                errorModel.mErrorType = 1;
                errorModel.mErrorMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_fail_network"));
                errorModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "ErrorMessage:" + mGson.toJson(errorModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage",
                            mGson.toJson(errorModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                errorModel.mErrorType = 2;
                errorModel.mErrorMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_fail_space"));
                errorModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "ErrorMessage:" + mGson.toJson(errorModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage",
                            mGson.toJson(errorModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
            case DownloadManager.ERROR_UNKNOWN:
                errorModel.mErrorType = 0;
                errorModel.mErrorMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_fail_other"));
                errorModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "ErrorMessage:" + mGson.toJson(errorModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage",
                            mGson.toJson(errorModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
            default:
                errorModel.mErrorType = 0;
                errorModel.mErrorMessage = mContext.getString(
                        AppConf.getStringResourceIDFromState(mContext,
                                "status_fail_other"));
                errorModel.mFiles = downloadGroup.items.toArray(new DownloadItem[downloadGroup.items.size()]);
                Log.d(TAG, "ErrorMessage:" + mGson.toJson(errorModel));
                try {
                    UnityPlayer.UnitySendMessage("MobileDownloadManager", "ErrorMessage",
                            mGson.toJson(errorModel));
                } catch (UnsatisfiedLinkError e) {
                    //everything is ok we can't call it from different process the in background
                }
                break;
        }
    }
}
