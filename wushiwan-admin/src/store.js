import Vue from "vue"
import Vuex from "vuex"
import createPersistedState from "vuex-persistedstate"
import getters from "@/store/getters"
import app from "@/store/modules/app"
import user from "@/store/modules/user"
import menu from "@/store/modules/menu"
import tagsView from "@/store/modules/tagsView"
import permission from "@/store/modules/permission"
import iframe from "@/store/modules/iframe"
import property from "@/store/modules/property"

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    app,
    user,
    menu,
    tagsView,
    permission,
    iframe,
    property
  },
  getters,
  plugins: [createPersistedState({
    storage: window.localStorage,
    reducer (val) {
      return {
        user: val.user,
        app: val.app
      }
    }
  })]
})
