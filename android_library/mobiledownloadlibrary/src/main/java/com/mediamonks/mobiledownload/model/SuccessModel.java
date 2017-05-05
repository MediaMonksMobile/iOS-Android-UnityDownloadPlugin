package com.mediamonks.mobiledownload.model;

/**
 * Created by raphael on 18/04/16.
 */
public class SuccessModel {

//        COMPLETED = 0,
//        NO_DOWNLOAD_REQUIRED = 1,
//        DELETED = 2,
//        PAUSED = 3

    public int mSuccessType = 0;

    public String mSuccessMessage;

    public DownloadItem[] mFiles;

    public SuccessModel(){
        // no-args constructor
    }
}
