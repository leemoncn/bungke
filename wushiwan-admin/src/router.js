import Vue from "vue"
import Router from "vue-router"
import store from "@/store"
import NProgress from "nprogress" // Progress 进度条
import "nprogress/nprogress.css"// Progress 进度条样式
import { generateIndexRouter } from "@/util/util"
// import { constantRouterMap } from "@/config/router.config"

Vue.use(Router)

const router = new Router({
  base: process.env.BASE_URL,
  scrollBehavior: () => ({ y: 0 }),
  routes: [{
    path: "/login",
    name: "Login",
    component: () => import("@/views/login")
  }, {
    path: "/404",
    component: () => import("@/views/404")
  }]
})

const whiteList = ["/login", "/404"] // 不重定向白名单
router.beforeEach(async (to, from, next) => {
  // console.log("from = ", from)
  // console.log("to = ", to)
  NProgress.start()
  if (to.path === "/404") {
    next()
    NProgress.done()
    return
  }
  if (store.getters.isLogin) {
    if (store.getters.propertyList.length === 0) { // 更新property属性表
      await store.dispatch("loadProperty")
    }
    if (to.path === "/login") { // 跳转到主页
      next({ path: "/" })
      NProgress.done()
    } else {
      if (store.getters.menuList.length === 0) {
        const result = await store.dispatch("refreshMenuList")
        if (result.success) {
          const menuData = result.data
          let constRoutes = generateIndexRouter(menuData.menus)
          // 添加主界面路由
          await store.dispatch("updateAppRouter", { constRoutes })
          // 根据roles权限生成可访问的路由表
          // 动态添加可访问路由表,路由表没有层级关系，没有children
          router.addRoutes(store.getters.addRouters)
          const redirect = decodeURIComponent(from.query.redirect || to.path)
          if (to.path === redirect) {
            // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
            next({ ...to, replace: true })
          } else {
            // 跳转到目的路由
            next({ path: redirect })
          }
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`) // 否则全部重定向到登录页
    }
    NProgress.done()
  }
})
// NProgress.configure({ showSpinner: false }) // NProgress Configuration
//
// const whiteList = ["/user/login", "/user/register", "/user/register-result"] // no redirect whitelist
//
// router.beforeEach(async (to, from, next) => {
//   console.log("from = ", from)
//   console.log("to = ", to)
//   console.log("routers = ", router)
//
//   NProgress.start() // start progress bar
//   // if (to.path === "/404") {
//   //   NProgress.done()
//   //   next()
//   //   return
//   // }
//
//   console.log("isLogin = ", store.getters.isLogin)
//   if (store.getters.isLogin) {
//     /* has token */
//     if (to.path === "/user/login") {
//       next({ path: "/" })
//       NProgress.done()
//     } else {
//       console.log("store.getters.menuList = ", store.getters.menuList)
//       if (store.getters.menuList.length === 0) {
//         const result = await store.dispatch("refreshMenuList")
//         if (result.success) {
//           const menuData = result.data
//           let constRoutes = generateIndexRouter(menuData)
//           console.log("constRoutes = ", constRoutes)
//           // 添加主界面路由
//           await store.dispatch("updateAppRouter", { constRoutes })
//           // 根据roles权限生成可访问的路由表
//           // 动态添加可访问路由表
//           router.addRoutes(store.getters.addRouters)
//           console.log("动态添加路由 = ", store.getters.addRouters)
//           console.log("router = ", router)
//           const redirect = decodeURIComponent(from.query.redirect || to.path)
//           console.log("redirect", redirect)
//           if (to.path === redirect) {
//             // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
//             console.log("replace to")
//             next({ ...to, replace: true })
//             // next({ name: "hhh" })
//           } else {
//             // 跳转到目的路由
//             console.log("replace to redirect")
//             next({ path: redirect })
//           }
//         }
//       } else {
//         next()
//       }
//     }
//   } else {
//     if (whiteList.indexOf(to.path) !== -1) {
//       // 在免登录白名单，直接进入
//       next()
//     } else {
//       next({ path: "/user/login", query: { redirect: to.fullPath } })
//       NProgress.done() // if current page is login will not trigger afterEach hook, so manually handle it
//     }
//   }
// })
//
router.afterEach(() => {
  NProgress.done() // finish progress bar
})

export default router
