/**
 * @Author: leemon
 * @Date: 2019-05-06 13:59
 * @Description:
 */

import store from "@/store"

/**
 * 根据属性表的id获取MissionRule
 */
export function getMissionRuleByTypePropertyId (typePropertyId) {
  let missionRuleList = store.getters.missionRuleList
  return missionRuleList.find((value, index, arr) => {
    return value.typePropertyId === typePropertyId
  })
}
