# Unity Plugin iOS

### IMPORTANT: Still in beta

Some functions will not work properly.

## General

This application downloads (multiple) data from the NSURLSession iOS plugin.
The following options are available: 

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

## How to use

- Download or Clone project
- Open Unity-iPhone.xcodeproj
- Check Info.plist for <key>UIBackgroundModes</key> key for Local Notificatons 
- Run Project

## Updates

### 1.0

#### iOS Plugin
- Fixed Bug for resuming downloadTasksWithData;
- Added Single Delete/Pause/Cancel/Resume functionalities;
- Added update amountLabel function with SendMessageToUnity functionality.

#### Unity UI
- Added Textfield for Delete/Pause/Cancel/Resume functionalities;
- Added Download amount label.
