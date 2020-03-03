import Vue from "vue"
import Vuex from "vuex"
import createPersistedState from "vuex-persistedstate"
import getters from "@/store/getters"
import user from "@/store/modules/user"
import tabbar from "@/store/modules/tabbar"
import configuration from "@/store/modules/configuration"
import missionAccept from "@/store/modules/missionAccept"
import htmlPage from "@/store/modules/htmlPage"
import setting from "@/store/modules/setting"

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user, tabbar, configuration, missionAccept, htmlPage, setting
  },
  getters,
  plugins: [createPersistedState({
    storage: window.localStorage,
    reducer (val) {
      // if (!isApp()) {//tabbar先不保存，会导致首页banner动画消失
      //   needSaveDisk.tabbar = val.tabbar
      // }
      return {
        user: val.user,
        setting: val.setting,
        htmlPage: val.htmlPage
      }
    }
  })]
})
