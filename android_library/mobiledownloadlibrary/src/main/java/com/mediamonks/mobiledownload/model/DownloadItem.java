package com.mediamonks.mobiledownload.model;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.mediamonks.mobiledownload.utils.AppConf;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.hash;

/**
 * DownloadItem POJO class holding info about file that was requested to download and reference to
 * DownloadManager instance.
 * Created by Raphael Gilyazitdinov on 19.04.16.
 */
public class DownloadItem implements Parcelable {

    private static final String TAG = DownloadItem.class.getSimpleName();

    public String mFilePath;

    public String mFileUrl;

    public long mReference;

    public List<String> mGroupUUIDs = new ArrayList<>();

    public DownloadItem() {
    }

    protected DownloadItem(Parcel in) {
        mFilePath = in.readString();
        mFileUrl = in.readString();
        mReference = in.readLong();
        in.readStringList(mGroupUUIDs);
    }

    public static final Creator<DownloadItem> CREATOR = new Creator<DownloadItem>() {
        @Override
        public DownloadItem createFromParcel(Parcel in) {
            return new DownloadItem(in);
        }

        @Override
        public DownloadItem[] newArray(int size) {
            return new DownloadItem[size];
        }
    };

    @Override
    public String toString() {
        return "DownloadItem{" +
                "mFilePath='" + mFilePath + '\'' +
                ", mFileUrl='" + mFileUrl + '\'' +
                ", mReference=" + mReference +
                ", mGroupUUID size=" + mGroupUUIDs.size() +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFilePath);
        dest.writeString(mFileUrl);
        dest.writeLong(mReference);
        dest.writeStringList(mGroupUUIDs);
    }

    /**
     * Returns {@link DownloadItem} object created from provided URL, add this object to DownloadManager,
     * add groupUUID reference to object, save download reference and object.
     *
     * @param context
     * @param gson
     * @param url       where file can be downloaded
     * @param groupUUID identificator of the Group that contatins this {@link DownloadItem}.
     * @return
     */
    public static DownloadItem fromUrl(Context context, Gson gson, String url, String groupUUID) {
        DownloadManager dMgr = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        Log.d(TAG, "fromUrl:" + url);
        DownloadItem downloadItem = new DownloadItem();
        downloadItem.mFileUrl = url;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadItem.mFileUrl));
        //Add to download queue
        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(AppConf.isWifiOnly(context) ? DownloadManager.Request.NETWORK_WIFI : DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle(context.getString(AppConf.getStringResourceIDFromState(context, "status_downloading")));//// TODO: 03.05.16 add to api ability to customize title for download notification of this file
        //request.setDescription(downloadItem.mFileUrl);//// TODO: 03.05.16  add to api ability customize description for download notification of this file.
        //Set the local destination for the downloaded file to a path within the application's external files directory
        //DIRECTORY_DOWNLOADS
        request.setDestinationInExternalFilesDir(context, null, AppConf.guessFileName(downloadItem.mFileUrl));
        //Enqueue a new download and same the referenceId
        downloadItem.mReference = dMgr.enqueue(request);
        //get filepath
        try {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadItem.mReference);
            Cursor c = dMgr.query(q);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    downloadItem.mFilePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                } else {
                    try {
                        downloadItem.mFilePath = AppConf.getSaveFilePath(context) + AppConf
                                .guessFileName(downloadItem.mFileUrl);
                        Log.d(TAG, downloadItem.mFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            c.close();
        } catch (Exception e) {
            try {
                downloadItem.mFilePath = AppConf.getSaveFilePath(context) + AppConf
                        .guessFileName(downloadItem.mFileUrl);
                Log.d(TAG, downloadItem.mFilePath);
            } catch (Exception ee) {
                ee.printStackTrace();
                return null;
            }
        }
        Log.d(TAG, "downloaditem created with filepath:" + ((downloadItem.mFilePath == null) ? "" : downloadItem.mFilePath));
        //
        downloadItem.addGroupId(groupUUID);
        //save download item
        Log.d(TAG, "created and enqueued:" + downloadItem.mFileUrl);
        AppConf.saveReferenceForDownload(context, gson, downloadItem.mFileUrl, downloadItem.mReference);
        AppConf.saveDownloadItem(context, gson, downloadItem);
        return downloadItem;
    }

    public void checkFilePath(Context context) {
        DownloadManager dMgr = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(mReference);
        Cursor c = dMgr.query(q);
        if (c.moveToFirst()) {
            Log.d(TAG, "downloaditem checkFilePath: cursor is there");
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                mFilePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                Log.d(TAG, "downloaditem checkFilePath: STATUS_SUCCESSFUL get name");
            } else {
                try {
                    mFilePath = AppConf.getSaveFilePath(context) + AppConf
                            .guessFileName(mFileUrl);
                    Log.d(TAG, mFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "downloaditem checkFilePath: cursor not there");
        c.close();
    }

    public void stop(Context context) {
        DownloadManager dMgr = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        dMgr.remove(mReference);
    }

    public boolean addGroupId(String groupUUID) {
        if (!mGroupUUIDs.contains(groupUUID)) {
            mGroupUUIDs.add(groupUUID);
            return true;
        }
        return false;
    }

    public boolean deleteGroupId(String groupUUID) {
        if (mGroupUUIDs.contains(groupUUID)) {
            mGroupUUIDs.remove(groupUUID);
            return true;
        }
        return false;
    }

    public boolean hasGroupConnections() {
        return !mGroupUUIDs.isEmpty();
    }

    public void save(Context context, Gson gson) {
        Log.d(TAG, "save :" + mFileUrl);
        AppConf.saveDownloadItem(context, gson, this);
    }

    public void delete(Context context, Gson gson) {
        Log.d(TAG, "delete :" + mFileUrl);
        AppConf.removeReferenceForDownload(context, gson, mFileUrl);
        AppConf.deleteDownloadItem(context, gson, this);
    }

    /**
     * Returns if provided object is the same {@link DownloadItem}.
     * Overrride equals to be able fast check if {@link DownloadItem} exists in ArrayList.
     *
     * @param o Object to check.
     * @return {@code true} if {@link DownloadItem} is the same, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DownloadItem)) return false;
        DownloadItem that = (DownloadItem) o;
        return this.mFileUrl.equalsIgnoreCase(that.mFileUrl);
    }

    /**
     * Returns hasCode for {@link DownloadItem}.
     *
     * @return hasCode for {@link DownloadItem} based on {@code mFileUrl}.
     */
    @Override
    public int hashCode() {
        return hash(this.mFileUrl);
    }
}
