import Vue from "vue"
import App from "./App.vue"
import router from "./router"
import store from "./store"
import "amfe-flexible"
import Navigation from "vue-navigation"
import "@/assets/less/common.less"
import "@/assets/less/vant.less"
import "vant/lib/icon/local.css"
import "@/assets/less/normalize.less"
import { httpPost, httpPostWithLoading, httpGet, httpPostFormDataWithLoading } from "@/util/request"
import { getPropertyById, getPropertyListByType } from "@/service/property"
import { imgFullPath } from "@/util/img"
import { TOAST_DURATION } from "@/config/define"
import SvgIcon from "vue-svg-icon/Icon.vue"
import { isApp, isInReview } from "@/service/plus"
import {
  NavBar,
  Field,
  Toast,
  Button,
  Cell,
  CellGroup,
  Loading,
  Tabbar,
  TabbarItem,
  Picker,
  Popup,
  Swipe,
  SwipeItem,
  DatetimePicker,
  Collapse,
  CollapseItem,
  Icon,
  Uploader,
  Search,
  List,
  PullRefresh,
  Tag,
  Dialog,
  ImagePreview,
  Divider,
  Panel,
  Step,
  Steps,
  Lazyload,
  NoticeBar,
  ActionSheet,
  Notify
} from "vant"
import { toastMsg, toastShortMsg } from "@/service/function"
import VueAwesomeSwiper from "vue-awesome-swiper"
import "swiper/dist/css/swiper.css"
import VConsole from "vconsole"
import { isWechat } from "@/service/device"

Vue.use(Tag)
Vue.use(PullRefresh)
Vue.use(List)
Vue.use(Search)
Vue.use(Uploader)
Vue.use(Icon)
Vue.use(Collapse).use(CollapseItem)
Vue.use(DatetimePicker)
Vue.use(Swipe).use(SwipeItem)
Vue.use(Popup)
Vue.use(Picker)
Vue.use(Tabbar).use(TabbarItem)
Vue.use(Loading)
Vue.use(Cell).use(CellGroup)
Vue.use(Button)
Vue.use(Field)
Vue.use(NavBar)
Vue.use(Toast)
Vue.use(Navigation, { router, store })
Vue.use(Dialog)
Vue.use(ImagePreview)
Vue.use(Divider)
Vue.use(Panel)
Vue.use(Step).use(Steps)
Vue.use(ActionSheet)
Vue.use(Notify)
Vue.use(Lazyload, {
  error: require("@/assets/img/banner_default.png"),
  loading: require("@/assets/img/banner_default.png")
})
Vue.component("icon", SvgIcon)

Vue.use(VueAwesomeSwiper)

Vue.use(NoticeBar)

Vue.config.productionTip = false
Vue.prototype.httpPost = httpPost
Vue.prototype.httpPostWithLoading = httpPostWithLoading
Vue.prototype.httpGet = httpGet
Vue.prototype.httpPostFormDataWithLoading = httpPostFormDataWithLoading
Vue.prototype.getPropertyById = getPropertyById
Vue.prototype.getPropertyListByType = getPropertyListByType
Vue.prototype.showLoading = function (msg) {
  Toast.loading({
    forbidClick: true,
    duration: 0,
    message: msg || "加载中..."
  })
}
Vue.prototype.hideLoading = function () {
  Toast.clear()
}
Vue.prototype.imgFullPath = imgFullPath
Vue.prototype.toastFail = function (msg) {
  Toast.fail({ message: msg, duration: TOAST_DURATION })
}
Vue.prototype.toastSuccess = function (msg) {
  Toast.success({ message: msg, duration: TOAST_DURATION })
}
Vue.prototype.toastMsg = toastMsg
Vue.prototype.toastShortMsg = toastShortMsg
Vue.prototype.px2rem = function (px) {
  return px / 37.5 // 如果要改这个37.5，需要同步修改vue.config.js里面的px2rem插件
}
Vue.prototype.isApp = isApp
Vue.prototype.isInReview = isInReview
Vue.prototype.isWechat = isWechat

Vue.prototype.openUrl = function (url) {
  // TODO 这里是否可以用plus.runtime.openWeb(url)或者其他的api，打开一个内置的新的浏览器？
  plus.runtime.openURL(url, function () {
    alert("打开网址失败")
  })
}

Vue.prototype.defaultHeadImg = require("@/assets/img/head.png")

// if (window.location.protocol === "file:" || window.location.port === "3000") {
//   let cordovaScript = document.createElement("script")
//   cordovaScript.setAttribute("type", "text/javascript")
//   cordovaScript.setAttribute("src", "cordova.js")
//   document.body.appendChild(cordovaScript)
// }

/**
 * JS 时间格式化参数
 * 参数：格式化字符串如：'yyyy-MM-dd HH:mm:ss'
 * 结果：如2017-09-15 10:09:00
 */
// eslint-disable-next-line
Date.prototype.format = function (fmt = "yyyy-MM-dd HH:mm:ss") {
  let o = {
    "M+": this.getMonth() + 1,
    "d+": this.getDate(),
    "H+": this.getHours(),
    "m+": this.getMinutes(),
    "s+": this.getSeconds(),
    "q+": Math.floor((this.getMonth() + 3) / 3),
    "S": this.getMilliseconds()
  }
  let year = this.getFullYear().toString()
  year = year.length >= 4 ? year : "0000".substr(0, 4 - year.length) + year

  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (year + "").substr(4 - RegExp.$1.length))
  for (let k in o) {
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)))
  }
  return fmt
}

if (!process.env.VUE_APP_DIST) {
  let vConsole = new VConsole() // 初始化
  console.log("vCousole 初始化")
}

// eslint-disable-next-line
const _hmt = _hmt || []
// eslint-disable-next-line
window._hmt = _hmt; // 必须把_hmt挂载到window下，否则找不到
(function () {
  const hm = document.createElement("script")
  hm.src = "https://hm.baidu.com/hm.js?889f6372c589e5c51e0c2902d368b38e"
  const s = document.getElementsByTagName("script")[0]
  s.parentNode.insertBefore(hm, s)
}())

window.$vue = new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app")
