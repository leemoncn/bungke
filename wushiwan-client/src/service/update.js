/**
 * @Author: limeng
 * @Date: 2019-08-18 20:39
 * @Description:
 */

import { httpPost } from "@/util/request"
import { UPDATE_URL, WGT_HOST } from "@/config/host"
import { isApp } from "@/service/plus"

export function checkUpdate () {
  if (isApp() && process.env.VUE_APP_DIST) {
    plus.runtime.getProperty(plus.runtime.appid, async function (wgtinfo) {
      let version = wgtinfo.version
      let obj = {
        appVersion: plus.runtime.version,
        h5Version: version,
        channel: wgtinfo.description
      }
      const result = await httpPost(UPDATE_URL, obj)
      if (result.success) {
        const wgtUrl = WGT_HOST + "/" + result.data.wgtFilename
        const forceUpdate = result.data.forceUpdate
        downloadWgt(wgtUrl, forceUpdate)
      }
    })
  }
}

function downloadWgt (wgtUrl, forceUpdate) {
  if (forceUpdate) {
    plus.nativeUI.showWaiting("软件更新中，请稍后...")
  }
  plus.downloader.createDownload(wgtUrl, { filename: "_doc/update/" }, function (d, status) {
    if (status === 200) {
      installWgt(d.filename, forceUpdate) // 安装wgt包
    } else { // 下载更新包失败，重启，重新下载
      if (forceUpdate) {
        plus.nativeUI.alert("软件更新失败，即将重试", function () {
          plus.runtime.restart()
        })
      }
    }
  }).start()
}

function installWgt (path, forceUpdate) {
  plus.runtime.install(path, {}, function () {
    if (forceUpdate) {
      plus.nativeUI.closeWaiting()
      plus.nativeUI.alert("软件更新完成", function () {
        plus.runtime.restart()
      })
    }
  }, function (e) { // 安装失败
    if (forceUpdate) {
      plus.nativeUI.closeWaiting()
      plus.nativeUI.alert("软件更新失败，即将重试", function () {
        plus.runtime.restart()
      })
    }
  })
}
