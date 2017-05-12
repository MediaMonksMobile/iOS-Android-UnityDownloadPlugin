# Unity Plugin iOS/Android


## General

This application downloads (multiple) data from the NSURLSession iOS plugin.
The following options are available within the plugin: 

- Progress state of a single download
- Download/Pause/Resume/Cancel/Delete single & multiple sessions
- Only download on wifi state
- The amount of downloaded files
- Set the maximum number of simultaneous connections to each host per task
- Set a notification per download or when all downloads are completed
- Automatically import need plist items

## Images

![Unity UI](https://raw.githubusercontent.com/MediaMonksMobile/iOSUnityDownloadPlugin/master/Screenshots/UnityUI.PNG)

## How to use

- Download or Clone project
- Open Unity
- Press `CMD + B` to build the project
- Select iOS as build target
- Select a target folder with name
- Xcode will open automatically or open `Unity-iPhone.xcodeproj`
- Run Project

## Updates

### 1.3

##### Unity UI
- Added iOSPluginProjectBuild

### 1.2

##### iOS Plugin
- Refactoring.

##### Unity UI
- Added progress slider.

### 1.1

#### iOS Plugin
- Added Single Notifications;
- Refactoring.

##### Unity UI
- Removed unnecessary code.

### 1.0

##### iOS Plugin
- Fixed Bug for resuming downloadTasksWithData;
- Added Single Delete/Pause/Cancel/Resume functionalities;
- Added update amountLabel function with SendMessageToUnity functionality.

##### Unity UI
- Added Textfield for Delete/Pause/Cancel/Resume functionalities;
- Added Download amount label.
