//
// Created by Peter Gerdes on 14/04/16.
// Copyright (c) 2016 MediaMonks. All rights reserved.
//

#import "FileDownloadInfo.h"


@implementation FileDownloadInfo

-(id)initWithDownloadURL:(NSString *)url
{
	if (self == [super init]) {
		self.downloadURL = url;
		self.downloadPath = @"";
		self.downloadProgress = 0;
		self.isDownloading = NO;
		self.downloadComplete = NO;
		self.taskIdentifier = (unsigned long) -1;
	}

	return self;
}

@end