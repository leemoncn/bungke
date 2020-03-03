// import { httpPost } from "@/util/request"
// import { PERMISSION_URL } from "@/config/host"
import * as PathUtil from "@/util/path"
// import * as RouterUtil from "@/util/router"

const permission = {
  state: {
    // 保存的是后台的router对象
    routers: [],
    routerNames: {}
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.routers = routers
    },
    SET_ROUTERS_NAME: (state, routerFullPath) => {
      let routerPath = routerFullPath
      if (routerPath.includes("/iframe?url=")) {
        routerPath = routerPath.replace("/iframe?url=", "")
        routerPath = decodeURIComponent(routerPath)
      }
      routerPath = PathUtil.trimEndPathSep(routerPath)
      if (!routerPath || state.routerNames.hasOwnProperty(routerPath)) {
        return
      }
      const getRouterName = function (routerPath, routers) {
        for (let router of routers) {
          if (PathUtil.trimEndPathSep(router.href) === PathUtil.trimEndPathSep(routerPath)) {
            return router.name
          }
          if (router.children && router.children instanceof Array) {
            const name = getRouterName(routerPath, router.children)
            if (name) {
              return name
            }
          }
        }
        return null
      }
      let name = getRouterName(routerPath, state.routers)
      if (name) {
        name = PathUtil.trimEndPathSep(name)
      }
      // iframe?url=xxxx
      state.routerNames[routerFullPath] = name
    }
  },
  actions: {
    // // 获取权限信息
    // GetPermission ({ commit }) {
    //   return new Promise((resolve, reject) => {
    //     httpPost(PERMISSION_URL).then(response => {
    //       const data = response.data
    //       const routes = RouterUtil.parseRoutes(data, true)
    //       commit("SET_ROUTERS", routes)
    //       resolve({ data: data, routes: routes })
    //     }).catch(error => {
    //       reject(error)
    //     })
    //   })
    // }
  }
}

export default permission
