package com.mediamonks.mobiledownload.model;

/**
 * Created by raphael on 15/04/16.
 */
public class ProgressModel {

//        DOWNLOADING = 0,
//        SUSPENDED = 1

    public DownloadItem mFile;

    public int mProgress;

    public int mProgressType = 0;

    public int mGroupPosition = 0;

    public int mGroupSize = 1;

    public ProgressModel() {
        // no-args constructor
    }

    public static ProgressModel empty() {
        ProgressModel progressModel = new ProgressModel();
        progressModel.mProgress = 0;
        progressModel.mFile = null;
        progressModel.mProgressType = 0;
        progressModel.mGroupPosition = 0;
        progressModel.mGroupSize = 1;
        return progressModel;
    }
}
