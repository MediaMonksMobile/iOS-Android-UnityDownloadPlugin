using UnityEngine;
using System.Collections;

public class ErrorModel {

	public enum ErrorType{
		OTHER = 0,
		NO_INTERNET_CONNECTION = 1,
		NOT_ENOUGH_SPACE = 2,
		CANCELED = 3,
		NETWORK_LOST = 5,
		TIMED_OUT = 7
	};

	public ErrorType mErrorType;

	public string mErrorMessage;

	public FileModel[] mFiles;

	public ErrorModel(){}

	public ErrorModel(string messageJson){
		JSONObject j = new JSONObject(messageJson);
		mErrorType = (ErrorType) j.GetField ("mErrorType").i;
		mErrorMessage = j.GetField ("mErrorMessage").ToString();
		if (j.GetField ("mFiles") != null && !j.GetField ("mFiles").ToString().Equals("null") && j.GetField ("mFiles").list != null) {
			mFiles = new FileModel[j.GetField ("mFiles").list.Count];
			for (int i = 0; i < j.GetField ("mFiles").list.Count; i++) {
				mFiles [i] = new FileModel (j.GetField ("mFiles").list [i].ToString ());
			}
		}
	}
}
