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

+ (void)configureWithWifiOnly:(bool)status maxConnections:(int)maxConnections;

+ (void)downloadFileWithURL:(NSString *)url;

+ (void)downloadFilesWithURLS:(NSArray <NSString *> *)urls;

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

- (void)startDownloadWithURL:(NSString *)url;

- (void)setCellularAllowed:(BOOL)allowed;

@property (nonatomic, copy) void(^backgroundTransferCompletionHandler)();

@end
