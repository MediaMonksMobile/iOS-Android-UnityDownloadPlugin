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

+ (void)configureWithWifiOnly:(bool)status maxConnections:(int)maxConnections notifyUserOnce:(bool)notifyUserOnce;

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

- (void)startDownloadWithURL:(NSString *)url andMessage:(NSString *)message;

- (void)setCellularAllowed:(BOOL)allowed;

@property (nonatomic, copy) void(^backgroundTransferCompletionHandler)();

@end
