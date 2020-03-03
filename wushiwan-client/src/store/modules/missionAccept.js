/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */

const user = {
  state: {
    needRemoveItemIdOnActive: null
  },
  mutations: {
    userNeedRemoveItemId: (state, id) => {
      state.needRemoveItemIdOnActive = id
    }
  },

  actions: {}
}

export default user
