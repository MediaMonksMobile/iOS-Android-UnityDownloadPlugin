//
// Created by Peter Gerdes on 14/04/16.
// Copyright (c) 2016 MediaMonks. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface FileDownloadInfo : NSObject

@property (nonatomic, strong) NSString *downloadURL;

@property (nonatomic, strong) NSString *downloadPath;

@property (nonatomic, strong) NSURLSessionDownloadTask *downloadTask;

@property (nonatomic, strong) NSData *taskResumeData;

@property (nonatomic) int downloadProgress;

@property (nonatomic) BOOL isDownloading;

@property (nonatomic) BOOL downloadComplete;

@property (nonatomic) unsigned long taskIdentifier;


-(id)initWithDownloadURL:(NSString *)url;

@end