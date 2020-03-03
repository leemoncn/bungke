import Vue from "vue"
import Router from "vue-router"
import store from "@/store"
import { isApp } from "@/service/plus"

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: "/mobile-login",
      name: "MobileLogin",
      component: () => import("./views/login/MobileLogin")
    },
    {
      path: "/third-login",
      name: "ThirdLogin",
      component: () => import("./views/login/ThirdLogin")
    },
    {
      path: "/register",
      name: "Register",
      component: () => import("./views/login/Register")
    },
    {
      path: "/test",
      name: "Test",
      component: () => import("./views/login/Test")
    },
    {
      path: "/",
      name: "TabPage",
      component: () => import("./views/tab/TabPage")
    },
    {
      path: "/create-mission",
      name: "CreateMission",
      component: () => import("./views/home/CreateMission")
    },
    {
      path: "/accept-mission",
      name: "AcceptMission",
      component: () => import("./views/home/AcceptMission")
    },
    {
      path: "/mission-detail",
      name: "MissionDetail",
      component: () => import("./views/home/MissionDetail")
    },
    {
      path: "/my-mission",
      name: "MyMission",
      component: () => import("./views/home/MyMission")
    },
    {
      path: "/finish-mission",
      name: "FinishMission",
      component: () => import("./views/home/FinishMission")
    },
    {
      path: "/mission-review",
      name: "MissionReview",
      component: () => import("./views/home/MissionReview")
    },
    {
      path: "/my-need-review",
      name: "MyNeedReview",
      component: () => import("./views/home/MyNeedReview")
    },
    {
      path: "/qrcode",
      name: "QRCode",
      component: () => import("./views/home/QRCode")
    },
    {
      path: "/complaint",
      name: "Complaint",
      component: () => import("./views/home/Complaint")
    },
    {
      path: "/advice",
      name: "Advice",
      component: () => import("./views/personal/Advice")
    },
    {
      path: "/recharge-mission-coin",
      name: "RechargeMissionCoin",
      component: () => import("./views/personal/RechargeMissionCoin")
    },
    {
      path: "/recharge-deposit",
      name: "RechargeDeposit",
      component: () => import("./views/personal/RechargeDeposit")
    },
    {
      path: "/alipay",
      name: "Alipay",
      component: () => import("./views/personal/Alipay")
    },
    {
      path: "/browser",
      name: "Browser",
      component: () => import("./views/personal/Browser")
    },
    {
      path: "/recharge-finish",
      name: "BrowserPayFinish",
      component: () => import("./views/personal/BrowserPayFinish")
    },
    {
      path: "/withdrawal-earning",
      name: "WithdrawalEarning",
      component: () => import("./views/personal/WithdrawalEarning")
    },
    {
      path: "/withdrawal-deposit",
      name: "WithdrawalDeposit",
      component: () => import("./views/personal/WithdrawalDeposit")
    },
    {
      path: "/withdrawal-mission-coin",
      name: "WithdrawalMissionCoin",
      component: () => import("./views/personal/WithdrawalMissionCoin")
    },
    {
      path: "/ailpay-account",
      name: "AlipayAccount",
      component: () => import("./views/personal/AlipayAccount")
    },
    {
      path: "/partner",
      name: "Partner",
      component: () => import("./views/personal/Partner")
    }, {
      path: "/subordinate",
      name: "Subordinate",
      component: () => import("./views/personal/Subordinate")
    }, {
      path: "/fans",
      name: "Fans",
      component: () => import("./views/personal/Fans")
    }, {
      path: "/shop",
      name: "Shop",
      component: () => import("./views/personal/Shop")
    }, {
      path: "/question",
      name: "Question",
      component: () => import("./views/personal/Question")
    }, {
      path: "/question-detail",
      name: "QuestionDetail",
      component: () => import("./views/personal/QuestionDetail")
    }, {
      path: "/html-page",
      name: "HtmlPage",
      component: () => import("./views/home/HtmlPage")
    }, {
      path: "/credit-card",
      name: "CreditCard",
      component: () => import("./views/home/CreditCard")
    }, {
      path: "/currency-detail",
      name: "CurrencyDetail",
      component: () => import("./views/personal/CurrencyDetail")
    }
  ]
})
// 无需判断登陆权限的白名单页面
const whiteList = ["/mobile-login", "/third-login", "/download"]

function trackPage (to) {
  // eslint-disable-next-line
  if (_hmt) {
    if (to.path) {
      let path = "/#" + to.path
      if (to.query && to.query.track) {
        path += "?track="
        path += to.query.track
      }
      // eslint-disable-next-line
      _hmt.push(["_trackPageview", path])
    }
  }
}

router.beforeEach(async (to, from, next) => {
  if (to.path === "/third-login" && !isApp()) {
    next("/mobile-login")
    return
  }
  if (whiteList.includes(to.path)) { // 访问白名单页面，直接放行
    if (store.getters.isLogin && (to.path === "/mobile-login" || to.path === "/third-login")) { // 已经登录的无需进入登录页
      next("/")
      return
    }
    Vue.navigation.cleanRoutes()
    store.commit("changeActiveItemIndex", 0)
    trackPage(to)
    next()
    return
  }
  if (!store.getters.isLogin) { // 没有登陆的话，统一跳转到登陆页
    if (isApp()) {
      next("/third-login")
    } else {
      next("/mobile-login")
    }
    return
  }
  await loadNecessaryData()
  trackPage(to)
  next()
})

async function loadNecessaryData () {
  if (store.getters.propertyList.length === 0) { // 更新property属性表
    await store.dispatch("loadProperty")
  }
  if (store.getters.missionRuleList.length === 0) {
    await store.dispatch("loadMissionRule")
  }
}

export default router
