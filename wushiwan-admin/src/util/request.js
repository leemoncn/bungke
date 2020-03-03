/**
 * @Author: limeng
 * @Date: 2019-03-31 16:36
 * @Description:
 */

import axios from "axios"
import store from "@/store"
import router from "@/router"
import { Message, Loading } from "element-ui"

axios.defaults.withCredentials = true
axios.defaults.headers["Content-Type"] = "application/json;charset=UTF-8"// 配置请求头
axios.defaults.timeout = 60000

// request拦截器
axios.interceptors.request.use(
  config => {
    console.log("request url = ", config.url, "\n", config.data)
    let headers = config.headers
    headers = Object.assign({ "Authorization": store.getters.userToken }, headers)
    config.headers = headers
    if (config.method === "post") {
      config.data = JSON.stringify(config.data)
    }
    if (config.showLoading) {
      startLoading(config.target)
    }
    return config
  },
  error => {
    console.error("request err", error)
    return Promise.reject(error)
  }
)

function showMsgTip (msg, type) {
  type = type || "error"
  Message({
    message: msg,
    type: type,
    duration: 5 * 1000
  })
}

// response 拦截器
axios.interceptors.response.use(
  response => {
    if (response.config.showLoading) {
      endLoading()
    }
    const res = response.data
    console.log("response url = ", response.config.url, "\n", res)
    if (res.code !== "SUCCESS") { // 请求未成功
      res.success = false
      if (res.code === "LOGIN_FAILED") {
        store.commit("userLogout")
        router.push({ name: "Login" })
      }
      if (res && res.msg) {
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
    // showMsgTip(error.message)
    return Promise.reject(error)
  }
)

let loading
let needLoadingRequestCount = 0

function startLoading (target) {
  if (needLoadingRequestCount === 0) {
    const options = target ? { target: target } : null
    loading = Loading.service(options)
  }
  needLoadingRequestCount++
}

function endLoading () {
  if (needLoadingRequestCount <= 0) {
    needLoadingRequestCount = 0
    return
  }
  needLoadingRequestCount--
  if (needLoadingRequestCount <= 0) {
    loading.close()
    needLoadingRequestCount = 0
  }
}

export function httpPost (url, data, config = { showLoading: false, target: "" }) {
  data = data || {}
  return axios.post(url, data, config).then((result) => {
    return result
  }).catch((err) => {
    console.error("catch err = ", err)
    return { success: false }
  })
}

export function httpPostWithLoading (url, data, target = ".app-main") {
  return httpPost(url, data, { showLoading: true, target: target })
}

export function httpGet (url, params, config = { showLoading: false, target: "" }) {
  const axiosConfig = Object.assign({}, { params: params || null })
  return axios.get(url, Object.assign(axiosConfig, config))
}
