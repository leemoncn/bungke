/**
 * @Author: limeng
 * @Date: 2019-04-01 20:18
 * @Description:
 */

const getters = {
  isLogin: state => {
    const token = state.user.token
    return !!token
  },
  userToken: state => state.user.token,
  pushId: state => state.user.pushId,
  tabbarActiveItemIndex: state => state.tabbar.activeItemIndex,
  propertyList: state => state.configuration.properties,
  missionRuleList: state => state.configuration.missionRules,
  user: state => state.user.user,
  userId: state => state.user.user.id,
  partner: state => state.user.partner,
  agency: state => state.user.agency,
  rechargeSuccessInApp: state => state.recharge.rechargeSuccessInApp
}

export default getters
