/**
 * @Author: limeng
 * @Date: 2019-12-05 21:10
 * @Description:
 */

export function getDeviceType () {
  let ua = navigator.userAgent
  let isQB = /(?:MQQBrowser|QQ)/.test(ua)
  let isWindowsPhone = /(?:Windows Phone)/.test(ua)
  let isSymbian = /(?:SymbianOS)/.test(ua) || isWindowsPhone
  let isAndroid = /(?:Android)/.test(ua)
  let isFireFox = /(?:Firefox)/.test(ua)
  let isChrome = /(?:Chrome|CriOS)/.test(ua)
  let isIpad = /(?:iPad|PlayBook)/.test(ua)
  let isIos = /(?:ipod|iphone|ipad)/.test(ua)
  let isTablet = /(?:iPad|PlayBook)/.test(ua) || (isFireFox && /(?:Tablet)/.test(ua))
  let isSafari = /(?:Safari)/.test(ua)
  let isPhone = /(?:iPhone)/.test(ua) && !isTablet
  let isOpen = /(?:Opera Mini)/.test(ua)
  let isUC = /(?:UCWEB|UCBrowser)/.test(ua)
  let isPc = !isPhone && !isAndroid && !isSymbian
  if (isAndroid) {
    return 19
  }
  if (isIos) {
    return 20
  }
  return null
}

export function isWechat () {
  let ua = window.navigator.userAgent.toLowerCase()
  let arr = ua.match(/MicroMessenger/i)
  return arr && arr.includes("micromessenger")
}

export function isAndroid () {
  return getDeviceType() === 19
}

export function isIos () {
  return getDeviceType() === 20
}
