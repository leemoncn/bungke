/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */

const tabbar = {
  state: {
    activeItemIndex: 0
  },
  mutations: {
    changeActiveItemIndex: (state, data) => {
      state.activeItemIndex = data
    }
  }
}

export default tabbar
