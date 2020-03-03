import Vue from "vue"

Vue.directive("permission", {
  inserted: function (el, binding, vnode) {
    const store = vnode.context.$store
    const { value } = binding
    // const routers = store.getters && store.getters.addRouters
    const buttonPermissionList = store.getters && store.getters.buttonPermissionList
    // 需要验证的所有权限
    let valueArray = []
    if (typeof (value) === "string") {
      valueArray.push(value)
    } else if (value instanceof Array) {
      valueArray = value
    }

    if (valueArray && valueArray instanceof Array && valueArray.length > 0) {
      const hasPermission = valueArray.every(permissionString => {
        if (buttonPermissionList.includes("admin")) {
          return true
        }
        return buttonPermissionList.includes(permissionString)
      })

      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error("need permission string")
    }
  }
})
