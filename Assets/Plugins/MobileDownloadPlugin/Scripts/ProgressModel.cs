using UnityEngine;
using System.Collections;

public class ProgressModel {

	public enum ProgressType{
		DOWNLOADING = 0,
		SUSPENDED = 1
	};

	public FileModel mFile;

	public ProgressType mProgressType;

	public int mProgress;

	public int mGroupSize = 1;//optional fields

	public int mGroupPosition = 0;//optinal fields

	public ProgressModel(){}

	public ProgressModel(string messageJson){
		JSONObject j = new JSONObject(messageJson);
		mProgressType = (ProgressType) j.GetField ("mProgressType").i;
		mProgress = (int) j.GetField ("mProgress").i;
		mFile = new FileModel (j.GetField ("mFile").ToString ());
		if (j.HasField ("mGroupSize")) {
			mGroupSize = (int) j.GetField ("mGroupSize").i;
		}
		if (j.HasField ("mGroupPosition")) {
			mGroupPosition = (int) j.GetField ("mGroupPosition").i;
		}
	}
}
