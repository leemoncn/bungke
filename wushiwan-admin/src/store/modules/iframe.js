const iframe = {
  state: {
    iframeList: [],
    currentUrl: ""
  },
  mutations: {
    // iframeUrl  http://www.baidu.com
    ADD_IFRAME: (state, iframeUrl) => {
      if (!state.iframeList.includes(iframeUrl)) {
        state.iframeList.push(iframeUrl)
      }
    },
    REMOVE_IFRAME: (state, iframeUrl) => {
      const list = state.iframeList
      const index = list.findIndex(v => v === iframeUrl)
      if (index >= 0) {
        list.splice(index, 1)
      }
      if (state.currentUrl === iframeUrl) {
        state.currentUrl = ""
      }
    },
    REMOVE_OTHER_IFRAME: (state, iframeUrl) => {
      state.currentUrl = iframeUrl
      const list = state.iframeList
      for (let i = list.length - 1; i >= 0; i--) {
        if (list[i] !== iframeUrl) {
          list.splice(i, 1)
        }
      }
    },
    SET_CURRENT_IFRAME_URL: (state, iframeUrl) => {
      state.currentUrl = iframeUrl
    },
    REMOVE_ALL_IFRAME: (state) => {
      state.currentUrl = ""
      state.iframeList.length = 0
    }
  },
  actions: {
    // ToggleSideBar: ({ commit }) => {
    //   commit("TOGGLE_SIDEBAR")
    // },
    // CloseSideBar ({ commit }, { withoutAnimation }) {
    //   commit("CLOSE_SIDEBAR", withoutAnimation)
    // },
    // ToggleDevice ({ commit }, device) {
    //   commit("TOGGLE_DEVICE", device)
    // }
  }
}

export default iframe
