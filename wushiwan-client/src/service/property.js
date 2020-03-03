/**
 * @Author: leemon
 * @Date: 2019-05-06 13:59
 * @Description:
 */

import store from "@/store"

export const PropertyType = {
  NONE: 0,
  LOGIN_REGISTER: 1, // 登陆注册类型
  LOGIN_STATUS: 2, // 是否允许登录类型
  MISSION_TYPE: 3, // 任务类型
  DEVICE_TYPE: 4, // 设备类型
  MISSION_PROCEED: 5, // 任务完成情况-5
  MISSION_PUBLISH: 6// 任务发布情况-6
}

/**
 * 通过ID获取property
 * @param id
 * @returns {Promise<*>}
 */
export function getPropertyById (id) {
  let propertyList = store.getters.propertyList
  return propertyList.find((value, index, arr) => {
    return value.id === id
  })
}

/**
 * 根据id，查询跟这个ID类型相同的属性
 * @param id
 * @returns {*}
 */
export function getSameTypePropertyListById (id) {
  let property = getPropertyById(id)
  let type = property.type
  return store.getters.propertyList.filter((p) => {
    return p.type === type
  })
}

/**
 * 根据id，查询跟这个type相同的属性
 * @param type
 * @returns {*}
 */
export function getPropertyListByType (type) {
  return store.getters.propertyList.filter((p) => {
    return p.type === type
  })
}
