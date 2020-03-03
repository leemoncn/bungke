/**
 * @Author: leemon
 * @Date: 2019-10-23 13:57
 * @Description:
 */

import {
  Toast
} from "vant"

export function toastMsg (msg) {
  Toast(msg)
}

export function toastShortMsg (msg) {
  Toast({
    message: msg,
    duration: 1000
  })
}

export function goDownloadPage () {
  window.location = "http://client.bungke.com/static/download.html"
}
