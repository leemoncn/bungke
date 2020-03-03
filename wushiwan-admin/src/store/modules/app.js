import Lockr from "lockr"

const app = {
  state: {
    sidebar: {
      opened: true,
      withoutAnimation: false
    },
    device: "desktop",
    theme: "light",
    layout: "sidemenu",
    contentWidth: "Fixed",
    fixedHeader: false,
    fixSiderbar: false,
    autoHideHeader: false,
    color: "#1890FF",
    weak: false,
    multipage: true
  },
  mutations: {
    TOGGLE_SIDEBAR: state => {
      if (state.sidebar.opened) {
        Lockr.set("sidebarStatus", 1)
      } else {
        Lockr.set("sidebarStatus", 0)
      }
      state.sidebar.opened = !state.sidebar.opened
      state.sidebar.withoutAnimation = false
    },
    CLOSE_SIDEBAR: (state, withoutAnimation) => {
      Lockr.set("sidebarStatus", 1)
      state.sidebar.opened = false
      state.sidebar.withoutAnimation = withoutAnimation
    },
    TOGGLE_DEVICE: (state, device) => {
      state.device = device
    }
  },
  actions: {
    ToggleSideBar: ({ commit }) => {
      commit("TOGGLE_SIDEBAR")
    },
    CloseSideBar ({ commit }, { withoutAnimation }) {
      commit("CLOSE_SIDEBAR", withoutAnimation)
    },
    ToggleDevice ({ commit }, device) {
      commit("TOGGLE_DEVICE", device)
    }
  }
}

export default app
