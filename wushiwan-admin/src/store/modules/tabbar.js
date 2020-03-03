/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */

const user = {
  state: {
    activeItemIndex: 0,
    isShow: false
  },
  mutations: {
    changeShowStatus: (state, data) => {
      state.isShow = data
    },
    changeActiveItemIndex: (state, data) => {
      state.activeItemIndex = data
    }
  }
}

export default user
