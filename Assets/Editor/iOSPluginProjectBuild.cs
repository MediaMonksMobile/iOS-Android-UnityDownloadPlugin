using UnityEngine;
using UnityEditor;
using UnityEditor.Callbacks;
using System.IO;
using UnityEditor.iOS.Xcode;

public class iOSPluginProjectBuild {

	[PostProcessBuild(9999)]
	public static void OnPostprocessBuild(BuildTarget buildTarget, string buildPath)
	{
		#if UNITY_IPHONE

			// Get plist
			string plistPath = buildPath + "/Info.plist";
			PlistDocument plist = new PlistDocument();
			plist.ReadFromString(File.ReadAllText(plistPath));

			// Get root 
			PlistElementDict rootDict = plist.root;

			// Add remote-notification Xcode plist.info
			var buildKey = "UIBackgroundModes";
			rootDict.CreateArray (buildKey).AddString ("remote-notification");

			// Write to plist
			File.WriteAllText(plistPath, plist.WriteToString());

		#endif
	}

}
