package com.mediamonks.mobiledownload.model;

/**
 * Created by raphael on 18/04/16.
 */
public class ErrorModel {

//        OTHER = 0,
//        NO_INTERNET_CONNECTION = 1,
//        NOT_ENOUGH_SPACE = 2,
//        CANCELED = 3,
//        PAUSED_NEED_CELLULAR_PERMISSION = 4,
//        PAUSED_NETWORK_UNAVAILABLE = 6

    public int mErrorType = 0;

    public String mErrorMessage;

    public DownloadItem[] mFiles;

    public ErrorModel() {
        // no-args constructor
    }
}
