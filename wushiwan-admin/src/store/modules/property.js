/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */
import { httpPost } from "@/util/request"
import { SYSPROPERTY_QUERY_ALL_URL } from "@/config/host"

const property = {
  state: {
    properties: []
  },
  mutations: {
    updateProperty: (state, data) => {
      state.properties = data
    },
    clearProperty: (state) => {
      state.properties = []
    }
  },

  actions: {
    loadProperty ({ commit }) { // 登录
      return httpPost(SYSPROPERTY_QUERY_ALL_URL).then((result) => {
        if (result.success) {
          commit("updateProperty", result.data)
        }
        return result
      })
    }
  }
}

export default property
