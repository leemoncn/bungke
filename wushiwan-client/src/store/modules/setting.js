/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */

const setting = {
  state: {
    newMsgCount: 0
  },
  mutations: {
    addNewMsgCount: (state, count) => {
      state.newMsgCount += count
    },
    updateNewMsgCount: (state, count) => {
      state.newMsgCount = count
    }
  }
}

export default setting
