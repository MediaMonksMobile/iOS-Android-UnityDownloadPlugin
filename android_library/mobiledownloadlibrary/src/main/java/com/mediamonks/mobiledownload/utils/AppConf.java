package com.mediamonks.mobiledownload.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mediamonks.mobiledownload.model.DownloadGroup;
import com.mediamonks.mobiledownload.model.DownloadItem;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Util class with different static util methods and global vars.
 * Created by Raphael Gilyazitdinov on 19.04.16.
 */
public class AppConf {

    private static final String TAG = AppConf.class.getSimpleName();

    public static final String WIFI_ONLY_PREF_KEY = "wifi_only";

    private static final String PREF_FILE_PREFIX = "d_file_";

    private static final String PREF_DOWNLOADS_REFERENCES = "download_references";

    /**
     * @return string resource ID for the corresponding string.
     */
    public static int getStringResourceIDFromState(Context context, String stringRes) {
        return getStringResource(context, stringRes);
    }

    public synchronized static DownloadItem getDownloadItem(Context context, Gson gson, String url) {
        long ref = getReferenceForDownload(context, gson, url);
        if (ref >= 0) {
            return getDownloadItem(context, gson, ref);
        } else {
            return null;
        }
    }

    public synchronized static DownloadItem getDownloadItem(Context context, Gson gson, long reference) {
        DownloadItem downloadItem = null;
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FILE_PREFIX + reference, null);
        if (json != null) {
            downloadItem = gson.fromJson(json, DownloadItem.class);
        }
        return downloadItem;
    }

    public synchronized static DownloadGroup getDownloadGroup(Context context, Gson gson, String groupUUID) {
        DownloadGroup downloadGroup = null;
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FILE_PREFIX + groupUUID, null);
        if (json != null) {
            downloadGroup = gson.fromJson(json, DownloadGroup.class);
        }
        return downloadGroup;
    }

    public synchronized static void saveDownloadItem(Context context, Gson gson, DownloadItem downloadItem) {
        String json = gson.toJson(downloadItem);
        if (json != null) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit().putString(PREF_FILE_PREFIX + downloadItem.mReference, json).commit();
        }
    }

    public synchronized static void saveDownloadGroup(Context context, Gson gson, DownloadGroup downloadGroup) {
        String json = gson.toJson(downloadGroup);
        if (json != null) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit().putString(PREF_FILE_PREFIX + downloadGroup.mGroupUUID, json).commit();
        }
    }

    public synchronized static void deleteDownloadItem(Context context, Gson gson, DownloadItem downloadItem) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().remove(PREF_FILE_PREFIX + downloadItem.mFileUrl).commit();
    }

    public synchronized static void deleteDownloadGroup(Context context, Gson gson, DownloadGroup downloadGroup) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().remove(PREF_FILE_PREFIX + downloadGroup.mGroupUUID).commit();
    }

    /**
     * Guesses canonical filename that a download would have, using
     * the URL. File extension, if not defined,
     * is added based on the mimetype
     *
     * @param url Url to the content
     * @return suggested filename
     */
    public static final String guessFileName(String url) {
        String filename = null;
        String extension = null;

        // If all the other http-related approaches failed, use the plain uri
        if (filename == null) {
            String decodedUrl = Uri.decode(url);
            if (decodedUrl != null) {
                int queryIndex = decodedUrl.indexOf('?');
                // If there is a query string strip it, same as desktop browsers
                if (queryIndex > 0) {
                    decodedUrl = decodedUrl.substring(0, queryIndex);
                }
                if (!decodedUrl.endsWith("/")) {
                    int index = decodedUrl.lastIndexOf('/') + 1;
                    if (index > 0) {
                        filename = decodedUrl.substring(index);
                    }
                }
            }
        }

        // Finally, if couldn't get filename from URI, get a generic filename
        if (filename == null) {
            filename = "downloadfile";
        }

        // Split filename between base and extension
        // Add an extension if filename does not have one
        int dotIndex = filename.indexOf('.');
        if (dotIndex < 0) {
            if (extension == null) {
                extension = ".bin";
            }
        } else {
            if (extension == null) {
                extension = filename.substring(dotIndex);
            }
            filename = filename.substring(0, dotIndex);
        }

        return filename + extension;
    }

    public static String getSaveFilePath(Context context) {
        return context.getExternalFilesDir(null).toString() + File.separator;
    }

    public static int getStringResource(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Return path for the user's public download directory.
     *
     * @return
     */
    public static String getDownloadDirPath() {
        try {
            File file = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!file.mkdirs()) {
            }
            if (file.isDirectory() && file.exists()) {
                return file.getAbsolutePath();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return if user allow downloads over Cellular network preference.
     *
     * @param context
     * @return
     */
    public static boolean isWifiOnly(Context context) {
        Log.d(TAG, "isWifiOnly");
        return PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(WIFI_ONLY_PREF_KEY, true);
    }

    /**
     * Check if download was valid, see issue
     * http://code.google.com/p/android/issues/detail?id=18462
     *
     * @param downloadId
     * @return
     */
    public static boolean validDownload(Context context, long downloadId) {

        Log.d("MobileDownloadAndroid", "Checking download status for id: " + downloadId);

        //Verify if download is a success
        DownloadManager dMgr = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        Cursor c = dMgr.query(new DownloadManager.Query().setFilterById(downloadId));

        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Log.d(TAG, "validDownload: Download is valid");
                return true; //Download is valid, celebrate
            } else {
                int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                Log.d(TAG, "validDownload: Download not correct, status [" + status + "] reason [" + reason + "]");
                return false;
            }
        }
        return false;
    }

    //internal hashmap of references and urls
    public static void saveReferenceForDownload(Context context, Gson gson, String url, long reference) {
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_DOWNLOADS_REFERENCES, null);
        Log.d(TAG, "saveReferenceForDownload before:" + json);
        Map<String, Long> map;
        if (json != null) {
            Type hashMap = new TypeToken<HashMap<String, Long>>() {
            }.getType();
            map = gson.fromJson(json, hashMap);
            if (!map.containsKey(url)) {
                map.put(url, reference);
            }
        } else {
            map = new HashMap<String, Long>();
            map.put(url, reference);
        }
        if (map != null) {
            Log.d(TAG, "saveReferenceForDownload after:" + gson.toJson(map));
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_DOWNLOADS_REFERENCES, gson.toJson(map)).commit();
        }
    }

    public static void removeReferenceForDownload(Context context, Gson gson, String url) {
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_DOWNLOADS_REFERENCES, null);
        Log.d(TAG, "removeReferenceForDownload:" + json);
        if (json == null) {
            return;
        } else {
            Type hashMap = new TypeToken<HashMap<String, Long>>() {
            }.getType();
            Map<String, Long> map = gson.fromJson(json, hashMap);
            if (!map.containsKey(url)) {
                return;
            } else {
                map.remove(url);
                if (map != null) {
                    Log.d(TAG, "removeReferenceForDownload after:" + gson.toJson(map));
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_DOWNLOADS_REFERENCES, gson.toJson(map)).commit();
                }
            }
        }
    }

    public static long getReferenceForDownload(Context context, Gson gson, String url) {
        long ref = -1;

        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_DOWNLOADS_REFERENCES, null);
        Log.d(TAG, "getReferenceForDownload:" + json);
        if (json != null) {
            Type hashMap = new TypeToken<Map<String, Long>>() {
            }.getType();
            Map<String, Long> map = gson.fromJson(json, hashMap);
            if (map.containsKey(url)) {
                return map.get(url);
            }
        }
        return ref;
    }
}
