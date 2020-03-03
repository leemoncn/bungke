// import { asyncRouterMap, constantRouterMap } from "@/config/router.config"
import { httpPost } from "@/util/request"
import { SYSMENU_USER_URL } from "@/config/host"

/**
 * 过滤账户是否拥有某一个权限，并将菜单从加载列表移除
 *
 * @param permission
 * @param route
 * @returns {boolean}
 */
function hasPermission (permission, route) {
  if (route.meta && route.meta.permission) {
    let flag = -1
    for (let i = 0, len = permission.length; i < len; i++) {
      flag = route.meta.permission.indexOf(permission[i])
      if (flag >= 0) {
        return true
      }
    }
    return false
  }
  return true
}

/**
 * 单账户多角色时，使用该方法可过滤角色不存在的菜单
 *
 * @param roles
 * @param route
 * @returns {*}
 */
// eslint-disable-next-line
function hasRole (roles, route) {
  if (route.meta && route.meta.roles) {
    return route.meta.roles.indexOf(roles.id)
  } else {
    return true
  }
}

function filterAsyncRouter (routerMap, roles) {
  const accessedRouters = routerMap.filter(route => {
    if (hasPermission(roles.permissionList, route)) {
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children, roles)
      }
      return true
    }
    return false
  })
  return accessedRouters
}

const menu = {
  state: {
    routers: [],
    addRouters: [],
    menuList: [],
    buttonPermissionList: []
  },
  mutations: {
    updateRouters: (state, data) => {
      state.addRouters = data
      state.routers = [].concat(data)
    },
    updateMenuList: (state, data) => {
      state.menuList = data || []
    },
    updateButtonPermissionList: (state, data) => {
      state.buttonPermissionList = data || []
    }
  },
  actions: {
    // 动态添加主界面路由，需要缓存
    updateAppRouter ({ commit }, routes) {
      let routeList = routes.constRoutes
      commit("updateRouters", routeList)
    },
    async refreshMenuList ({ commit }) {
      const result = await httpPost(SYSMENU_USER_URL)
      if (result.success) {
        result.data.menus.splice(0, 0, {
          id: 0,
          name: "首页",
          sort: 0,
          href: "/",
          icon: "bank",
          isShow: true,
          component: "Home",
          componentName: "Home",
          permission: "sys_home"
        })
        commit("updateMenuList", result.data.menus)
        commit("updateButtonPermissionList", result.data.buttonPermissionList)
      }
      return result
    }
  }
}

export default menu
