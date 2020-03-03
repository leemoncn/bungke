package com.leemon.wushiwan.pay.alipay;

import lombok.Data;

/**
 * @description:
 * @author: leemon
 * @create: 2019-07-09 10:45
 **/
public class AlipayPayConfig {
	public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
	//参数返回格式，只支持json
	public static final String FORMAT = "json";
	//请求和签名使用的字符编码格式，支持GBK和UTF-8
	public static final String CHARSET = "UTF-8";
	//商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
	public static final String SIGN_TYPE = "RSA2";

	public static String appId = "2019070665750833";
	//开发者应用私钥，由开发者自己生成
	public static String appPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDEX/74CPO9CK6w\n" +
			"dNepQ0J1bwXW8AlgFxeh7d/z2hRHABcCaA2imzxgG0cXdk1lLTjNX89hImnggG6K\n" +
			"eLphwF079+d1NquHVGaY0QzhfIBV5kXn29yjNNWfUVtSfo7KQ6PUUVXMbot3Hmrr\n" +
			"QCpFGmmKUmHWSmCmqnXeW/4cNw9uDLR9EBCVl9iHKoY+ybIulj1XbL64n3mGXThg\n" +
			"xI3SjyQyS+g0cc/G1cWjzmQMTYrflcupci0Nu60cqqFQFGpPr88r9Jhb/7CgfYF1\n" +
			"fDnvHkKp1O8QeT7L0LBDmSHh7+1ik9ljCWJ9EcPP2QjGDOpGQRahhpcZBCtaMA9U\n" +
			"UREV1e73AgMBAAECggEAQn12aSkuzfyYmWqWctsxL23t/j68DesDt3JBd+GJKBkh\n" +
			"sVDl/P6uzKOvyIPn5LARIYilQPBUBQ23X+9MvDhT2TAxEID4RxQOwQNXZymukmcr\n" +
			"mDvbUu9avVzgnOf5FQcd+4+mi1eKpUSJUyogpdExlwIZ2mLIouXojud6/Lav5MSU\n" +
			"qgyx4BWKud0Ob6tjpJv+rZvLzRbOVteji2w7GIXdDV4Go740u5PJz23wSxsBW0mW\n" +
			"R15nOtJqb1H87yAp85c1XVZ7JYB5JxyJ63DbD5r8e9AxYM2sgEGSpuv5X8f0ejlM\n" +
			"nZcJThP9UA1ziYwPPaAaiRAYoCOsLmn679icLL18EQKBgQDhRVYJAZ+x6iHhToFH\n" +
			"PWrfzZcdYo08xWURc0eY7i9ZqW3D7cb35BelFWrz6kKyYSiCQTJxfxnbi1PQwjVy\n" +
			"CHHZW8l//P/hJsSbM1gcJOfVcNguNBWxbolvH14tppuj1bYLKQtR03nBQwDfWdv7\n" +
			"PJiw6nLlDSfVRli24wQj9Gz5XQKBgQDfKZcNFWUj94HeZ/1v21Kw1pJDNZmR2k3j\n" +
			"eMMYDB5h/2ge8uC8xuMTKNhU0zVdXtn9pWLAUbjmkU+osYelv1N0Ik/Gwq5UnTZ5\n" +
			"86jIb5Rbj+uYlhJpL514MGfl0EyZ0hzJUx+ag3YlKO1wtUT5uXBfhJul3F9AV2AW\n" +
			"Fdewl9+AYwKBgQDVhaoJ+V8L/VfFvmqtQtrVbYKJ2Mr0ykmFI9a+by20d/LR3sRU\n" +
			"XP/IeyeYMPitKYiDBhcxGv9AaKy2WA7emoCHPQgeZ4mGOP/r99vROW4Bkudly/qv\n" +
			"5o0gJgRXku89eKkHpQzUr3VdqQvfqMXpOkOklWT4FOMntXkLSNy408qjBQKBgD85\n" +
			"IjEzuYa6DVH8NiHligTNlXxzCAH5Tg2+3c7HSChNWqtB9geldRmtfbRybfD4+VtR\n" +
			"6qs/u9mem2nYeeysSrFhMybFauxP6eoc7Nl0OxzBpmaBRdAFDGDLnfPO0pTVoRAs\n" +
			"e4yj5+nZMYO350W0krCM+ifASkAJQRCguY2vSUOvAoGBAKYpiJEW40e0GFIZk1MY\n" +
			"IpSUN33lfVeoqF/XIHCKyKSDIZ5H1qInoGh5T2Q3zxeN2qHwZBfM2qf+Jnp480DQ\n" +
			"2DdG80rcLoopV16aYytvjQ21ISeb61n9dnunCqBGiLnkj2mKqV0gS4elkcxRis41\n" +
			"liwsfx0e2owabjaYEWUe11EP";
	//支付宝公钥，由支付宝生成
	public static String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArYqyw8OC4Ugt8Fi2pHPwUY1z7MrXBhm0j/JpHoF34KcAqX9vXWfsmju/YnIPelvyxHKpfayvFN5+Z4sInRC0D8U44bOLC+UK1vduVWPrGryHCx/BWcQrKCKFHJ8sWTFapCdcYh0zIE3wj7tU02xHmLhPs54e48q6ktNiA8xCSKiNxkvBGo6kXp2TXjoxLv0hxBTyga9nvpRytEq8JIETDF1sHp46O+k5lj5oFPnSE/Q4mROO/aShFgPSpYEkbU+pYFDwbEgk0BMTnKKt7yxt4R5M479PAra3RtZiHzhvWDBn20dpH+Geghp763WT8Fw/5mQhSLUE3oL9iardfNBJ1wIDAQAB";
}
