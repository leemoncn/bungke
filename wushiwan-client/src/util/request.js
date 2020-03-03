/**
 * @Author: limeng
 * @Date: 2019-03-31 16:36
 * @Description:
 */

import axios from "axios"
import store from "@/store"
import router from "@/router"
import { Toast } from "vant"
import { TOAST_ERROR_DURATION } from "@/config/define"
import { isApp } from "@/service/plus"

axios.defaults.withCredentials = false
axios.defaults.timeout = 60000
axios.defaults.headers["Content-Type"] = "application/json;charset=UTF-8"// 配置请求头

// request拦截器
axios.interceptors.request.use(
  config => {
    let headers = config.headers
    headers["isApp"] = isApp()// 配置请求头
    headers = Object.assign({ "Authorization": store.getters.userToken }, headers)
    if (config.hasOwnProperty("customHeader")) {
      headers = Object.assign(headers, config.customHeader)
    }
    config.headers = headers
    if (config.showLoading) {
      startLoading()
    }
    return config
  },
  error => {
    console.error("request err", error)
    return Promise.reject(error)
  }
)

function showMsgTip (msg) {
  Toast({
    message: msg,
    duration: TOAST_ERROR_DURATION
  })
}

// response 拦截器
axios.interceptors.response.use(
  response => {
    if (response.config.showLoading) {
      endLoading()
    }
    const res = response.data
    console.log(response.config.url, "\n", res)
    if (res.code !== "SUCCESS") { // 请求未成功
      res.success = false
      switch (res.code) {
        case "LOGIN_FAILED":
          store.commit("userLogout")
          router.push({ name: "ThirdLogin" })
          break
        case "REGIST_ACCOUNT_EXIST":
          break
        default:
          showMsgTip(res.msg)
      }
    } else { // 请求成功
      res.success = true
    }
    return res
  }, error => {
    if (error.config) {
      if (error.config.showLoading) {
        endLoading()
      }
      console.error("response err =", error.response)
    } else {
      console.error("response err =", error)
    }
    showMsgTip(error.message)
    return Promise.reject(error)
  }
)

let needLoadingRequestCount = 0

function startLoading () {
  if (needLoadingRequestCount === 0) {
    let options = {
      forbidClick: true,
      duration: 0,
      message: "加载中..."
    }
    Toast.loading(options)
  }
  needLoadingRequestCount++
}

function endLoading () {
  // console.log("endloading")
  if (needLoadingRequestCount <= 0) {
    needLoadingRequestCount = 0
    return
  }
  needLoadingRequestCount--
  if (needLoadingRequestCount <= 0) {
    Toast.clear()
    needLoadingRequestCount = 0
  }
}

export function httpPost (url, data, config = { showLoading: false }) {
  data = data || {}
  return axios.post(url, data, config).then((result) => {
    return result
  }).catch((err) => {
    console.error("catch err = ", err)
    return { success: false }
  })
}

export function httpPostFormDataWithLoading (url, data, config = {
  customHeader: { "Content-Type": "multipart/form-data;charset=UTF-8" },
  showLoading: true
}) {
  return httpPost(url, data, config)
}

export function httpPostWithLoading (url, data) {
  return httpPost(url, data, { showLoading: true })
}

export function httpGet (url, params, config = { showLoading: false }) {
  const axiosConfig = Object.assign({}, { params: params || null })
  return axios.get(url, Object.assign(axiosConfig, config))
}
