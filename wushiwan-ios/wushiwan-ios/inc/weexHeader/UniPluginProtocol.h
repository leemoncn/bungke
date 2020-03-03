//
//  UniPluginProtocol.h
//  libWeex
//
//  Created by 4Ndf on 2018/11/30.
//  Copyright © 2018年 DCloud. All rights reserved.
//
#import <UIKit/UIApplication.h>

@protocol UniPluginProtocol <NSObject>
@required
-(void)onCreateUniPlugin;
@optional
- (BOOL)application:(UIApplication *_Nullable)application didFinishLaunchingWithOptions:(NSDictionary *_Nullable)launchOptions;
- (void)application:(UIApplication *_Nullable)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *_Nullable)deviceToken;
- (void)application:(UIApplication *_Nullable)application didFailToRegisterForRemoteNotificationsWithError:(NSError *_Nullable)err;
- (void)application:(UIApplication *_Nullable)application didReceiveRemoteNotification:(NSDictionary *_Nullable)userInfo;
- (void)application:(UIApplication *_Nullable)application didReceiveRemoteNotification:(NSDictionary *_Nullable)userInfo fetchCompletionHandler:(void (^_Nullable)(UIBackgroundFetchResult))completionHandler;
- (void)application:(UIApplication *_Nullable)application didReceiveLocalNotification:(UILocalNotification *_Nullable)notification;
- (BOOL)application:(UIApplication *_Nullable)application handleOpenURL:(NSURL *_Nullable)url;

-(BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> * _Nullable))restorationHandler;

@end
