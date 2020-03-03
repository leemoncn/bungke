/**
 * @Author: limeng
 * @Date: 2019-08-18 21:16
 * @Description:
 */
import { checkUpdate } from "@/service/update"
import store from "@/store"
import { Notify } from "vant"
import { getPropertyById } from "@/service/property"

let currentAppChannel = ""

export function isApp () {
  let u = navigator.userAgent
  return u.indexOf("Html5Plus") > -1
}

export function isInReview () {
  if (!isApp()) {
    return false
  }
  if (currentAppChannel) {
    let obj = JSON.parse(getPropertyById(75).value)
    let channelObj = obj[currentAppChannel]
    let appVersion = plus.runtime.version
    if (channelObj.version === appVersion && channelObj.review) {
      return true
    }
  }
  return false
}

export function getDeviceInfo () {
  if (!isApp()) {
    return null
  }
  let obj = {
    model: plus.device.model,
    vendor: plus.device.vendor,
    os: plus.os
  }
  return JSON.stringify(obj)
}

// 扩展API加载完毕后调用onPlusReady回调函数
document.addEventListener("plusready", onPlusReady, false)

function onPlusReady () {
  plus.runtime.getProperty(plus.runtime.appid, async function (wgtinfo) {
    currentAppChannel = wgtinfo.description
  })
  plus.screen.lockOrientation("portrait")
  document.addEventListener("resume", onAppReume, false)
  plus.key.addEventListener("backbutton", handleBack)
  checkUpdate()
  registerPush()
  // TODO 会多次触发router，检查下
  if (store.getters.isLogin) {
    store.dispatch("refreshUserInfo")
  }
}

function onAppReume () { // 后台切换到前台
  checkUpdate()
  // 刷新user信息
  if (store.getters.isLogin) {
    store.dispatch("refreshUserInfo")
  }
}

let requestPushIdTimes = 0

function initPushId () {
  requestPushIdTimes++
  let clientInfo = plus.push.getClientInfo()
  if (clientInfo) {
    // console.log("client info = ", clientInfo)
    let cid = clientInfo.clientid
    if (cid) {
      store.commit("updatePushId", cid)
    } else {
      if (requestPushIdTimes < 30) {
        setTimeout(initPushId, 1000)
      }
    }
  } else {
    if (requestPushIdTimes < 30) {
      setTimeout(initPushId, 1000)
    }
  }
}

function registerPush () {
  initPushId()
  // 监听系统通知栏消息点击事件
  plus.push.addEventListener("click", function (msg) {
    // console.log("click = ", msg)
    store.commit("addNewMsgCount", 1)
  }, false)
  // 监听接收透传消息事件
  plus.push.addEventListener("receive", function (msg) {
    // console.log("receive = ", msg)
    handlePushModel(msg)
  }, false)
}

function handlePushModel (model) {
  let payloadStr = model.payload
  let payload
  if (model.payload instanceof Object) {
    if (model.payload.data) {
      payload = model.payload.data.payload
    } else {
      payload = model.payload.payload
    }
  } else {
    let jsonObj = JSON.parse(payloadStr)
    if (jsonObj.data) {
      payload = jsonObj.data.payload
    } else {
      payload = jsonObj.payload
    }
  }
  if (payload && payload.length > 0) {
    for (const p of payload) {
      switch (p.type) {
        case "TOAST":// toast消息
          window.$vue.toastMsg(p.msg)
          break
        case "NOTICE":
          Notify({ type: "success", message: p.msg })
          store.commit("addNewMsgCount", 1)
          break
      }
    }
    if (store.getters.isLogin) {
      store.dispatch("refreshUserInfo")
    }
  }
}

let firstBackPressedTime = 0

function handleBack () {
  if (window.$vue.$route.path === "/") {
    let now = new Date().getTime()
    if (!firstBackPressedTime) {
      firstBackPressedTime = now
      plus.nativeUI.toast("再按一次退出应用")
      setTimeout(function () {
        firstBackPressedTime = 0
      }, 2000)
    } else if (now - firstBackPressedTime < 2000) {
      plus.runtime.quit()
    }
  } else {
    // 根据实际需求选择相应的方法
    window.$vue.$router.back()
  }
}

window.addEventListener("message", function (evt) {
  if (evt.data === "landscape") {
    plus.screen.lockOrientation("landscape") // 锁死屏幕方向为横屏
  }
  if (evt.data === "portrait") {
    plus.screen.lockOrientation("portrait") // 锁死屏幕方向为竖屏
  }
})
