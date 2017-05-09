//
//  mobiledownloadmanager.mm
//  mobiledownloadmanager
//
//  Created by Peter Gerdes on 13/04/16.
//  Copyright © 2016 MediaMonks. All rights reserved.
//

#import "mobiledownloadmanager.h"
#import "FileDownloadInfo.h"
#import "WMLSwizzler.h"

#include <sys/mount.h>
#include "UnityInterface.h"

@interface mobiledownloadmanager () <NSURLSessionDelegate, NSURLSessionDownloadDelegate>

@property (nonatomic, strong) NSURLSession *session;
@property (nonatomic, strong) NSMutableArray<FileDownloadInfo *> *fileDownloads;
@property (nonatomic, strong) NSURL *docDirectoryURL;
@property (nonatomic) BOOL singleNotification;

@end

static mobiledownloadmanager *globalSelf;

typedef id (*IMPPlus)(id, SEL, UIApplication*, NSString*, void (^completionHandler)());

@implementation mobiledownloadmanager


/*
* Default initializer
* Calls initWithWifi:maxConnections:notifyUserOnce 
* with default settings
*/
- (instancetype)init {
    return [self initWithWifiOnly:true maxConnections:1 notifyUserOnce:true];
}

/*
* Custom initializer
* @params(bool) for applying wifi settings
* @params(int) for applying the max HTTPMaximumConnectionsPerHost
* @params(bool) for applying the setting the local notifications per completed download
*/
- (instancetype)initWithWifiOnly:(bool)status maxConnections:(int)amount notifyUserOnce:(bool)notifyUserOnce {
	self = [super init];
	if (self) {
        
		globalSelf = self;
        
        _singleNotification = notifyUserOnce;

        NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration backgroundSessionConfigurationWithIdentifier:[NSString stringWithFormat:@"com.mediamonks.mobiledownloadmanager"]];
        sessionConfiguration.HTTPMaximumConnectionsPerHost = amount;
        
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        sessionConfiguration.allowsCellularAccess = [defaults boolForKey:@"com.mediamonks.mobiledownloadmanager.cellularAllowed"];
        
        NSLog(@"mobiledownloadmanager - Allow cellular: %i", sessionConfiguration.allowsCellularAccess);
        
        self.session = [NSURLSession sessionWithConfiguration:sessionConfiguration
                                                     delegate:self
                                                delegateQueue:nil];

		self.fileDownloads = [[NSMutableArray alloc] init];

		NSArray *URLs = [[NSFileManager defaultManager] URLsForDirectory:NSCachesDirectory inDomains:NSUserDomainMask];
		self.docDirectoryURL = URLs[0];
        
        // Setup Local UserNotification
        
        UIUserNotificationType types = UIUserNotificationTypeAlert;
        UIUserNotificationSettings *mySettings = [UIUserNotificationSettings settingsForTypes:types categories:nil];
        [[UIApplication sharedApplication] registerUserNotificationSettings:mySettings];
        
        // AppDelegate selector injection for local notification
        
        SEL setObjectSelector = @selector(application:handleEventsForBackgroundURLSession:completionHandler:);
        __block IMPPlus originalIMP = (IMPPlus)[[[UIApplication sharedApplication].delegate class] wml_replaceInstanceMethod:setObjectSelector withBlock:^(id self, UIApplication *application, NSString *identifier, void (^completionHandler)()){
            NSLog(@"mobiledownloadmanager -  Download done!");
            
            if(originalIMP)
            {
                originalIMP(self, setObjectSelector, application, identifier, completionHandler);
            }
        }];
	}

	return self;
}

#pragma mark - Methods callable from Unity

+ (void)configureWithWifiOnly:(bool)status maxConnections:(int)maxConnections notifyUserOnce:(bool)notifyUserOnce
{
    if(!globalSelf)
    {
        globalSelf = [[mobiledownloadmanager alloc] initWithWifiOnly:status maxConnections:maxConnections notifyUserOnce:notifyUserOnce];
    }
}

+ (void)downloadFilesWithURLS:(NSArray <NSString *> *)urls andMessage:(NSString *)message
{
    if(!globalSelf)
    {
        globalSelf = [[mobiledownloadmanager alloc] init];
    }
    
    for (NSUInteger i = 0; i < urls.count; ++i) {
        NSLog(@"mobiledownloadmanager - Download: %@", urls[i]);
        [globalSelf startDownloadWithURL:urls[i] andMessage:message];
    }
}

+ (void)deleteFileWithName:(NSString *)name
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	NSArray *array = @[ name ];

	[globalSelf deleteFileWithNames:array];
}

+ (void)deleteFileWithNames:(NSArray <NSString *> *)names
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	[globalSelf deleteFileWithNames:names];
}

+ (void)setAllowCellular:(BOOL)allowed
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	[globalSelf setCellularAllowed:allowed];
}

+ (BOOL)allowCellular
{
	NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
	return [defaults boolForKey:@"com.mediamonks.mobiledownloadmanager.cellularAllowed"];
}

+ (void)cancelSingleDownload:(NSString *)url
{
    if(!globalSelf)
    {
        globalSelf = [[mobiledownloadmanager alloc] init];
    }
    
    [globalSelf cancelSingleDownload:url];
}

+ (void)cancelAllDownloads
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	[globalSelf cancelAllDownloads];
}

+ (void)pauseSingleDownload:(NSString *)url
{
    if(!globalSelf)
    {
        globalSelf = [[mobiledownloadmanager alloc] init];
    }
    
    [globalSelf pauseSingleDownload:url];
}

+ (void)pauseAllDownloads
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	[globalSelf pauseAllDownloads];
}

+ (void)resumeSingleDownload:(NSString *)url
{
    if (!globalSelf)
    {
        globalSelf = [[mobiledownloadmanager alloc] init];
    }
    
    [globalSelf resumeSingleDownload:url];
}

+ (void)resumeAllDownloads
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	[globalSelf resumeAllDownloads];
}

+ (void)checkFileExistWithName:(NSString *)name
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	NSArray *names = @[ name ];

	[globalSelf checkFilesWithNames:names];
}

+ (void)checkFilesExistWithNames:(NSArray <NSString *> *)names
{
	if(!globalSelf)
	{
		globalSelf = [[mobiledownloadmanager alloc] init];
	}

	[globalSelf checkFilesWithNames:names];
}

+ (NSString *)currentLocale
{
    NSLocale *locale = [NSLocale currentLocale];
    NSString *countryCode = [locale objectForKey: NSLocaleCountryCode];
    NSString *languageCode = [locale objectForKey:NSLocaleLanguageCode];
    return [NSString stringWithFormat:@"%@_%@", languageCode,countryCode];
}

#pragma mark - Internal methods

/*
* Checks if the download allready excists in the phones directory and
* sets a download session on and FileDownloadInfo object with given values.
* At last it will send a status update to Unity
* @params(NSString) for applying the given url string
* @params(NSString) for applying the given notification message string
*/
- (void)startDownloadWithURL:(NSString *)url andMessage:(NSString *)message
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSURL *URL = [NSURL URLWithString:url];
    
    NSString *destinationFilename = URL.lastPathComponent;
    NSURL *destinationURL = [self.docDirectoryURL URLByAppendingPathComponent:destinationFilename];
    
    if ([fileManager fileExistsAtPath:[destinationURL path]]) {
        //File is already downloaded.
        NSLog(@"mobiledownloadmanager - File already exists.");
        
        NSDictionary *JSONDic = @{
            @"mFilePath": destinationURL.absoluteString,
            @"mFileUrl": url
        };
        
        NSDictionary *JSONData = @{
            @"mSuccessType": @0,
            @"mSuccessMessage": @"File already exists.",
            @"mFiles":@[JSONDic]
        };
        
        [self sendMessageToUnity:"SuccessMessage" JSONDataDictionary: JSONData];
        
        return;
    }
    
    for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
    
        FileDownloadInfo *fdi = self.fileDownloads[i];
        
        if([fdi.downloadURL isEqualToString:url]) {
        
            if(fdi.isDownloading) {
                NSLog(@"mobiledownloadmanager - File already downloading");
                return;
            }
            
            if(fdi.downloadComplete) {
                NSLog(@"mobiledownloadmanager - File already downloaded");
                return;
            }
        }
    }
    
    NSLog(@"mobiledownloadmanager - Start downloading: %@", url);
    
    FileDownloadInfo *fdi = [[FileDownloadInfo alloc] initWithDownloadURL:url];
    
    fdi.downloadTask = [self.session downloadTaskWithURL:[NSURL URLWithString:fdi.downloadURL]];
    fdi.notificationMessage = message;
    fdi.taskIdentifier = fdi.downloadTask.taskIdentifier;
    fdi.isDownloading = YES;
    [fdi.downloadTask resume];
    
    [self.fileDownloads addObject:fdi];
}

/*
* Checks if the url excists in the mobiles directory and
* removes the data in the path with the commen created FileDownloadInfo.
* At last it will send a status update to UnityUI.
* @params(NSArray<NSString>) for applying the givens urls in a array
* @params(NSString) for applying the given notification message string
*/
- (void)deleteFileWithNames:(NSArray <NSString *>*)names
{
	BOOL success = YES;
	NSMutableArray *dicArray = [[NSMutableArray alloc] init];

	for (NSUInteger j = 0; j < names.count; ++j) {
		NSFileManager *fileManager = [NSFileManager defaultManager];

		NSURL *URL = [NSURL URLWithString:names[j]];
		NSString *deleteFilename = URL.lastPathComponent;
		NSURL *removeURL = [self.docDirectoryURL URLByAppendingPathComponent:deleteFilename];

		for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
			FileDownloadInfo *fdi = self.fileDownloads[i];
            
			NSURL *fdiURL = [NSURL URLWithString:fdi.downloadURL];
            
			if([fdiURL.lastPathComponent isEqual:deleteFilename]) {
            
				NSLog(@"mobiledownloadmanager - Cancel download");
                
				[fdi.downloadTask cancel];
                
				[self.fileDownloads removeObject:fdi];
			}
		}

		if ([fileManager fileExistsAtPath:[removeURL path]]) {
        
			BOOL deleteSuccess = [fileManager removeItemAtURL:removeURL error:nil];

			if(deleteSuccess) {
				NSLog(@"mobiledownloadmanager - Deleted file: %@", removeURL.lastPathComponent);

				NSDictionary *JSONDic = @{
                    @"mFilePath": removeURL.absoluteString,
                    @"mFileUrl": URL.absoluteString
				};

				[dicArray addObject:JSONDic];
                
			} else {
				success = NO;
			}
            
		} else {
			success = NO;
		}
	}

	NSString *message;

	if(success) {
    
		message = @"Deleted.";
        NSLog(@"Deleted");
        
	} else {
    
		message = @"Deletion encountered some errors";
        NSLog(@"Error found while Deleting");
	}

	NSDictionary *JSONData = @{
        @"mSuccessType": @2,
        @"mSuccessMessage": message,
        @"mFiles": dicArray
	};

	if(success && names.count >= 1) {
		[self sendMessageToUnity:"SuccessMessage" JSONDataDictionary:JSONData];
	} else {
		[self sendMessageToUnity:"ErrorMessage" JSONDataDictionary:JSONData];
	}
}

/*
* Loops through all the download objects and checks if a download session is active.
* If so, it will compare it with the given @param and pauses and saves the allready 
* downloaded data in the FileDownloadInfo object for later use.
* NSURLSession delegate will catch this action and updates UnityUI.
* @params(NSString) for applying the given url string
*/
- (void)pauseSingleDownload:(NSString *)name
{
    NSLog(@"mobiledownloadmanager - Pause single downloads");
    for (NSUInteger i = 0; i < self.fileDownloads.count; i++) {
        FileDownloadInfo *fdi = self.fileDownloads[i];
        if ([fdi.downloadURL isEqual:name]) {
            [fdi.downloadTask cancelByProducingResumeData:^(NSData *resumeData) {
                if (resumeData != nil) {
                    fdi.taskResumeData = [[NSData alloc] initWithData:resumeData];
                }
                fdi.isDownloading = NO;
            }];
        }
    }
}

/*
* Loops through all the download objects and checks if a download session is active.
* If so, it will pauses and saves the allready downloaded data in 
* the FileDownloadInfo object for later use.
* NSURLSession delegate will catch this action and updates UnityUI.
*/
- (void)pauseAllDownloads
{
	NSLog(@"mobiledownloadmanager - Pause all downloads");
	for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
		FileDownloadInfo *fdi = self.fileDownloads[i];
		[fdi.downloadTask cancelByProducingResumeData:^(NSData *resumeData) {
			if (resumeData != nil) {
				fdi.taskResumeData = [[NSData alloc] initWithData:resumeData];
			}
			fdi.isDownloading = NO;
		}];
	}
}

/*
* Loops through all the download objects and checks if a download is paused.
* If so, it will compare it with the @param and resumes the download with the saved data.
* NSURLSession delegate will catch this action and updates UnityUI.
* @params(NSString) for applying the given url string
*/
- (void)resumeSingleDownload:(NSString *)name
{
    NSLog(@"resumeSingleDownload: %@", name);
    
    for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
        FileDownloadInfo *fdi = self.fileDownloads[i];
        if ([fdi.downloadURL isEqual:name]){
            if(!fdi.isDownloading && !fdi.downloadComplete)
            {
                if (fdi.taskResumeData == nil) {
                    NSLog(@"URL: %@ has not data yet", fdi.downloadURL);
                    break;
                }
                
                // Bug fux for NULL reference on ResumeData
                NSData *cData = [self correctResumData:fdi.taskResumeData];
                if (!cData) {
                    cData = fdi.taskResumeData;
                }
                fdi.downloadTask = [self.session downloadTaskWithResumeData:cData];
                if ([self getResumDictionary:cData]) {
                    NSDictionary *dict = [self getResumDictionary:cData];
                    if (!fdi.downloadTask.originalRequest) {
                        NSData *originalData = dict[kResumeOriginalRequest];
                        [fdi.downloadTask setValue:[NSKeyedUnarchiver unarchiveObjectWithData:originalData] forKey:@"originalRequest"];
                    }
                    if (!fdi.downloadTask.currentRequest) {
                        NSData *currentData = dict[kResumeCurrentRequest];
                        [fdi.downloadTask setValue:[NSKeyedUnarchiver unarchiveObjectWithData:currentData] forKey:@"currentRequest"];
                    }
                }
                
                [fdi.downloadTask resume];
                fdi.taskIdentifier = fdi.downloadTask.taskIdentifier;
                fdi.isDownloading = !fdi.isDownloading;
            }
        }
    }
}

/*
* Loops through all the download objects and checks if a download is paused.
* If so, it will resume the download with the saved data.
* NSURLSession delegate will catch this action and updates UnityUI.
*/
- (void)resumeAllDownloads
{
	NSLog(@"mobiledownloadmanager - Resume all downloads");
	for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
		FileDownloadInfo *fdi = self.fileDownloads[i];
		if(!fdi.isDownloading && !fdi.downloadComplete)
		{
            if (fdi.taskResumeData == nil) {
                NSLog(@"ResumeData of %@ is nil", fdi.downloadURL);
                break;
            }
            
            // Bug fix for NULL reference on ResumeData
            NSData *cData = [self correctResumData:fdi.taskResumeData];
            if (!cData) {
                cData = fdi.taskResumeData;
            }
            fdi.downloadTask = [self.session downloadTaskWithResumeData:cData];
            if ([self getResumDictionary:cData]) {
                NSDictionary *dict = [self getResumDictionary:cData];
                if (!fdi.downloadTask.originalRequest) {
                    NSData *originalData = dict[kResumeOriginalRequest];
                    [fdi.downloadTask setValue:[NSKeyedUnarchiver unarchiveObjectWithData:originalData] forKey:@"originalRequest"];
                }
                if (!fdi.downloadTask.currentRequest) {
                    NSData *currentData = dict[kResumeCurrentRequest];
                    [fdi.downloadTask setValue:[NSKeyedUnarchiver unarchiveObjectWithData:currentData] forKey:@"currentRequest"];
                }
            }
            
			[fdi.downloadTask resume];
            fdi.taskIdentifier = fdi.downloadTask.taskIdentifier;
            fdi.isDownloading = !fdi.isDownloading;
		}
	}
}

/*
* Loops through all the download objects and compares it with the @param.
* If so, it will cancel the download without saving the data.
* NSURLSession delegate will catch this action and updates UnityUI.
* @params(NSString) for applying the given url string
*/
- (void)cancelSingleDownload:(NSString *)name
{
    NSLog(@"mobiledownloadmanager - Cancel single downloads");
    for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
        FileDownloadInfo *fdi = self.fileDownloads[i];
        if ([fdi.downloadURL isEqual:name]) {
            [fdi.downloadTask cancel];
            
            [self.fileDownloads removeObjectAtIndex:i];
        }
    }
}

/*
* Loops through all the download objects.
* Next, it will cancel all downloads without saving the data.
* NSURLSession delegate will catch this action and updates UnityUI.
*/
- (void)cancelAllDownloads
{
	NSLog(@"mobiledownloadmanager - Cancel all downloads");
	for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
		FileDownloadInfo *fdi = self.fileDownloads[i];
		[fdi.downloadTask cancel];
	}
	[self.fileDownloads removeAllObjects];
}

/*
* Updates the newly created cellularStatus from Unity in UserDefaults
* @params(BOOL) for applying the given cellular status.
*/
- (void)setCellularAllowed:(BOOL)allowed
{
	NSLog(@"mobiledownloadmanager - Set cellular allowed to: %i", allowed);
	[self.session.configuration setAllowsCellularAccess:allowed];
	NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
	[defaults setBool:allowed forKey:@"com.mediamonks.mobiledownloadmanager.cellularAllowed"];
	[defaults synchronize];
}

/*
* Checks if the urls excists in the phones directory
* Depending on the outocme it will send a update to UnityUI.
* @params(NSArray<NSString>) for applying the given string array.
*/
- (void)checkFilesWithNames:(NSArray <NSString *>*)names
{
	NSMutableArray *dicArray = [[NSMutableArray alloc] init];
	BOOL success = YES;

	for (NSUInteger i = 0; i < names.count; ++i) {
		NSFileManager *fileManager = [NSFileManager defaultManager];

		NSURL *URL = [NSURL URLWithString:names[i]];
		NSString *checkFilename = URL.lastPathComponent;
		NSURL *checkURL = [self.docDirectoryURL URLByAppendingPathComponent:checkFilename];

		if ([fileManager fileExistsAtPath:[checkURL path]]) {
			NSDictionary *JSONDic = @{
                @"mFilePath": checkURL.absoluteString,
                @"mFileUrl": URL.absoluteString
			};

			[dicArray addObject:JSONDic];
            
		} else {
			success = NO;
		}
	}
    
    if (dicArray.count != 0) {
        [self getDownloadedFilesLength:dicArray];
        
        NSDictionary *JSONDic = @{
            @"mFilePath": @"NO PATH",
            @"mFileUrl": @"NO URL"
        };
        
        [dicArray addObject:JSONDic];
    }

	NSString *message;

	if(success) {
		message = @"Files exist.";
	} else {
		message = @"(Some) files do not exist.";
	}

	NSDictionary *JSONData = @{
        @"mSuccessType": @0,
        @"mSuccessMessage": message,
        @"mFiles": dicArray
	};

	if (success && names.count >= 1) {
		[self sendMessageToUnity:"SuccessMessage" JSONDataDictionary:JSONData];
	} else {
		[self sendMessageToUnity:"ErrorMessage" JSONDataDictionary:JSONData];
	}
}

#pragma mark - URLSession delegate

- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didFinishDownloadingToURL:(NSURL *)location
{
	NSError *error;
	NSFileManager *fileManager = [NSFileManager defaultManager];

	NSString *destinationFilename = downloadTask.originalRequest.URL.lastPathComponent;
	NSURL *destinationURL = [self.docDirectoryURL URLByAppendingPathComponent:destinationFilename];

	BOOL success = [fileManager moveItemAtURL:location toURL:destinationURL error:&error];
    
	NSUInteger index = [self getFileDownloadInfoIndexWithTaskIdentifier:downloadTask.taskIdentifier];
	FileDownloadInfo *fdi = self.fileDownloads[index];
    
    if (success) {
        
        fdi.isDownloading = NO;
        fdi.downloadComplete = YES;
        fdi.taskIdentifier = (unsigned long) -1;
        fdi.taskResumeData = nil;
        fdi.downloadPath = destinationURL.absoluteString;
        
        if(!_singleNotification) {
            
            BOOL shouldSendNotification = YES;
            
            for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
            
                FileDownloadInfo *fdInfo = self.fileDownloads[i];
                
                if(fdInfo.isDownloading) {
                    shouldSendNotification = NO;
                }
            }
            
            if(shouldSendNotification) {

                [self sendLocalNotification:fdi.notificationMessage];
                
                NSLog(@"mobiledownloadmanager - All downloads complete, send local notification.");
            }
            
        } else {
            
            [self sendLocalNotification:[NSString stringWithFormat:@"Single download %@ is ready", fdi.downloadURL]];
            
            NSLog(@"mobiledownloadmanager - %@ downloads complete, send local notification.", fdi.downloadURL);
        }
        
        NSMutableArray *dicArray = [[NSMutableArray alloc] init];
        
        for (NSUInteger i = 0; i < self.fileDownloads.count; ++i) {
            FileDownloadInfo *fdInfo = self.fileDownloads[i];
            
            NSDictionary *JSONDic = @{
                @"mFilePath": fdInfo.downloadPath,
                @"mFileUrl": fdInfo.downloadURL
            };
            
            [dicArray addObject:JSONDic];
        }
        
        NSDictionary *JSONData = @{
            @"mSuccessType": @0,
            @"mSuccessMessage": @"Downloaded.",
            @"mFiles": dicArray
        };
        
        [self sendMessageToUnity:"SuccessMessage" JSONDataDictionary:JSONData];

        if (!_singleNotification) {
            [self.fileDownloads removeAllObjects];
        } else {
            [self.fileDownloads removeObjectAtIndex:index];
        }
        
    } else {
        
		NSLog(@"mobiledownloadmanager - Unable to move temp file. Error: %@", [error localizedDescription]);

		NSDictionary *JSONFileDic = @{
            @"mFilePath": fdi.downloadPath,
            @"mFileUrl": fdi.downloadURL
		};

		NSDictionary *JSONDic = @{
            @"mErrorType": @2,
            @"mErrorMessage": @"Not enough space.",
            @"mFiles": @[JSONFileDic]
		};
        
        [self sendMessageToUnity:"ErrorMessage" JSONDataDictionary:JSONDic];
	}
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
	if(error) {
    
		NSInteger errorType = [self getErrorMessage:error];

		NSLog(@"mobiledownloadmanager - Error Downloading: %@", [error localizedDescription]);

		NSDictionary *JSONFileDic;
		NSString *errorMessage;
		NSUInteger index = [self getFileDownloadInfoIndexWithTaskIdentifier:task.taskIdentifier];

		if(self.fileDownloads.count && index <= self.fileDownloads.count) {
			FileDownloadInfo *fdi = self.fileDownloads[index];

			JSONFileDic = @{
                @"mFilePath": fdi.downloadPath,
                @"mFileUrl": fdi.downloadURL
			};

			errorMessage = @"Downloading file failed.";
            
		} else {
        
			JSONFileDic = @{
                @"mFilePath": @"",
                @"mFileUrl": task.currentRequest.URL.absoluteString
			};

			errorMessage = @"User canceled download.";
		}

		NSDictionary *JSONDic = @{
            @"mErrorType": @(errorType),
            @"mErrorMessage": errorMessage,
            @"mFiles": @[JSONFileDic]
		};

		[self sendMessageToUnity:"ErrorMessage" JSONDataDictionary:JSONDic];
	}
}

- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didWriteData:(int64_t)bytesWritten totalBytesWritten:(int64_t)totalBytesWritten totalBytesExpectedToWrite:(int64_t)totalBytesExpectedToWrite
{
	if (totalBytesExpectedToWrite == NSURLSessionTransferSizeUnknown) {
        
        NSLog(@"mobiledownloadmanager - Unknown transfer size");
        
	} else {
        
		NSUInteger index = [self getFileDownloadInfoIndexWithTaskIdentifier:downloadTask.taskIdentifier];
        
		if (index <= self.fileDownloads.count) {
            
			FileDownloadInfo *fdi = self.fileDownloads[index];

			if(totalBytesExpectedToWrite > [self diskSpace])
			{
				[session invalidateAndCancel];
				fdi.isDownloading = NO;
				fdi.downloadComplete = NO;
				[self.fileDownloads removeObject:fdi];

				NSLog(@"mobiledownloadmanager - Not enough space to download!");

				NSDictionary *JSONFileDic = @{
                    @"mFilePath": fdi.downloadPath,
                    @"mFileUrl": fdi.downloadURL
				};

				NSDictionary *JSONDic = @{
                    @"mErrorType": @2,
                    @"mErrorMessage": @"Not enough space.",
                    @"mFiles": @[JSONFileDic]
				};

				[self sendMessageToUnity:"ErrorMessage" JSONDataDictionary:JSONDic];
			}

			[[NSOperationQueue mainQueue] addOperationWithBlock:^{
				// Calculate the progress.
				fdi.downloadProgress = (int)ceil(((double)totalBytesWritten / (double)totalBytesExpectedToWrite) * 100);

				NSLog(@"mobiledownloadmanager - Downloadnumber: %i, progress: %i", (int)index, fdi.downloadProgress);

				NSInteger downloadState = [self getDownloadState:downloadTask.state];

				NSDictionary *JSONmFile = @{
                    @"mFilePath": fdi.downloadPath,
                    @"mFileUrl": fdi.downloadURL
				};

				NSDictionary *JSONDic = @{
                    @"mProgressType": @(downloadState),
                    @"mProgress": @(fdi.downloadProgress),
                    @"mFile": JSONmFile,
                    @"mGroupSize": @(self.fileDownloads.count),
                    @"mGroupPosition": @(index + 1)
				};

				[self sendMessageToUnity:"ProgressMessage" JSONDataDictionary:JSONDic];
			}];
		}
	}
}

- (void)URLSessionDidFinishEventsForBackgroundURLSession:(NSURLSession *)session
{
	// Check if all download tasks have been finished.
	[self.session getTasksWithCompletionHandler:^(NSArray *dataTasks, NSArray *uploadTasks, NSArray *downloadTasks) {
		if ([downloadTasks count] == 0) {

			if (self.backgroundTransferCompletionHandler != nil) {
				void(^completionHandler)() = self.backgroundTransferCompletionHandler;

				self.backgroundTransferCompletionHandler = nil;

				[[NSOperationQueue mainQueue] addOperationWithBlock:^{
					// Call the completion handler to tell the system that there are no other background transfers.
					completionHandler();
				}];
			}
		}
	}];
}

- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didResumeAtOffset:(int64_t)fileOffset expectedTotalBytes:(int64_t)expectedTotalBytes
{
    if (downloadTask.error != nil) {
        NSError *err = [downloadTask.error.userInfo valueForKey:NSURLSessionDownloadTaskResumeData];
        NSLog(@"Error found With: %@", err.localizedDescription);
    }
}

#pragma mark - Helpers

//- (NSString)

- (NSUInteger)getFileDownloadInfoIndexWithTaskIdentifier:(unsigned long)taskIdentifier
{
	for (NSUInteger i = 0; i < self.fileDownloads.count; i++) {
		FileDownloadInfo *fdi = self.fileDownloads[i];
		if (fdi.taskIdentifier == taskIdentifier) {
			return i;
		}
	}

	return (NSUInteger) -1;
}

- (float)diskSpace
{
	NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	struct statfs tStats;
	statfs([[paths lastObject] UTF8String], &tStats);

	float free_space = (float)(tStats.f_bavail * tStats.f_bsize);
	return free_space;
}

-(void)getDownloadedFilesLength:(NSArray *)array
{
    
    NSString *sizeMessage = [NSString stringWithFormat:@"Downloads: %lu", (unsigned long)array.count];
    
    NSInteger sizeState = 0;
    
    if (array.count != 0) {
        sizeState = 1;
    }
    
    //File is already downloaded.
    NSLog(@"mobiledownloadmanager - DownloadedSize = : %d", (int)sizeState);
    
    NSDictionary *JSONDic = @{
        @"mFilePath": @"nil",
        @"mFileUrl": @"nil"
    };
    
    NSDictionary *JSONData = @{
        @"mSizeType": @(sizeState),
        @"mSizeMessage": sizeMessage,
        @"mFiles":@[JSONDic]
    };
    
    [self sendMessageToUnity:"SizeMessage" JSONDataDictionary: JSONData];
}

- (void)sendLocalNotification:(NSString *)message
{
    UILocalNotification *notification = [[UILocalNotification alloc] init];
    notification.alertBody = message;
    notification.fireDate = [[NSDate alloc] initWithTimeIntervalSinceNow:0];
    notification.timeZone = [NSTimeZone defaultTimeZone];
    notification.repeatInterval = 0;
    [[UIApplication sharedApplication] presentLocalNotificationNow:notification];
}

-(NSInteger)getDownloadState:(NSURLSessionTaskState)state
{
    switch(state) {
    case NSURLSessionTaskStateRunning:
        return 0;
    case NSURLSessionTaskStateSuspended:
        return 1;
    case NSURLSessionTaskStateCanceling:
        return 2;
    case NSURLSessionTaskStateCompleted:
        return 3;
    }
    
    NSAssert(NO, @"Fatal Error occured!");
    return 2;
}

-(NSInteger)getErrorMessage:(NSError *)error
{
    if ([error.domain isEqual:NSURLErrorDomain]) {
        
        switch (error.code) {
        case NSURLErrorNotConnectedToInternet:
            return 1;
        case NSURLErrorCannotWriteToFile:
            return 2;
        case NSURLErrorCancelled:
            return 3;
        case NSURLErrorNetworkConnectionLost:
            return 5;
        case NSURLErrorTimedOut:
            return 7;
        }
    }
    
    return -99;
}

- (void)sendMessageToUnity:(const char *)methodName JSONDataDictionary:(NSDictionary *)jsonDataDictionary {
    
    const char *gameObj = "MobileDownloadManager";
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDataDictionary options:NSJSONWritingPrettyPrinted error:&error];
    
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    UnitySendMessage (gameObj, methodName, [jsonString UTF8String]);
}

/**
 * BUGFIX METHODS:
 *
 * The 'correctRequestData', 'correctResumData' & 'getResumDictionary' methods 
 * are being used to fix a bug regarding the Resume function in NSURLSession >= iOS 10.0
 * The bug is still not fixed in iOS 10.2.
 * http://stackoverflow.com/questions/39346231/resume-nsurlsession-on-ios10/39347461#39347461
 **/

- (NSData *)correctRequestData:(NSData *)data
{
    if (!data) {
        return nil;
    }
    if ([NSKeyedUnarchiver unarchiveObjectWithData:data]) {
        return data;
    }
    
    NSMutableDictionary *archive = [NSPropertyListSerialization propertyListWithData:data options:NSPropertyListMutableContainersAndLeaves format:nil error:nil];
    if (!archive) {
        return nil;
    }
    int k = 0;
    while ([[archive[@"$objects"] objectAtIndex:1] objectForKey:[NSString stringWithFormat:@"$%d", k]]) {
        k += 1;
    }
    
    int i = 0;
    while ([[archive[@"$objects"] objectAtIndex:1] objectForKey:[NSString stringWithFormat:@"__nsurlrequest_proto_prop_obj_%d", i]]) {
        NSMutableArray *arr = archive[@"$objects"];
        NSMutableDictionary *dic = [arr objectAtIndex:1];
        id obj;
        if (dic) {
            obj = [dic objectForKey:[NSString stringWithFormat:@"__nsurlrequest_proto_prop_obj_%d", i]];
            if (obj) {
                [dic setObject:obj forKey:[NSString stringWithFormat:@"$%d",i + k]];
                [dic removeObjectForKey:[NSString stringWithFormat:@"__nsurlrequest_proto_prop_obj_%d", i]];
                arr[1] = dic;
                archive[@"$objects"] = arr;
            }
        }
        i += 1;
    }
    
    if ([[archive[@"$objects"] objectAtIndex:1] objectForKey:@"__nsurlrequest_proto_props"]) {
        NSMutableArray *arr = archive[@"$objects"];
        NSMutableDictionary *dic = [arr objectAtIndex:1];
        if (dic) {
            id obj;
            obj = [dic objectForKey:@"__nsurlrequest_proto_props"];
            if (obj) {
                [dic setObject:obj forKey:[NSString stringWithFormat:@"$%d",i + k]];
                [dic removeObjectForKey:@"__nsurlrequest_proto_props"];
                arr[1] = dic;
                archive[@"$objects"] = arr;
            }
        }
    }
    
    id obj = [archive[@"$top"] objectForKey:@"NSKeyedArchiveRootObjectKey"];
    if (obj) {
        [archive[@"$top"] setObject:obj forKey:NSKeyedArchiveRootObjectKey];
        [archive[@"$top"] removeObjectForKey:@"NSKeyedArchiveRootObjectKey"];
    }
    NSData *result = [NSPropertyListSerialization dataWithPropertyList:archive format:NSPropertyListBinaryFormat_v1_0 options:0 error:nil];
    return result;
}

- (NSMutableDictionary *)getResumDictionary:(NSData *)data
{
    NSMutableDictionary *iresumeDictionary;
    if ([[NSProcessInfo processInfo] operatingSystemVersion].majorVersion >= 10) {
        NSMutableDictionary *root;
        NSKeyedUnarchiver *keyedUnarchiver = [[NSKeyedUnarchiver alloc] initForReadingWithData:data];
        NSError *error = nil;
        root = [keyedUnarchiver decodeTopLevelObjectForKey:@"NSKeyedArchiveRootObjectKey" error:&error];
        if (!root) {
            root = [keyedUnarchiver decodeTopLevelObjectForKey:NSKeyedArchiveRootObjectKey error:&error];
        }
        [keyedUnarchiver finishDecoding];
        iresumeDictionary = root;
    }
    
    if (!iresumeDictionary) {
        iresumeDictionary = [NSPropertyListSerialization propertyListWithData:data options:0 format:nil error:nil];
    }
    return iresumeDictionary;
}

static NSString * kResumeCurrentRequest = @"NSURLSessionResumeCurrentRequest";
static NSString * kResumeOriginalRequest = @"NSURLSessionResumeOriginalRequest";

- (NSData *)correctResumData:(NSData *)data
{
    NSMutableDictionary *resumeDictionary = [self getResumDictionary:data];
    if (!data || !resumeDictionary) {
        return nil;
    }
    
    resumeDictionary[kResumeCurrentRequest] = [self correctRequestData:[resumeDictionary objectForKey:kResumeCurrentRequest]];
    resumeDictionary[kResumeOriginalRequest] = [self correctRequestData:[resumeDictionary objectForKey:kResumeOriginalRequest]];
    
    NSData *result = [NSPropertyListSerialization dataWithPropertyList:resumeDictionary format:NSPropertyListXMLFormat_v1_0 options:0 error:nil];
    return result;
}

/**
 * END
 **/

@end

#pragma mark - Unity C methods

extern "C"
{
    void configure(bool wifiOnly, int maxConnections, bool notifyUserOnce)
    {
        [mobiledownloadmanager configureWithWifiOnly:wifiOnly maxConnections:maxConnections notifyUserOnce:notifyUserOnce];
    }
    
    void downloadFilesWithMessage(const char* urls[], int urlcount, const char * notificationMessage)
    {
        NSMutableArray *array = [[NSMutableArray alloc] init];
        
        for (NSUInteger i = 0; i < urlcount; ++i) {
            [array addObject:[NSString stringWithUTF8String:urls[i]]];
        }
        
        [mobiledownloadmanager downloadFilesWithURLS:array andMessage:[NSString stringWithUTF8String:notificationMessage]];
    }

	void deleteFile(const char * url)
	{
		[mobiledownloadmanager deleteFileWithName:[NSString stringWithUTF8String:url]];
	}

	void deleteFiles(const char* names[], int namescount)
	{
		NSMutableArray *array = [[NSMutableArray alloc] init];

		for (NSUInteger i = 0; i < namescount; ++i) {
			[array addObject:[NSString stringWithUTF8String:names[i]]];
		}

		[mobiledownloadmanager deleteFileWithNames:array];
	}

	void setAllowCellular(bool allowed)
	{
		[mobiledownloadmanager setAllowCellular:allowed];
	}

	bool allowCellular()
	{
		return [mobiledownloadmanager allowCellular];
	}
    
    void cancelSingleDownload(const char * url)
    {
        [mobiledownloadmanager cancelSingleDownload:[NSString stringWithUTF8String:url]];
    }

	void cancelAllDownloads()
	{
		[mobiledownloadmanager cancelAllDownloads];
	}
    
    void pauseSingleDownload(const char * url)
    {
        [mobiledownloadmanager pauseSingleDownload:[NSString stringWithUTF8String:url]];
    }

	void pauseAllDownloads()
	{
		[mobiledownloadmanager pauseAllDownloads];
	}
    
    void resumeSingleDownload(const char * url)
    {
        [mobiledownloadmanager resumeSingleDownload:[NSString stringWithUTF8String:url]];
    }

	void resumeAllDownloads()
	{
		[mobiledownloadmanager resumeAllDownloads];
	}

	void checkFileExist(const char * url)
	{
		[mobiledownloadmanager checkFileExistWithName:[NSString stringWithUTF8String:url]];
	}

	void checkFilesExist(const char* urls[], int urlcount)
	{
		NSMutableArray *array = [[NSMutableArray alloc] init];

		for (NSUInteger i = 0; i < urlcount; ++i) {
			[array addObject:[NSString stringWithUTF8String:urls[i]]];
		}

		[mobiledownloadmanager checkFilesExistWithNames:array];
	}

    char* currentLocale()
	{
        char* res = (char*)malloc(strlen([[mobiledownloadmanager currentLocale] UTF8String]) + 1);
        strcpy(res, [[mobiledownloadmanager currentLocale] UTF8String]);
        return res;
	}
}
