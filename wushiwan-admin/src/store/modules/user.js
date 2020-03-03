/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */
import { httpPost } from "@/util/request"
import { LOGIN_URL, LOGOUT_URL } from "@/config/host"
import store from "@/store"

const user = {
  state: {
    roles: [],
    token: "",
    user: null
  },
  mutations: {
    userLoginSuccess: (state, data) => {
      state.token = data.token
      state.user = data.sysUser
      // TODO 角色赋值
    },
    userLogout: (state) => {
      state.token = ""
      state.roles = []
    }
  },

  actions: {
    // 登录
    async login ({ commit }, userInfo) {
      const result = await httpPost(LOGIN_URL, userInfo)
      if (result.success) {
        commit("userLoginSuccess", result.data)
      }
      return result
    },
    // 登出
    logout ({ commit, state }) {
      store.commit("clearProperty")
      return new Promise((resolve, reject) => {
        httpPost(LOGOUT_URL, null).finally(() => {
          commit("userLogout")
          resolve()
        })
      })
    },
    refresh ({ commit, state }) {
      return new Promise((resolve, reject) => {
        // setLogin(false)
        // commit("SET_LOGIN", false)
        // httpPost(LOGOUT_URL, null, ".body").then(() => {
        //   resolve()
        // }).catch(error => {
        //   reject(error)
        // })
      })
    }
  }
}

export default user
