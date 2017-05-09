using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;

public class MobileDownloadManager : Singleton<MobileDownloadManager>
{

	protected MobileDownloadManager (){ }
	//need to be protected to be Singleton.

	public event Success OnSuccess;

	public event Error OnError;

	public event Progress OnProgress;

	public event Size OnSize;
	#if UNITY_ANDROID
	AndroidJavaObject mobileDownloadAndroid;
	AndroidJavaObject context;
	#elif UNITY_IOS

		[DllImport("__Internal")]
		public static extern void configure(bool wifi, int maxConnections, bool notifyUserOnce);
		
		[DllImport("__Internal")]
		public static extern void downloadFilesWithMessage(string[] urls, int count, string notificationMessage);

		[DllImport("__Internal")]
		public static extern void deleteFile(string name);

		[DllImport("__Internal")]
		public static extern void deleteFiles(string[] names, int count);

		[DllImport("__Internal")]
		public static extern void setAllowCellular(bool allowed);

		[DllImport("__Internal")]
		public static extern bool allowCellular();

		[DllImport("__Internal")]
		public static extern void cancelSingleDownload(string url);

		[DllImport("__Internal")]
		public static extern void cancelAllDownloads();

		[DllImport("__Internal")]
		public static extern void pauseSingleDownload (string url);

		[DllImport("__Internal")]
		public static extern void pauseAllDownloads();

		[DllImport("__Internal")]
		public static extern void resumeSingleDownload(string url);

		[DllImport("__Internal")]
		public static extern void resumeAllDownloads();

		[DllImport("__Internal")]
		public static extern void checkFileExist(string url);

		[DllImport("__Internal")]
		public static extern void checkFilesExist(string[] urls, int count);
		
		[DllImport("__Internal")]
		public static extern string currentLocale();
	#endif

	/**
	 * @Deprecated not needed anymore. Initialize MobileDownloadManager, should be called from Awake().
	 **/
	public void Init (){}
		
	/* Configure Method calls a instancetype for configuring HTTPMaxConnectionsPerHost and OnlyWifi enabled
	* @param(bool) for applying the OnlyWifi enabling
	* @param(integer) for applying the HTTPMaxConnectionsPerHost
	* @param(bool) for applying the Notification per download complete
	* */
	public void Configure(bool wifi, int maxConnections, bool isSingleNotifications)
	{
		#if UNITY_ANDROID

		#elif UNITY_IOS

		Debug.Log("iOS Configure With wifi Options & max connections");

		configure(wifi, maxConnections, isSingleNotifications);

		#else
		Debug.Log("XXX DownloadFile");
		#endif
	}


	/**
	 * Initialize downloading file from provided URL.
	 * Will call OnProgress to notify about progress.
	 * Will call OnSuccess or OnError when result ready.
	 * @param url File url
	 **/
	public void DownloadFile (string url)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android DownloadFile");
		string[] urls = new string[1];
		urls [0] = url;
		downloadFileAndroid (urls, null);
		#elif UNITY_IOS
		Debug.Log("iOS DownloadFile");
		#else
		Debug.Log("XXX DownloadFile");
		#endif
	}

	/**
	* Initialize downloading array of files from provided URLs.
	* Will call OnProgress to notify about progress.
	* Will call OnSuccess or OnError when result ready.
	* @param urls Files urls
	* @param notificationMessage message to show when download of files complete.
	**/
	public void DownloadFiles(string[] urls, string notificationMessage)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android DownloadFiles with custom NotificationMessage");
		downloadFileAndroid (urls, notificationMessage);
		#elif UNITY_IOS
		Debug.Log("iOS DownloadFilesWithMessage: ");
		downloadFilesWithMessage(urls, urls.Length, notificationMessage);
		#else
		Debug.Log("XXX DownloadFiles");
		#endif
	}

	/**
	* Initialize downloading array of files from provided URLs.
	* Will call OnProgress to notify about progress.
	* Will call OnSuccess or OnError when result ready.
	* @param urls Files urls
	**/
	public void DownloadFiles (string[] urls)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android DownloadFiles");
		downloadFileAndroid (urls, null);
		#elif UNITY_IOS
		Debug.Log("iOS DownloadFiles");
		#else
		Debug.Log("XXX DownloadFiles");
		#endif
	}

	/**
	* Check state of file from provided URL without downloading .
	* Will call OnProgress to notify about progress.
	* Will call OnSuccess or OnError when result ready.
	* @param url File url
	**/
	public void CheckFileExist (string url)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android CheckFileExist");
		string[] urls = new string[1];
		urls [0] = url;
		checkFileAndroid (urls);
		#elif UNITY_IOS
		Debug.Log("iOS CheckFileExist");
		checkFileExist(url);
		#else
		Debug.Log("XXX CheckFilesExist");
		#endif
	}

	/**
	* Check state of files from provided URLs without downloading .
	* Will call OnProgress to notify about progress.
	* Will call OnSuccess or OnError when result ready.
	* @param url File url
	**/
	public void CheckFilesExist (string[] urls)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android CheckFilesExist");
		checkFileAndroid (urls);
		#elif UNITY_IOS
		Debug.Log("iOS CheckFilesExist");
		checkFilesExist(urls, urls.Length);
		#else
		Debug.Log("XXX CheckFilesExist");
		#endif
	}

	/**
	* Delete file that was downloaded with URL.
	* Will call OnSuccess or OnError when result ready.
	* @param url File url
	**/
	public void DeleteFile (string url)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android DeleteFile");
		string[] urls = new string[1];
		urls [0] = url;
		deleteFileAndroid (urls);
		#elif UNITY_IOS
		Debug.Log("iOS DeleteFile");
		deleteFile(url);
		#else
		Debug.Log("XXX DeleteFile");
		#endif
	}

	/**
	* Delete array of files that were downloaded with URLs provided.
	* Will call OnSuccess or OnError when result ready.
	* @param url Files urls
	**/
	public void DeleteFiles (string[] urls)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android DeleteFiles");
		deleteFileAndroid (urls);
		#elif UNITY_IOS
		Debug.Log("iOS DeleteFiles");
		deleteFiles(urls, urls.Length);
		#else
		Debug.Log("XXX DeleteFiles");
		#endif
	}

	/**
	* Returns {@code true} if currently only download via WiFI enabled, {@code false} otherwise.
	**/
	public bool IsWifiOnly ()
	{
		#if UNITY_ANDROID
		Debug.Log ("Android IsWifiOnly");
		return getMobileDownloadAndroid ().Call<bool> ("isWifiOnly", getContext ());
		#elif UNITY_IOS
		return !allowCellular();
		#else
		Debug.Log("XXX IsWifiOnly");
		return true;
		#endif
	}

	/**
	* Set download via WiFI enabled.
	* @param wifiOnly {@code true} enabled, {@code false} otherwise.
	**/
	public void SetEnableWifiOnly (bool wifiOnly)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android SetEnableWifiOnly: " + wifiOnly);
		getMobileDownloadAndroid ().Call ("setEnableWifiOnly", getContext (), new AndroidJavaObject ("java.lang.Boolean", wifiOnly));
		#elif UNITY_IOS
		Debug.Log("iOS SetEnableWifiOnly: "+wifiOnly);
		setAllowCellular(!wifiOnly);
		#else
		Debug.Log("XXX SetEnableWifiOnly: "+wifiOnly);
		#endif
	}

	/**
	* Returns absolute path to public download folder, {@code null} otherwise.
	**/
	public string GetAndroidDownloadPath ()
	{
		#if UNITY_ANDROID
		Debug.Log ("Android GetAndroidDownloadPath");
		return getMobileDownloadAndroid ().Call<string> ("getDownloadDirectory", getContext ());
		#else
		Debug.Log("XXX GetAndroidDownloadPath");
		return null;
		#endif
	}

	/**
	* Cancel all current downloads in the queue
	**/
	public void CancelSingleDownload (string url)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android CancelDownloads");
		getMobileDownloadAndroid ().Call ("cancelAllDownloads", getContext ());
		#elif UNITY_IOS

		Debug.Log("CancelSingeDownload with url:" +url);

		Debug.Log("iOS CancelSingleDownload");

		cancelSingleDownload(url);
		#else
		Debug.Log("XXX CancelDownloads");
		#endif
	}

	/**
	* Cancel all current downloads in the queue
	**/
	public void CancelDownloads ()
	{
		#if UNITY_ANDROID
		Debug.Log ("Android CancelDownloads");
		getMobileDownloadAndroid ().Call ("cancelAllDownloads", getContext ());
		#elif UNITY_IOS
		Debug.Log("iOS CancelDownloads");
		cancelAllDownloads();
		#else
		Debug.Log("XXX CancelDownloads");
		#endif
	}

	/**
	 * Pause the download with given index.
	**/
	public void PauseSingleDownload (string url)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android PauseDownloads");
		pauseSingleDownloadAndroid ();
		#elif UNITY_IOS
		Debug.Log("iOS PauseSingleDownload");
		pauseSingleDownload(url);
		#else
		Debug.Log("XXX PauseDownloads");
		#endif
	}

	/**
	* Pause all current downloads in the queue (Not yet working in Android)
	**/
	public void PauseDownloads ()
	{
		#if UNITY_ANDROID
		Debug.Log ("Android PauseDownloads");
		pauseDownloadAndroid ();
		#elif UNITY_IOS
		Debug.Log("iOS PauseDownloads");
		pauseAllDownloads();
		#else
		Debug.Log("XXX PauseDownloads");
		#endif
	}

	/**
	 * Pause the download with given index.
	**/
	public void ResumeSingleDownload (string url)
	{
		#if UNITY_ANDROID
		Debug.Log ("Android PauseDownloads");
		pauseSingleDownloadAndroid ();
		#elif UNITY_IOS
		Debug.Log("iOS PauseSingleDownload");
		resumeSingleDownload(url);
		#else
		Debug.Log("XXX PauseDownloads");
		#endif
	}

	/**
	* Resume all current downloads in the queue (Not yet working in Android)
	**/
	public void ResumeDownloads ()
	{
		#if UNITY_ANDROID
		Debug.Log ("Android ResumeDownloads");
		resumeDownloadAndroid ();
		#elif UNITY_IOS
		Debug.Log("iOS ResumeDownloads");
		resumeAllDownloads();
		#else
		Debug.Log("XXX ResumeDownloads");
		#endif
	}

	/**
	* Get current locale of phone.
	**/
	public string GetCurrentLocale ()
	{
		#if UNITY_ANDROID
		Debug.Log ("Android GetCurrentLocale");
		return getMobileDownloadAndroid ().Call<string> ("getLocale");
		#elif UNITY_IOS
		Debug.Log("iOS GetCurrentLocale");
		return currentLocale();
		#else
		Debug.Log("XXX GetCurrentLocale");
        return "";
		#endif
	}

	public delegate void Error (ErrorModel error);

	public delegate void Success (SuccessModel success);

	public delegate void Progress (ProgressModel progress);

	public delegate void Size (SizeModel size);

	/**
	* For internal use
	**/
	public void SuccessMessage (string message)
	{
		Debug.Log ("SuccessMessage received:" + message);
		SuccessModel model = new SuccessModel (message);
		OnSuccess (model);
	}

	/**
	* For internal use
	**/
	public void ErrorMessage (string message)
	{
		Debug.Log ("ErrorMessage received:" + message);
		ErrorModel model = new ErrorModel (message);
		OnError (model);
	}

	/**
	* For internal use
	**/
	public void ProgressMessage (string message)
	{
		Debug.Log ("ProgressMessage received:" + message);
		ProgressModel model = new ProgressModel (message);
		OnProgress (model);
	}

	/**
	* For internal use
	**/
	public void SizeMessage (string message)
	{
		Debug.Log ("SizeMessage received:" + message);
		SizeModel model = new SizeModel (message);
		OnSize (model);
	}

	void OnEnable ()
	{
		#if UNITY_ANDROID
			getMobileDownloadAndroid ().Call ("enable");
		#endif
	}

	void OnDisable ()
	{
		#if UNITY_ANDROID
			getMobileDownloadAndroid ().Call ("disable");
		#endif
	}

	#if UNITY_ANDROID
	//android specific methods For internal use
	private AndroidJavaObject getContext ()
	{
		if (context == null) {
			using (AndroidJavaClass jc = new AndroidJavaClass ("com.unity3d.player.UnityPlayer")) { 
				context = jc.GetStatic<AndroidJavaObject> ("currentActivity");
			}
		}
		return context;
	}
	
	private AndroidJavaObject getMobileDownloadAndroid ()
	{
		if (mobileDownloadAndroid == null) {
			using (AndroidJavaClass mClass = new AndroidJavaClass ("com.mediamonks.mobiledownload.MobileDownloadAndroid")) { 
				mobileDownloadAndroid = mClass.CallStatic<AndroidJavaObject> ("getInstance", getContext ()); 
			}
		}
		return mobileDownloadAndroid;
	}

	private void downloadFileAndroid (string[] urls, string notificationMessage)
	{
		if (urls == null) {
			return;
		}
		JSONObject holder = new JSONObject (JSONObject.Type.OBJECT);
		JSONObject arr = new JSONObject (JSONObject.Type.ARRAY);
		foreach (string url in urls) {
			arr.Add (url);
		}
		holder.AddField ("files", arr);
		holder.AddField ("notificationMessage", notificationMessage);
		getMobileDownloadAndroid ().Call ("downloadFile", 
			getContext (), 
		new AndroidJavaObject ("java.lang.String", holder.ToString ()));
	}

	private void checkFileAndroid (string[] urls)
	{
		if (urls == null) {
			return;
		}
		JSONObject arr = new JSONObject (JSONObject.Type.ARRAY);
		foreach (string url in urls) {
			arr.Add (url);
		}
		getMobileDownloadAndroid ().Call ("checkFile", 
			getContext (), 
			new AndroidJavaObject ("java.lang.String", arr.ToString ()));
	}

	private void deleteFileAndroid (string[] urls)
	{
		if (urls == null) {
			return;
		}
		JSONObject arr = new JSONObject (JSONObject.Type.ARRAY);
		foreach (string url in urls) {
			arr.Add (url);
		}
		getMobileDownloadAndroid ().Call ("deleteFile",
			getContext (),
			new AndroidJavaObject ("java.lang.String", arr.ToString ()));
	}

	private void pauseDownloadAndroid ()
	{
		getMobileDownloadAndroid ().Call ("pauseDownloadAndroid", 
			getContext ());
	}

	private void resumeDownloadAndroid ()
	{
		getMobileDownloadAndroid ().Call ("resumeDownloadAndroid", 
			getContext ());
	}
	#endif

}
