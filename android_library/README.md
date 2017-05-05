# MobileDownload Unity3d library (Android)
 Android library for Unity3d projects for Downloading file/files (mostly large video files, VR video etc) in background while Unity player is not active. Return progress,success,error information to Unity3d project through C# delegates and easy to use Singleton API class.

#Integration into Unity3d project
1) Copy the Assets/Plugins/Android folder and Assets/Plugins/MobileDownloadPlugin folder
    to you Unity3d project Assets/Plugins folder.

2) You might need to tweak Assets/Plugins/Android/AndroidManifest.xml file for your needs,
    or you can delete it if you already have your own version, in this case you'll need to
     add next things to your existing AndroidManifest.xml file:

     ```
     <application>
     ...
        <receiver
            android:name="com.mediamonks.mobiledownload.DownloadNotificationReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.DOWNLOAD_COMPLETE" />
            </intent-filter>
        </receiver>
     ...
     </application>
     <!-- Set target sdk version to Lollipop to prevent issues with Marshmallow's runtime permissions. -->
     <uses-sdk android:targetSdkVersion="22" />
     ```

3) You ready to go! You can find the full example demo project in Assets/ folder.
For example of usage MobileDownloadManager from you C# code look for:
Assets/Scripts/TestSceneManager.cs

## Dependencies and Libraries used
- google gson [github](https://github.com/google/gson) - A Java serialization/deserialization library that can convert Java Objects into JSON and back.

## Support
any questions:
(Android) - raphael.gilyazitdinov@mediamonks.com, stephan@mediamonks.com
(iOS) - peterg@mediamonks.com