//
//  mobiledownloadmanager.h
//  mobiledownloadmanager
//
//  Created by Peter Gerdes on 13/04/16.
//  Copyright © 2016 MediaMonks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface mobiledownloadmanager : NSObject

@property (nonatomic, copy) void(^backgroundTransferCompletionHandler)();

/**
 * @discussion  Default initializer
 *
 */
- (instancetype)init;

/**
 * @discussion  Initializer for configuring values given by Unity GameObject
 *
 * @param status           Applying wifi settings
 * @param amount           Applying the max HTTPMaximumConnectionsPerHost
 * @param notifyUserOnce   Applying the setting the local notifications per completed download
 *
 */
- (instancetype)initWithWifiOnly:(BOOL)status maxConnections:(int)amount notifyUserOnce:(BOOL)notifyUserOnce;

/**
 * @dicussion   Checks if the download allready excists in the phones directory and
 * sets a download session on and FileDownloadInfo object with given values.
 * At last it will send a status update to UnityUI.
 *
 * @param url       Applying the given url string
 * @param message   Applying the given notification message string
 */
- (void)startDownloadWithURL:(NSString *)url andMessage:(NSString *)message;

/**
 * @discussion  Checks if the url excists in the mobiles directory and
 * removes the data in the path with the commen created FileDownloadInfo.
 * At last it will send a status update to UnityUI.
 *
 * @param names Applying the givens urls in a array
 */
- (void)deleteFileWithNames:(NSArray <NSString *>*)names;

/**
 * @discussion  Loops through all the download objects and checks if a download session is active.
 * If so, it will compare it with the given @param and pauses and saves the allready
 * downloaded data in the FileDownloadInfo object for later use.
 * NSURLSession delegate will catch this action and updates UnityUI.
 *
 * @param name Applying the given url string
 */
- (void)pauseSingleDownload:(NSString *)name;

/**
 * @dicussion   Loops through all the download objects and checks if a download session is active.
 * If so, it will pauses and saves the allready downloaded data in
 * the FileDownloadInfo object for later use.
 * NSURLSession delegate will catch this action and updates UnityUI.
 */
- (void)pauseAllDownloads;

/**
 * @discussion  Loops through all the download objects and checks if a download is paused.
 * If so, it will compare it with the @param and resumes the download with the saved data.
 * NSURLSession delegate will catch this action and updates UnityUI.
 *
 * @param name  Applying the given url string
 */
- (void)resumeSingleDownload:(NSString *)name;

/**
 * @discussion  Loops through all the download objects and checks if a download is paused.
 * If so, it will resume the download with the saved data.
 * NSURLSession delegate will catch this action and updates UnityUI.
 */
- (void)resumeAllDownloads;

/**
 * @dicussion   Cancel a single download without saving the data.
 * NSURLSession delegate will catch this action and updates UnityUI.
 *
 * @param name  Applying the given url string
 */
- (void)cancelSingleDownload:(NSString *)name;

/**
 * @discussion  Cancel all downloads without saving the data.
 * NSURLSession delegate will catch this action and updates UnityUI.
 */
- (void)cancelAllDownloads;

/**
 * @discussion  Updates the newly created cellularStatus from Unity in UserDefaults
 *
 * @param allowed Applying the given cellular status.
 */
- (void)setCellularAllowed:(BOOL)allowed;

/**
 * @discussion  Checks if the urls excists in the phones directory
 * Depending on the outcome, it will send a update to UnityUI.
 *
 * @param names Applying the given string array.
 */
- (void)checkFilesWithNames:(NSArray <NSString *>*)names;

#pragma mark - Methods callable from Unity

+ (void)configureWithWifiOnly:(BOOL)wifiOnly maxConnections:(int)maxConnections notifyUserOnce:(bool)notifyUserOnce;

+ (void)downloadFilesWithURLS:(NSArray <NSString *> *)urls andMessage:(NSString *)message;

+ (void)deleteFileWithName:(NSString *)name;

+ (void)deleteFileWithNames:(NSArray <NSString *> *)names;

+ (void)setAllowCellular:(BOOL)allowed;

+ (BOOL)allowCellular;

+ (void)cancelSingleDownload:(NSString *)url;

+ (void)cancelAllDownloads;

+ (void)pauseSingleDownload:(NSString *)url;

+ (void)pauseAllDownloads;

+ (void)resumeSingleDownload:(NSString *)url;

+ (void)resumeAllDownloads;

@end
