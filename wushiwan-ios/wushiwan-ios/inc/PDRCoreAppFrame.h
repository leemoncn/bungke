//
//  PDR_Manager_Feature.h
//  Pandora
//
//  Created by Mac Pro_C on 12-12-25.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "PDRCore.h"
#import "PDRNView.h"

@class PDRCoreAppFrame;
@class PDRCoreApp;
@class PDRCoreAppWindow;
@class PGMethod;
@class PDRCoreAppFrameFeature;
@class H5WEWebEngine;
NS_ASSUME_NONNULL_BEGIN
///页面加载完成通知
extern NSString *const PDRCoreAppFrameDidLoadNotificationKey;
///页面关闭通知
extern NSString *const PDRCoreAppFrameDidCloseNotificationKey;
///页面将要加载通知
extern NSString *const PDRCoreAppFrameWillLoadNotificationKey;
///页面开始加载通知
extern NSString *const PDRCoreAppFrameStartLoadNotificationKey;
///页面加载失败通知
extern NSString *const PDRCoreAppFrameLoadFailedNotificationKey;
///页面标题变化通知
extern NSString *const PDRCoreAppFrameTitleUpdaedNotificationKey;


/// H5+应用页面
@interface PDRCoreAppFrame : PDRNView
/**
 @brief 创建runtime页面
 @param viewName 页面标示
 @param pagePath 页面地址 支持http:// file:// 本地地址
 @param frame 页面位置
 @return PDRCoreAppFrame* 
 */
- (PDRCoreAppFrame*)initWithName:(NSString*)viewName
                         loadURL:(NSString*)pagePath
                           frame:(CGRect)frame;

- (PDRCoreAppFrame*)initWithName:(NSString*)viewName
                         loadURL:(NSString*)pagePath
                           frame:(CGRect)frame
                  withEngineName:(NSString*__nullable)engineName;

/// 页面名字用作plus.webview.findViewById()中的id
//@property(nonatomic, copy)NSString *frameName; @see//@property(nonatomic, copy)NSString *viewName;
/// HTML CSS渲染View
@property(nonatomic, readonly, nullable)H5WEWebEngine *webEngine;
/// 页面地址
@property(nonatomic, copy, nullable)NSString* currenLocationHref;
@property(nonatomic, copy, nullable)NSString* baseURL;
/**
 @brief 在当前页面同步执行Javascript
 @param js javasrcipt 脚本
 @return NSString* 执行结果
 */
- (NSString*)stringByEvaluatingJavaScriptFromString:(NSString*)js;
- (void)evaluateJavaScript:(NSString*)javaScriptString completionHandler:(void (^__nullable)(id, NSError* ))completionHandler;
/// @brief 关闭页面中的键盘
- (void)dismissKeyboard;
/// @brief 触发document事件 document.addEventListener(evtName,function(e){})
- (void)dispatchDocumentEvent:(NSString*)evtName;
- (void)dispatchForgroundEvent:(NSString*)actType;
- (void)dispatchDocumentEvent:(NSString *)evtName withData:(NSDictionary*__nullable)data;

@end
NS_ASSUME_NONNULL_END
