/**
 * @Author: leemon
 * @Date: 2018-12-26 09:37
 * @Description:
 */

import { trimEndPathSep } from "@/util/path"

export function getParentRoute (list, parentID) {
  let parentRoute = null
  for (const route of list) {
    if (route.id === parentID) {
      parentRoute = route
      break
    }
    if (route.children !== undefined) {
      parentRoute = getParentRoute(route.children, parentID)
      if (parentRoute !== null) {
        break
      }
    }
  }
  return parentRoute
}

/**
 * 对数据进行重新排列，只针对权限菜单接口起作用
 * @param routes 要求排好序的列表
 * @param buttonToChild button的权限是填到children里面还是permissions里面
 * @returns {Array}
 */
export function parseRoutes (routes, buttonToChild) {
  const list = []
  for (const route of routes) {
    if (route.parentId === "1") {
      list.push(route)
      continue
    }
    const parentRoute = getParentRoute(list, route.parentId)
    if (parentRoute === null) {
      continue
    }
    if (!route.href && route.isShow === "0" && !buttonToChild) { // 菜单按钮权限
      const parentRoutePermissions = parentRoute.permissions
      if (parentRoutePermissions === undefined) {
        const permissions = []
        permissions.push(route)
        parentRoute.permissions = permissions
      } else {
        parentRoute.permissions.push(route)
      }
    } else { // 菜单选项
      const parentRouteChildren = parentRoute.children
      if (parentRouteChildren === undefined) {
        const children = []
        children.push(route)
        parentRoute.children = children
      } else {
        parentRoute.children.push(route)
      }
    }
  }
  return list
}

export function isAllowedPath (routes, path) {
  path = trimEndPathSep(path)
  for (let route of routes) {
    if (trimEndPathSep(route.href) === path) {
      return true
    }
    const children = route.children
    if (children instanceof Array && children.length > 0) {
      if (isAllowedPath(children, path)) {
        return true
      }
    }
  }
  return false
}
