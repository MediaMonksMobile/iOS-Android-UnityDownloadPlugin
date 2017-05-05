package com.mediamonks.mobiledownload.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.mediamonks.mobiledownload.utils.AppConf;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.hash;

/**
 * DownloadGroup POJO class holding list of files that were requested to download in one single group.
 * One file download request is also a group with 1 item.
 * Created by Raphael Gilyazitdinov on 3.05.16.
 */
public class DownloadGroup {

    private static final String TAG = DownloadGroup.class.getSimpleName();

    public List<DownloadItem> items = new ArrayList<>();

    public String mNotificationMessage = null;

    public String mGroupUUID;//urls of all files in the group summed up together

    public DownloadGroup() {
    }

    public void addItem(DownloadItem downloadItem) {
        items.add(downloadItem);
    }

    public boolean isCompleted(Context context) {
        if (items == null || items.isEmpty()) return false;
        for (DownloadItem item : items) {
            if (!AppConf.validDownload(context, item.mReference)) {
                return false;
            }
            item.checkFilePath(context);
        }
        return true;
    }

    public static DownloadGroup fromUuid(Context context, Gson gson, String groupUUID) {
        return AppConf.getDownloadGroup(context, gson, groupUUID);
    }

    /**
     * Return {@link DownloadGroup} object, checks if we already have group saved earlier,
     * if not create group and {@link DownloadItem} objects that should be included.
     *
     * @param context
     * @param gson
     * @param urls
     * @param check
     * @return
     */
    public static DownloadGroup fromUrls(Context context, Gson gson, String[] urls, boolean check, String notificationMessage) {
        String groupUUID = null;
        for (String url : urls) {
            groupUUID += url;
        }

        DownloadGroup downloadGroup;
        downloadGroup = AppConf.getDownloadGroup(context, gson, groupUUID);
        if (downloadGroup != null) {
            Log.d(TAG, "fromUrls: previously save group");
            return downloadGroup;
        }
        Log.d(TAG, "fromUrls: no previously save group");
        downloadGroup = new DownloadGroup();
        downloadGroup.mNotificationMessage = notificationMessage;
        downloadGroup.mGroupUUID = groupUUID;
        for (int i = 0; i < urls.length; i++) {
            DownloadItem downloadItem = null;
            //check if we have DownloadItem with this url
            downloadItem = AppConf.getDownloadItem(context, gson, urls[i]);
            if (check) {
                //check
                if (downloadItem != null && AppConf.validDownload(context, downloadItem.mReference)) {
                    downloadItem.checkFilePath(context);
                    downloadGroup.addItem(downloadItem);
                }
            } else {
                //download
                if (downloadItem != null) {
                    if (downloadItem.addGroupId(groupUUID)) {
                        downloadItem.save(context, gson);
                    }
                    downloadGroup.addItem(downloadItem);
                } else {
                    downloadItem = DownloadItem.fromUrl(context, gson, urls[i], downloadGroup.mGroupUUID);
                    if (downloadItem == null) return null;//I/O or sd card issue;
                    downloadGroup.addItem(downloadItem);
                }
            }
        }
        if (!check) {
            AppConf.saveDownloadGroup(context, gson, downloadGroup);
        }
        return downloadGroup;
    }

    public void delete(Context context, Gson gson) {
        for (DownloadItem item : items) {
            item.deleteGroupId(mGroupUUID);
            item.save(context, gson);
            if (!item.hasGroupConnections()) {
                item.stop(context);
                item.delete(context, gson);
            }
        }
        AppConf.deleteDownloadGroup(context, gson, this);
    }

    /**
     * Returns if provided object is the same {@link DownloadGroup}.
     * Overrride equals to be able fast check if {@link DownloadGroup} exists in ArrayList.
     *
     * @param o Object to check.
     * @return {@code true} if DownloadGroup is the same and contatins same objects, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DownloadGroup)) return false;
        DownloadGroup that = (DownloadGroup) o;
        return this.mGroupUUID.equalsIgnoreCase(that.mGroupUUID);
    }

    /**
     * Returns hasCode for {@link DownloadGroup}.
     *
     * @return hasCode for {@link DownloadGroup} based on {@code mGroupUUID}.
     */
    @Override
    public int hashCode() {
        return hash(this.mGroupUUID);
    }

    @Override
    public String toString() {
        return "DownloadGroup{" +
                "items size=" + items.size() +
                ", mGroupUUID='" + mGroupUUID + '\'' +
                '}';
    }
}
