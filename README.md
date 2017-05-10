# Unity Plugin iOS


## General

This application downloads (multiple) data from the NSURLSession iOS plugin.
The following options are available: 

- Progress state
- Change Only over Wifi

Download:
- Single by textfield;
- All at once.

Pause download:
- Single by textfield;
- All at once.

Resume download:
- Single by textfield;
- All at once.

Cancel download:
- Single by textfield;
- All at once.

Delete download:
- Single by textfield;
- All at once.

## Images

![Unity UI](https://raw.githubusercontent.com/MediaMonksMobile/iOSUnityDownloadPlugin/master/Screenshots/UnityUI.PNG)

## How to use

- Download or Clone project
- Open Unity
- Press cmd + B to build the project
- Select iOS as build target
- Select a target folder with name
- Xcode will open automatically or open Unity-iPhone.xcodeproj
- Check Info.plist for <key>UIBackgroundModes</key> key for Local Notifications 
- Run Project

## Updates

### 1.2

#### iOS Plugin
- Refactoring.

#### Unity UI
- Added progress slider.

### 1.1

#### iOS Plugin
- Added Single Notifications;
- Refactoring.

#### Unity UI
- Removed unnecessary code.

### 1.0

#### iOS Plugin
- Fixed Bug for resuming downloadTasksWithData;
- Added Single Delete/Pause/Cancel/Resume functionalities;
- Added update amountLabel function with SendMessageToUnity functionality.

#### Unity UI
- Added Textfield for Delete/Pause/Cancel/Resume functionalities;
- Added Download amount label.
