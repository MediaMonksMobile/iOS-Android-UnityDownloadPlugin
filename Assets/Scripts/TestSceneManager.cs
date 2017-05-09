using UnityEngine;
using System.Collections;
using UnityEngine.UI;

/* *
 * Example of MobileDownloadManager usage in Unity3d project. Provides examples of downloading single file,
 * downloading multiple files, showing progress of download, delete downloaded items, check if item downloaded
 * and available to read,
 * */
public class TestSceneManager : MonoBehaviour {


	/* *
	 * Text variables for multiple field in Unity.UI
	 * */
	public Text actionText;
	public Text statusText;
	public Text amountText;

	public Text cellularButtonText;

	public Text loadText;
	public Text deleteText;
	public Text cancelText;
	public Text pauzeText;
	public Text resumeText;

	public Slider progressSlider;

	/* *
	 * Boolean variable for setting the button state
	 * */
	private bool onlyWifi = true;

	/* *
	 * Array with all the urls Strings to be downloaded.
	 * Remember that the ProgressMessage methods are not being called
	 * if the url headers are not available.
	 * Default "www.colocenter.nl" do not support these headers.
	 * http://mirror.nl.leaseweb.net/speedtest/1000mb.bin does
	 * */
	private string[] urls = { 
		"http://www.colocenter.nl/speedtest/25mb.bin",
		"http://www.colocenter.nl/speedtest/50mb.bin",
		"http://www.colocenter.nl/speedtest/100mb.bin" 
	};

	/* *
	 * Notifications message string when file is downloaded from url
	 * */
	static private string notificationMessage = "Downloads are finished downloading! :)";

	void Start () {

	   	//Configure Method calls a instancetype for configuring HTTPMaxConnectionsPerHost and OnlyWifi enabled
		MobileDownloadManager.Instance.Configure (true, 1, true);
		 
		//Get language of the device.
		Debug.Log("Locale: " + MobileDownloadManager.Instance.GetCurrentLocale());

		//Get current user (download over wifi) preference .
		onlyWifi = MobileDownloadManager.Instance.IsWifiOnly();
		cellularButtonText.text = "Only over WiFI: " + onlyWifi;

		// Subscribe for different events.
		MobileDownloadManager.Instance.OnSuccess += EventDownloaded;
		MobileDownloadManager.Instance.OnError += EventError;
		MobileDownloadManager.Instance.OnProgress += EventProgress;
		MobileDownloadManager.Instance.OnSize += EventDownloadSize;

		if (Application.platform == RuntimePlatform.Android) {
			//get path to public folder for downloads in Android.
			getAndroidDownloadPath();
		}

		//check file status
		CheckFileStatus ();
	}

	void Update () {
		if (Application.platform == RuntimePlatform.Android) {
			// Quit from app on android back button press
			if (Input.GetKey (KeyCode.Escape)) {
				Application.Quit();
			}
		}
	}

	/* *
	 * Prints path to public folder for downloads in Android if it's mounted and available to read.
	 * */
	public void getAndroidDownloadPath(){
		string downloadPath = MobileDownloadManager.Instance.GetAndroidDownloadPath();
		if(downloadPath==null){
			Debug.Log("No download path from android");
		}else{
			Debug.Log("Download path on android is located at:"+downloadPath);
		}
	}

	/* *
	 * Check if file/files from url/urls are downloaded and available.
	 * */
	public void CheckFileStatus(){
		
		MobileDownloadManager.Instance.CheckFilesExist(urls);

		actionText.text = "Checking if Files 10/1/5 exists";
	}

	/* *
	 * Downloads one file from a certain Index.
	 * */
	public void LoadFile(){

		if (!isValidTextInput(loadText.text)) {
			actionText.text = "WARNING: Please enter a number first...";
			return;
		}

		int index = int.Parse(loadText.text);

		if (!isValidIndex(index)) {
			actionText.text = "WARNING: Invalid number, use 0...2";
			return;
		}

		string [] tempUrls = new string[]  { urls[index] };

		MobileDownloadManager.Instance.DownloadFiles(tempUrls, notificationMessage);

		actionText.text = "LoadFile started";
	}

	/* *
	 * Downloads multiple files.
	 * */
	public void LoadFiles(){
		actionText.text = "LoadFiles started";

		MobileDownloadManager.Instance.DownloadFiles(urls, notificationMessage);
	}

	/* *
	 * Delete single file.
	 * */
	public void DeleteFile(){

		if (!isValidTextInput(deleteText.text)) {
			actionText.text = "WARNING: Please enter a number first...";
			return;
		}

		int index = int.Parse(deleteText.text);

		if (!isValidIndex(index)) {
			actionText.text = "WARNING: Invalid number, use 0...2";
			return;
		}

		actionText.text = "DeleteFile started";

		string [] tempUrls = new string[]  { urls[index] };

		MobileDownloadManager.Instance.DeleteFiles (tempUrls);
	}

	/* *
	 * Delete multiple files.
	 * */
	public void DeleteFiles(){
		actionText.text = "DeleteFiles started";

		MobileDownloadManager.Instance.DeleteFiles (urls);
	}

	/* *
	 * Cancel a single download based on index of TextField.
	 * */
	public void CancelSingleDownload(){

		actionText.text = "Canceling Single Download";

		if (!isValidTextInput(cancelText.text)) {
			actionText.text = "WARNING: Please enter a number first...";
			return;
		}

		int index = int.Parse(cancelText.text);

		if (!isValidIndex(index)) {
			actionText.text = "WARNING: Invalid number, use 0...2";
			return;
		}

		MobileDownloadManager.Instance.CancelSingleDownload (urls[index]);
	}

	/* *
	 * Stops and Cancels all downloads.
	 * */
	public void CancelDownloads(){
		actionText.text = "Canceling Downloads";

		MobileDownloadManager.Instance.CancelDownloads ();
	}

	/* *
	 * Pausing Single download.
	 * */
	public void PauseSingleDownload(){
		actionText.text = "Pausing Single Downloads";

		if (!isValidTextInput(pauzeText.text)) {
			actionText.text = "WARNING: Please enter a number first...";
			return;
		}
			
		int index = int.Parse(pauzeText.text);

		if (!isValidIndex(index)) {
			actionText.text = "WARNING: Invalid number, use 0...2";
			return;
		}

		MobileDownloadManager.Instance.PauseSingleDownload (urls[index]);
	}

	/* *
	 * Pausing all downloads.
	 * */
	public void PauseDownloads() {
		actionText.text = "Pausing Downloads";

		MobileDownloadManager.Instance.PauseDownloads();
	}

	/* *
	 * Resuming Single download.
	 * */
	public void ResumeSingleDownload() {
		actionText.text = "Resuming Single Downloads";

		if (!isValidTextInput(resumeText.text)) {
			actionText.text = "WARNING: Please enter a number first...";
			return;
		}

		int index = int.Parse(resumeText.text);

		if (!isValidIndex(index)) {
			actionText.text = "WARNING: Invalid number, use 0...2";
			return;
		}
			
		MobileDownloadManager.Instance.ResumeSingleDownload(urls[index]);
	}

	/* *
	 * Resuming all downloads.
	 * */
	public void ResumeDownloads() {
		actionText.text = "Resuming Downloads";

		MobileDownloadManager.Instance.ResumeDownloads();
	}

	/* *
	 * Switch on/off downloads over wifi preference.
	 * */
	public void OnlyWifiSwitch(){
		onlyWifi = !onlyWifi;
		cellularButtonText.text = "Only over WiFI: " + onlyWifi;

		MobileDownloadManager.Instance.SetEnableWifiOnly (onlyWifi);
	}


	// MARK: - Helper Methods


	/* *
	 * Check if index is valid.
	 * */
	private bool isValidIndex(int index)
	{
		if (index >= urls.Length) {
			return false;
		}

		return true;
	}

	/* *
	 * Check if TextInput is not empty.
	 * */
	private bool isValidTextInput(string text)
	{
		if (text.Length == 0) {
			return false;
		}

		return true;
	}


	// MARK: - Events Methods


	/* *
	 * Event that will be called with information about successfuly downloaded file.
	 * */
	void EventDownloaded(SuccessModel success)
	{
		Debug.Log ("Downloaded: " + success.mSuccessMessage);
		actionText.text = "Success: " + success.mSuccessMessage +
		" State: " + success.mSuccessType+" "+
			((success.mFiles!=null)?success.mFiles.Length+" files "+ success.mFiles[0].mFilePath:" no files");

		progressSlider.value = (float)0;
	}

	/* *
	 * Event that will be called with information about error that occured.
	 * */
	void EventError(ErrorModel error)
	{
		Debug.Log ("Error: "+error.mErrorMessage);
		actionText.text = "Error: "+error.mErrorMessage +
			" State: " + error.mErrorType+" "+
			((error.mFiles!=null)?error.mFiles.Length+" files "+ error.mFiles[0].mFilePath:" no files");

		progressSlider.value = (float)0;
	}

	/* *
	 * Event that will be called with information about the progress of currently downloading file, 
	 * multiple files can be downloaded at the same time on Android, so this event will be called for each of them.
	 * */
	void EventProgress(ProgressModel progress)
	{
		statusText.text = "Progress: "+progress.mProgress+"%"+
			" File in group "+progress.mGroupPosition+"/"+progress.mGroupSize+
			" File: "+((progress.mFile != null)?progress.mFile.mFilePath:" no file info")+
			" State: "+progress.mProgressType;

		progressSlider.value = (float)progress.mProgress;
	}

	/* *
	 * Event that will be called with information about the current downloadURLPaths size of the array 
	 * inside the iOS Directory
	 * */
	void EventDownloadSize(SizeModel size)
	{
		amountText.text = size.mSizeMessage;
	}
}
