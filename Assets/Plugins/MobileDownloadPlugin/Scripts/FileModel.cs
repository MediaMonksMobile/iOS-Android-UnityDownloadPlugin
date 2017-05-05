using UnityEngine;
using System.Collections;

public class FileModel{

	public string mFilePath = "";

	public string mFileUrl = "";

	public FileModel(){}

	public FileModel(string messageJson){
		if(messageJson==null||messageJson.Equals("null"))
			return;
		JSONObject j = new JSONObject(messageJson);
		mFilePath = j.GetField ("mFilePath").ToString();
		mFileUrl = j.GetField ("mFileUrl").ToString();
	}
}
