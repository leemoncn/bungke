/*
 *------------------------------------------------------------------
 *  pandora/tools/PDRToolSystemEx.h.h
 *  Description:
 *      获取设备信息头文件
 *  DCloud Confidential Proprietary
 *  Copyright (c) Department of Research and Development/Beijing/DCloud.
 *  All Rights Reserved.
 *
 *  Changelog:
 *	number	author	modify date modify record
 *   0       xty     2013-1-10 创建文件
 *------------------------------------------------------------------
 */
#import <CoreGraphics/CoreGraphics.h>
#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import <SystemConfiguration/SystemConfiguration.h>
#import <CoreText/CoreText.h>

#define PT_IsAtLeastiOSVersion(X) ([[[UIDevice currentDevice] systemVersion] compare:X options:NSNumericSearch] != NSOrderedAscending)

#define PT_IsIPad() ([[UIDevice currentDevice] respondsToSelector:@selector(userInterfaceIdiom)] && ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad))

/*
 **@获取系统时间
 */
@interface PTDate : NSObject

+(PTDate*)date;

@property(nonatomic, readonly)NSInteger year;
@property(nonatomic, readonly)SInt8 month;
@property(nonatomic, readonly)SInt8 day;
@property(nonatomic, readonly)SInt8 hour;
@property(nonatomic, readonly)SInt8 minute;
@property(nonatomic, readonly)double sencond;
@property(nonatomic, readonly)double milliseconds;
+(NSDate*)dateWithYear:(NSInteger)year month:(NSInteger)month day:(NSInteger)day;
+(NSDate*)dateWithHour:(NSInteger)hour minute:(NSInteger)minute sencond:(NSInteger)sencond;
@end

/*
 **@采集网络的相关信息，域名为plus.device
 */
typedef NS_ENUM(NSInteger, PTNetType) {
    PTNetTypeUnknow = 0,
    PTNetTypeNone,     // none
    PTNetTypeEthernet, // none
    PTNetTypeWIFI,   // wifi
    PTNetTypeCell2G, // 2G
    PTNetTypeCell3G, // 3G
    PTNetTypeCell4G, // 4G
    PTNetTypeWWAN    // 2g/3g
};

typedef void (^PTNetInfoNetChangeCallback)(PTNetType newNetType, PTNetType oldNetType);

@interface PTNetInfo : NSObject
@property(nonatomic, readonly)PTNetType netType;
+ (instancetype)info;
- (BOOL)startNotifierWithCallback:(SCNetworkReachabilityCallBack)reachabilityCallback;
- (BOOL)startNotifierWithBlock:(PTNetInfoNetChangeCallback)reachabilityCallback;
- (void)stopNotifier;
+ (instancetype)reachabilityForInternetConnection;
@end

/*
 **@采集手机硬件的相关信息，域名为plus.device
 */
@interface PTDeviceInfo : NSObject

//国际移动设备身份码
@property(nonatomic, retain)NSString *IMEI;
//国际移动用户识别码
@property(nonatomic, retain)NSString *IMSI;
//设备型号
@property(nonatomic, retain)NSString *model;
//生产厂商
@property(nonatomic, retain)NSString *vendor;
@property(nonatomic, retain)NSString *UUID;
//移动网络国家类型，Mobile Country Code
@property(nonatomic, retain)NSString *mcc;
//"运营商代号，Mobile Country Code"
@property(nonatomic, retain)NSString *mnc;
@property(nonatomic, retain)NSString *mac;
+(PTDeviceInfo*)deviceInfo;
+ (NSString*)openUUID;
+ (NSString*)uniqueAppInstanceIdentifier;
@end

typedef NS_ENUM(NSInteger, PTSystemVersion) {
    PTSystemVersion5Series = 0,
    PTSystemVersion6Series,
    PTSystemVersion7Series,
    PTSystemVersion8Series,
    PTSystemVersion9Series,
    PTSystemVersion10Series,
    PTSystemVersion11Series,
    PTSystemVersionUnknown
};

typedef NS_ENUM(NSInteger, PTDeviceType) {
    PTDeviceTypeiPhoneSimulator,
    PTDeviceTypeiPhone3G,
    PTDeviceTypeiPhone3GS,
    PTDeviceTypeiPhone4,
    PTDeviceTypeiPhone4s,
    PTDeviceTypeiPhone5,
    PTDeviceTypeiPhone5c,
    PTDeviceTypeiPhone5s,
    PTDeviceTypeiPhone6,
    PTDeviceTypeiPhone6Plus,
    PTDeviceTypeiPhone6s,
    PTDeviceTypeiPhone6sPlus,
    PTDeviceTypeiPhone7,
    PTDeviceTypeiPhone7Plus,
    PTDeviceTypeiPhone8,
    PTDeviceTypeiPhone8Plus,
    PTDeviceTypeiPhoneX,
    PTDeviceTypeiPhoneXR,
    PTDeviceTypeiPhoneXS,
    PTDeviceTypeiPhoneXSMax,
    PTDeviceTypeiPhoneSE,
    PTDeviceTypeiPod3G,
    PTDeviceTypeiPod4G,
    PTDeviceTypeiPod5G,
    PTDeviceTypeiPad5,
    PTDeviceTypeiPad6,
    PTDeviceTypeiPadPro,
    PTDeviceTypeiPadAir2,
    PTDeviceTypeiPadAir,
    PTDeviceTypeNewiPad,
    PTDeviceTypeiPad3,
    PTDeviceTypeiPad2,
    PTDeviceTypeiPad1,
    PTDeviceTypeiPadMini4,
    PTDeviceTypeiPadMini3,
    PTDeviceTypeiPadMini2,
    PTDeviceTypeiPadMini1,
    PTDeviceTypeiAppleTV,
    PTDeviceTypeiUnknown
};

/*
 **@采集手机操作系统的相关信息，域名为plus.os
 */
@interface PTDeviceOSInfo : NSObject

//操作系统语言
@property(nonatomic, retain)NSString *language;
//操作系统版本号
@property(nonatomic, retain)NSString *version;
//操作系统名称
@property(nonatomic, retain)NSString *name;
//操作系统提供商
@property(nonatomic, retain)NSString *vendor;

+(NSString*)deviceUtsname;
+ (NSString*)getPreferredLanguage;
+ (PTDeviceOSInfo*)osInfo;
+ (PTSystemVersion)systemVersion;
+ (PTDeviceType)deviceType;
+ (NSString*)deviceTypeInString;
+ (NSString*)cuntryCode;
+ (BOOL)is7Series;
+ (BOOL)is6Series;
+ (BOOL)is5Series;
+ (BOOL)isIpad;
@end

/*
 **@采集手机自身屏幕的相关分辨率等信息，域名为plus.screen
 */
@interface PTDeviceScreenInfo : NSObject

//屏幕高度
@property(nonatomic, assign)CGFloat resolutionHeight;
//屏幕宽度
@property(nonatomic, assign)CGFloat resolutionWidth;
//X方向上的密度
@property(nonatomic, assign)CGFloat dpiX;
//Y方向上的密度
@property(nonatomic, assign)CGFloat dpiY;
@property(nonatomic, assign)CGFloat scale;
+(PTDeviceScreenInfo*)screenInfo;

@end

/*
 **@采集手机自身屏幕的相关分辨率等信息，域名为plus.screen
 */
@interface PTDeviceDisplayInfo : NSObject

//应用可用区域
@property (nonatomic, assign)CGRect displayRect;
//应用可用高度
@property(nonatomic, assign)CGFloat resolutionHeight;
//应用可用宽度
@property(nonatomic, assign)CGFloat resolutionWidth;

- (CGRect)displayRect;

+(PTDeviceDisplayInfo*)displayInfo;
+(PTDeviceDisplayInfo*)displayInfoWith:(UIInterfaceOrientation)orientation;
@end


@interface PTDevice : NSObject
{
    PTDeviceInfo *_deviceInfo;
    PTDeviceOSInfo *_osInfo;
    PTDeviceScreenInfo *_screenInfo;
    PTDeviceDisplayInfo *_displayInfo;
    PTNetInfo *_netInfo;
}

+(PTDevice*)sharedDevice;
-(void)update;
@property(nonatomic, retain)PTDeviceInfo *deviceInfo;
@property(nonatomic, retain)PTDeviceOSInfo *osInfo;
@property(nonatomic, retain)PTDeviceScreenInfo *screenInfo;
@property(nonatomic, retain)PTDeviceDisplayInfo *displayInfo;
/*@property(nonatomic, retain)PTNetInfo *netInfo;*/
+(long long)getAvailableMemorySize;
+(long long)getUseMemorySize;
@end

@interface NSString(Measure)
- (BOOL)isAlphaNumeric;
- (int)getMeasure:(CGFloat*)aOutValue withStaff:(CGFloat)aStaff;
@end

@interface UIColor(longColor)
-(NSString*)CSSColor:(BOOL)hasAlpha;
+(UIColor*)colorWithLong:(long)colorValue;
+(UIColor*)colorWithCSS:(NSString*)cssColor;
@end

@interface CAMediaTimingFunction(Util)
+(CAMediaTimingFunction*)curveEnum2Obj:(UIViewAnimationCurve)curve;
@end

@interface PTGIF :NSObject
@property(nonatomic, retain, readonly)NSArray *frames;
@property(nonatomic, retain, readonly)NSArray *delayTimes;
+ (instancetype)praseGIFData:(NSData *)data;
+ (instancetype)createGifWithFrames:(NSArray *)f withDelayTimes:(NSArray*)delayTimes;
@end

@interface NSDate (DateFormater)
+ (NSDate*)dateFromString:(NSString*)dateStr;
+ (NSString*)stringFrmeDate:(NSDate*)date;
//根据格式把时间转为字符串（默认使用本地所在时区）
- (NSString *)stringWithFormat:(NSString*)fmt;
@end

//导航图标旋转接口
@interface UIImage(Util)
- (UIImage *)adjustOrientation;
- (UIImage*)adjustOrientationToup;
- (UIImage*)imageRotatedByDegrees:(CGFloat)degrees
                    supportRetina:(BOOL)support
                            scale:(CGFloat)scale;
+ (UIImage*)screenshot:(UIView*)view clipRect:(CGRect)shotRect;
- (UIImage *)scaleToSize:(UIImage *)img size:(CGSize)size;
+ (BOOL)checkImageIsPureWhite:(UIImage*)image;
+ (NSData*)compressImageData:(NSData*)srcData toMaxSize:(long)maxSize;
+ (UIImage*)dcloud_imageWithContentsOfFile:(NSString *)path;
@end

@interface NSString (WBRequest)
- (NSString *)URLDecodeStringEx;
- (NSString *)URLEncodedStringEx;
- (NSString *)URLDecodedStringWithCFStringEncodingEx:(CFStringEncoding)encoding;
- (NSString *)URLEncodedStringWithCFStringEncodingEx:(CFStringEncoding)encoding;
/**
 * 判断字符串中是否有中文，如果有则将中文转码
 */
- (NSString *)URLChineseEncode;
- (NSString *)convertToMD5;
- (BOOL)isWebUrlString;
@end

@interface PTTool : NSObject
+ (BOOL)setSkipBackupAttribute:(BOOL)skip toItemAtURL:(NSURL*)URL;
+ (NSDictionary*)merge:(NSDictionary*)merge to:(NSDictionary*)to;
@end

@interface NSData (AES)
- (NSData *)AESEncryptWithKey:(NSString *)key;
- (NSData *)AESEncryptWithKey128:(NSString *)key;
- (NSData *)AESDecryptWithKey:(NSString *)key;
- (NSData *)AESDecryptWithKey128:(NSString *)key;
+ (NSData *)compressData:(NSData*)uncompressedData;
- (NSData *)compressData:(NSData*)uncompressedData;
@end

typedef NS_ENUM(NSInteger, H5CoreToolDirection) {
    H5CoreToolDirectionDown,
    H5CoreToolDirectionUp,
    H5CoreToolDirectionLeft,
    H5CoreToolDirectionRight,
    H5CoreToolDirectionUnknown
};

@interface H5CoreTool : NSObject
+ (NSString*)dynamicLoadFont:(NSString*)fontFilePath;
+ (NSString*)dynamicLoadFontUseCache:(NSString*)newPath;
+ (H5CoreToolDirection)determineDirection:(CGPoint)translation;
+ (void)getLocationTestAuthentication:(BOOL)testAuthentication withReslutBlock:(void(^)(NSDictionary*, NSError*))block;
@end

@interface H5TextCheck :NSObject
+ (BOOL)isTelephone:(NSString*)value;
+ (BOOL)isEmail:(NSString*)value;
@end

@interface UIFont(H5Tool)
+(CGFloat)piexl2Size:(CGFloat)piexl;
@end

@interface NSArray(DCAdd)
-(BOOL)dc_containsStringCaseInsensitive:(NSString*)testString;
@end


typedef NS_ENUM(NSInteger, PDRCoreAppSSLActive) {
    PDRCoreAppSSLActiveAllow = 0,
    PDRCoreAppSSLActiveWarning,
    PDRCoreAppSSLActiveRefuse
};



#ifdef __cplusplus
extern "C" {
#endif
int PT_Parse_GetMeasurement( NSObject* aMeasure, CGFloat aStaff, CGFloat * aOutMeasureValue );
    CGSize DCT_CGSizeSwap(CGSize);
    CGRect DCT_CGRectEdgeInsets(CGRect, UIEdgeInsets);
#ifdef __cplusplus
}
#endif
