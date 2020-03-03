/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */
import { httpPost } from "@/util/request"
import { SYSPROPERTY_QUERY_ALL_URL, MISSION_RULE_URL } from "@/config/host"

const configuration = {
  state: {
    properties: [],
    missionRules: []
  },
  mutations: {
    updateProperty: (state, data) => {
      state.properties = data
    },
    clearProperty: (state) => {
      state.properties = []
    },
    updateMissionRule: (state, data) => {
      state.missionRules = data
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
    },
    loadMissionRule ({ commit }) {
      return httpPost(MISSION_RULE_URL).then((result) => {
        if (result.success) {
          commit("updateMissionRule", result.data)
        }
        return result
      })
    }
  }
}

export default configuration
