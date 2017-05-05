using UnityEngine;
using System.Collections;

public class SizeModel {

	public enum SizeType{
		NONE = 0,
		FILLED = 1
	};

	public SizeType mSizeType;

	public string mSizeMessage;

	public FileModel[] mFiles;

	public SizeModel(){}

	public SizeModel(string messageJson){

		JSONObject j = new JSONObject(messageJson);

		mSizeType = (SizeType) j.GetField ("mSizeType").i;

		mSizeMessage = j.GetField ("mSizeMessage").ToString();

		if (j.GetField ("mFiles") != null && !j.GetField("mFiles").ToString().Equals("null") && j.GetField("mFiles").list != null) {

			mFiles = new FileModel[j.GetField ("mFiles").list.Count];

			for (int i = 0; i < j.GetField ("mFiles").list.Count; i++) {

				mFiles[i]=  new FileModel(j.GetField ("mFiles").list[i].ToString());
			}
		}
	}
}
