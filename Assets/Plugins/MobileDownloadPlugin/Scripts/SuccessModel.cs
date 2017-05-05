using UnityEngine;
using System.Collections;

public class SuccessModel {

	public enum SuccessType{
		COMPLETED = 0,
		NO_DOWNLOAD_REQUIRED = 1,
		DELETED = 2,
		PAUSED = 3,
		CANCELED = 4,
		NOTHING = 99
	};

	public SuccessType mSuccessType;

	public string mSuccessMessage;

	public FileModel[] mFiles;

	public SuccessModel(){}

	public SuccessModel(string messageJson){
		
		JSONObject j = new JSONObject(messageJson);

		mSuccessType = (SuccessType) j.GetField ("mSuccessType").i;

		mSuccessMessage = j.GetField ("mSuccessMessage").ToString();

		if (j.GetField ("mFiles") != null && !j.GetField("mFiles").ToString().Equals("null") && j.GetField("mFiles").list != null) {
			
			mFiles = new FileModel[j.GetField ("mFiles").list.Count];

			for (int i = 0; i < j.GetField ("mFiles").list.Count; i++) {
				
				mFiles[i]=  new FileModel(j.GetField ("mFiles").list[i].ToString());
			}
		}
	}
}
