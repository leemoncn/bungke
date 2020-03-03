/**
 * 数组转为树结构
 * @param array
 */
export function arrayToTree (arr, pId = "0") {
  if (!arr || !(arr instanceof Array)) {
    return arr
  }

  let es = arr.filter(ele => ele.parentId === pId)
  for (let e of es) {
    let children = arrayToTree(arr, e.id)
    if (children && children.length > 0) {
      e.children = arrayToTree(arr, e.id)
    }
  }
  return es
}

/**
 * 从list里面获取id的对象，会递归遍历children数组里面的子对象
 * @param list
 * @param id
 * @returns {*}
 */
export function getItemWithId (list, id) {
  for (let item of list) {
    if (item.id === id) {
      return item
    }
    if (item.children && item.children instanceof Array) {
      const newItem = getItemWithId(item.children, id)
      if (newItem) {
        return newItem
      }
    }
  }
  return null
}

/**
 * 从list里面获取permission的对象，会递归遍历children数组里面的子对象
 * @param list
 * @param permission
 * @returns {*}
 */
export function getItemWithPermission (list, permission) {
  for (let item of list) {
    if (item.permission === permission) {
      return item
    }
    if (item.children && item.children instanceof Array) {
      const newItem = getItemWithPermission(item.children, permission)
      if (newItem) {
        return newItem
      }
    }
  }
  return null
}

/**
 * 在list中查找permission字段含有permissionString的对象
 * @param list
 * @param permissionString
 */
export function havePermissionInList (list, permissionString) {
  for (let item of list) {
    let havePermissionInPermissionList = false
    if (item.meta && item.meta.permissionList && item.meta.permissionList instanceof Array) {
      havePermissionInPermissionList = item.meta.permissionList.some(p => p.permission === permissionString)
    }
    if (havePermissionInPermissionList) {
      return true
    }
    if (item.children && item.children instanceof Array) {
      const isHave = havePermissionInList(item.children, permissionString)
      if (isHave) {
        return isHave
      }
    }
  }
  return false
}
