import { isApp } from "@/service/plus"

/**
 * @Author: limeng
 * @Date: 2019-12-03 21:37
 * @Description:
 */

export function wechatLogout () {
  if (isApp()) {
    // 微信授权登录对象
    let weixinAuth = null
    plus.oauth.getServices(function (services) {
      for (const auth of services) {
        if (auth.id === "weixin") {
          weixinAuth = auth
          break
        }
      }
      if (!weixinAuth) {
        return
      }
      weixinAuth.logout(function (e) { // 注销
      }, function (e) {
      })
    }, function (e) {
    })
  }
}
