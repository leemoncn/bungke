/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */
import { httpPostWithLoading, httpPost } from "@/util/request"
import { LOGIN_URL, LOGOUT_URL, REFRESH_URL } from "@/config/host"
import store from "@/store"
import { getDeviceInfo, isApp } from "@/service/plus"

const user = {
  state: {
    roles: [],
    token: "",
    user: null,
    partner: null, // TODO 购买partner和agency时应该更新
    agency: null,
    pushId: null
  },
  mutations: {
    userLoginSuccess: (state, data) => {
      state.token = data.token
    },
    userLogout: (state) => {
      state.token = ""
      state.roles = []
      state.user = null
      state.partner = null
      state.agency = null
    },
    updateUser: (state, sysUser) => {
      state.user = sysUser
    },
    updatePartner: (state, partner) => {
      state.partner = partner
    },
    updateAgency: (state, agency) => {
      state.agency = agency
    },
    updatePushId: (state, pushId) => {
      state.pushId = pushId
    }
  },

  actions: {
    // 登录
    async login ({ commit }, userInfo) {
      if (isApp()) {
        userInfo.pushId = store.state.user.pushId
        userInfo.deviceInfo = getDeviceInfo()
      }
      const result = await httpPost(LOGIN_URL, userInfo)
      if (result.success) {
        commit("userLoginSuccess", result.data)
        commit("updateUser", result.data.sysUser)
        commit("updatePartner", result.data.partner)
        commit("updateAgency", result.data.agency)
        store.commit("clearProperty")
        await store.dispatch("loadProperty")
        await store.dispatch("loadMissionRule")
      }
      return result
    },
    // 登出
    async logout ({ commit, state }) {
      store.commit("clearProperty")
      await httpPostWithLoading(LOGOUT_URL, null)
      commit("userLogout")
    },
    async refreshUserInfo ({ commit, state }) {
      let req = {}
      if (isApp()) {
        req.pushId = store.state.user.pushId
        req.deviceInfo = getDeviceInfo()
      }
      const result = await httpPost(REFRESH_URL, req)
      if (result.success) {
        commit("updateUser", result.data.sysUser)
        commit("updatePartner", result.data.partner)
        commit("updateAgency", result.data.agency)
      }
      return null
    }
  }
}

export default user
