//
//  PDRCoreDefs.h
//  PDRCore
//
//  Created by X on 14-2-11.
//  Copyright (c) 2014年 io.dcloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

UIKIT_EXTERN NSString *const PDRCoreOpenUrlNotification;
UIKIT_EXTERN NSString *const PDRCoreRevDeviceToken;
UIKIT_EXTERN NSString *const PDRCoreRegRemoteNotificationsError;

#if __has_feature(objc_arc)
#define H5_AUTORELEASE(exp) exp
#define H5_RELEASE(exp) exp
#define H5_RETAIN(exp) exp
#else
#define H5_AUTORELEASE(exp) [exp autorelease]
#define H5_RELEASE(exp) [exp release]
#define H5_RETAIN(exp) [exp retain]
#endif

#ifndef H5_STRONG
#if __has_feature(objc_arc)
#define H5_STRONG strong
#else
#define H5_STRONG retain
#endif
#endif

#ifndef H5_WEAK
#if __has_feature(objc_arc_weak)
#define H5_WEAK weak
#elif __has_feature(objc_arc)
#define H5_WEAK unsafe_unretained
#else
#define H5_WEAK assign
#endif
#endif

#if DEBUG
#define H5CORE_LOG NSLog
#else
#define H5CORE_LOG
#endif

#define kPDRCoreAppWindowAnimationDefaltDuration .3f

typedef NS_ENUM(NSInteger, PDRCoreRunMode) {
    /**
     正常启动runtime
     使用该方法启动runtime具有全部功能
     包括具有应用管理、窗口管理、插件管理、权限管理、资源管理等功能
     */
    PDRCoreRunModeNormal = 0,
    /**
     使用该方法启动的runtime不具有应用管理窗口管理功能
     当需要显示页面时,需要自己创建PDRCoreAppFrame
     */
    PDRCoreRunModeWebviewClient = 1,
    /**
     使用该方法启动的runtime具有正常启动所有功能但不包含启动图片
     应用升级、应用copy运行逻辑，该启动会自动启动app管理器但
     不会自动创建app，需要自己创建app
     */
    PDRCoreRunModeAppClient
};

/// 系统事件类型定义
typedef NS_ENUM(NSInteger, PDRCoreSysEvent) {
    /// 网络状态改变事件 Reserved
    PDRCoreSysEventNetChange = 0,
    /// 程序进入后台事件
    PDRCoreSysEventEnterBackground = 1,
    /// 程序进入前台事件
    PDRCoreSysEventEnterForeGround = 2,
    /// 打开URL事件
    PDRCoreSysEventOpenURL = 3,
    ///通用链接
    PDRCoreSysEventContinueUserActivity=18,
    /// 本地自定义消息提醒
    PDRCoreSysEventRevLocalNotification = 4,
    /// APNS事件
    PDRCoreSysEventRevRemoteNotification = 5,
    /// 获取到APNS DeviceToken事件
    PDRCoreSysEventRevDeviceToken = 6,
    /// 获取到APNS错误
    PDRCoreSysEventRegRemoteNotificationsError = 7,
    /// 低内存警告
    PDRCoreSysEventReceiveMemoryWarning = 8,
    /// 屏幕旋转事件 Reserved
    PDRCoreSysEventInterfaceOrientation = 9,
    /// 按键事件
    PDRCoreSysEventKeyEvent = 10,
    /// 全屏
    PDRCoreSysEventEnterFullScreen = 11,
    /// 点击了shortcut Reserved
    PDRCoreSysEventPeekQuickAction = 12,
    /// 应用暂停，被其他应用覆盖
    PDRCoreSysEventResignActive = 13,
    /// 应用被激活，重新处于活动状态
    PDRCoreSysEventBecomeActive = 14,
    /// 系统主动触发状态栏隐藏显示
    PDRCoreSysEventStatusbarChange = 15,
    /// 执行weex post mesasge
    PDRCoreSysEventWeexPostMessage = 16,
    /// 打印weex log
    PDRCoreSysEventWeexOutputLog = 17
};

/// app Statusbar style
typedef enum {
    //深色背景样式（深色底白字，即状态栏前景文字为白色），此时background建议设置为深颜色
    PDRCoreStatusBarStyleLight,
    //浅色背景样色（浅色底黑字，即状态栏前景文字为黑色），此时background建设设置为浅颜色
    PDRCoreStatusBarStyleDark
} PDRCoreStatusBarStyle;

typedef NS_ENUM(NSInteger, PDRCoreStatusBarMode) {
    PDRCoreStatusBarModeNormal = 1,
    PDRCoreStatusBarModeImmersion
};

/// runtime错误码定义
enum {
    PDRCoreSuccess = 0,
    PDRCoreNetObserverCreateError = 1,
    PDRCoreInvalidParamError,
    PDRCoreFileNotExistError,
    PDRCorePandoraApibundleMiss,
    PDRCoreStatusError,
    PDRCoreNetError,
    PDRCoreUnknownError,
    /// 资源管理器启动失败
    PDRCoreErrorResManagerBase = 10000,
    /// 应用管理类错误
    PDRCoreErrorAppManagerBase = 20000,
    PDRCoreErrorDownloadStreamJSON,
    PDRCoreErrorStreamJSONFormat,
    PDRCoreErrorDownloadIndexPage,
    PDRCoreErrorUsercancel,
    PDRCoreInvalidMainpageError,
    /// 无效的mainfest文件
    PDRCoreAppInvalidMainfestError
};

#define kAppManagerOpenAppMaxDefalut 3
#define kWindowAppBarViewHeight (44)
#define kWindowTitleNViewHeight (44)
#define kWindowTitleNView_btn_W (41)
#define kWindowStatusBarViewHeight (20)
#define kWindowNavStatusbarViewHeight (([PTDeviceOSInfo deviceType]==PTDeviceTypeiPhoneX)?44:20)
@interface H5Server : NSObject
+ (NSString*)identifier;
- (void)onCreate;
- (void)onDestroy;
@end
