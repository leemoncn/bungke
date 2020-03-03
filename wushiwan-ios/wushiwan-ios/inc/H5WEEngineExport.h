//
//  H5WebviewProtolca.h
//  libPDRCore
//
//  Created by DCloud on 16/4/6.
//  Copyright © 2016年 DCloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIkit.h>

NS_ASSUME_NONNULL_BEGIN

@interface H5WEWebEngine :NSObject
@property(nullable, nonatomic, retain)NSString* name;
@property (nullable, nonatomic, readonly, strong) NSURLRequest *request;
@property (nonatomic, readonly, getter=canGoBack) BOOL canGoBack;
@property (nonatomic, readonly, getter=canGoForward) BOOL canGoForward;
@property (nonatomic) BOOL scalesPageToFit;
@property (nonatomic) BOOL allowsInlineMediaPlayback;
@property(nonatomic) UIDataDetectorTypes dataDetectorTypes;
@property (nullable, nonatomic, readonly, copy) NSString *title;
@property (nonatomic) BOOL allowsBackForwardNavigationGestures;

- (id)loadRequest:(NSURLRequest*)request;
- (void)stopLoading;
- (void)reload;
- (void)close;

- (void)goBack;
- (void)goForward;

- (void)evaluateJavaScript:(NSString*)javaScriptString completionHandler:(nullable void (^)(id, NSError*))completionHandler;
@end
NS_ASSUME_NONNULL_END