import Vue from "vue"
import "normalize.css/normalize.css" // A modern alternative to CSS resets
import App from "./App.vue"
import router from "./router"
import store from "./store"
import "@/icon" // icon
import "@/directive"
import "@/assets/sass/index.scss" // global css
import { httpPost, httpPostWithLoading, httpGet } from "@/util/request"
import ElementUI, {
  Loading
} from "element-ui"
import "element-ui/lib/theme-chalk/index.css"
import _ from "lodash"
import { getPropertyById, getPropertyNameById } from "@/service/property"
import { imgFullPath } from "@/util/img"
import VueLazyload from "vue-lazyload"

Vue.use(ElementUI)
Vue.use(VueLazyload)

Vue.config.productionTip = false
Vue.prototype.httpPost = httpPost
Vue.prototype.httpPostWithLoading = httpPostWithLoading
Vue.prototype.httpGet = httpGet
Vue.prototype._ = _
Vue.prototype.getPropertyById = getPropertyById
Vue.prototype.getPropertyNameById = getPropertyNameById
Vue.prototype.imgFullPath = imgFullPath

let loading = null
Vue.prototype.showLoading = function () {
  loading = Loading.service({ fullscreen: true })
}
Vue.prototype.hideLoading = function () {
  loading.close()
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app")
