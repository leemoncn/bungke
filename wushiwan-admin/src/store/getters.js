/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */

// import { USER_INFO } from "@/store/mutation-types"

const getters = {
  isLogin: state => {
    const token = state.user.token
    return !!token
  },
  userToken: state => state.user.token,
  // device: state => state.app.device,
  theme: state => state.app.theme,
  color: state => state.app.color,
  token: state => state.user.token,
  avatar: state => {
    return null
    // state.user.avatar = Vue.ls.get(USER_INFO).avatar
    // return state.user.avatar
  },
  username: state => state.user.user.nickname,
  nickname: state => {
    return state.user.user.nickname
  },
  welcome: state => state.user.welcome,
  menuList: state => state.menu.menuList,
  buttonPermissionList: state => state.menu.buttonPermissionList,
  userInfo: state => {
    // state.user.info = Vue.ls.get(USER_INFO)
    // return state.user.info
    return null
  },
  addRouters: state => state.menu.addRouters,
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  permission_routers: state => state.permission.routers,
  permission_names: state => state.permission.routerNames,
  iframeList: state => state.iframe.iframeList,
  currentIframeUrl: state => state.iframe.currentUrl,
  propertyList: state => state.property.properties
}

export default getters
