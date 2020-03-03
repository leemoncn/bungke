// 后端地址
export const LOCAL_SERVER = "XXX"
export const DEV_SERVER = "XXX"
export const DIST_SERVER = "XXX"

// 图片地址
export const LOCAL_IMG = "XXX"
export const DEV_IMG = "XXX"
export const DIST_IMG = "XXX"

export const HOST = process.env.VUE_APP_DIST === "1" ? DIST_SERVER : process.env.NODE_ENV === "production" ? DEV_SERVER : LOCAL_SERVER
export const IMG_HOST = process.env.VUE_APP_DIST === "1" ? DIST_IMG : process.env.NODE_ENV === "production" ? DEV_IMG : LOCAL_IMG

// user
export const USER_INFO_URL = HOST + "/sys-user/queryDetailById"

// index
export const CLEAN_CACHE_URL = HOST + "/index/clean-cache"
export const TEST_URL = HOST + "/index/test"

// 蚂蚁
export const MAYI_CREATE_MISSION = HOST + "/mayi/mission/create"
export const MAYI_MISSION_LIST = HOST + "/mayi/mission/list"
export const MAYI_MISSION_DETAIL = HOST + "/mayi/mission/detail"

// CoreLoginAuth
export const LOGIN_URL = HOST + "/core-login-auth/no-auth/login"
export const REFRESH_URL = HOST + "/core-login-auth/refresh"
export const LOGOUT_URL = HOST + "/core-login-auth/logout"

// CoreImg地址
export const COREIMG_LIST_URL = HOST + "/core-img/list"
export const COREIMG_DELETE_URL = HOST + "/core-img/delete"
export const COREIMG_ADD_URL = HOST + "/core-img/add"
export const COREIMG_EDIT_URL = HOST + "/core-img/edit"
export const COREIMG_QUERY_URL = HOST + "/core-img/query"

// SysMenu地址
export const SYSMENU_USER_URL = HOST + "/sys-menu/queryByUser"
export const SYSMENU_LIST_URL = HOST + "/sys-menu/list"
export const SYSMENU_DELETE_URL = HOST + "/sys-menu/delete"
export const SYSMENU_ADD_URL = HOST + "/sys-menu/add"
export const SYSMENU_EDIT_URL = HOST + "/sys-menu/edit"

// SysUser地址
export const SYSUSER_LIST_URL = HOST + "/sys-user/list"
export const SYSUSER_DELETE_URL = HOST + "/sys-user/delete"
export const SYSUSER_ADD_URL = HOST + "/sys-user/add"
export const SYSUSER_EDIT_URL = HOST + "/sys-user/edit"

// SysRole地址
export const SYSROLE_LIST_URL = HOST + "/sys-role/list"
export const SYSROLE_DELETE_URL = HOST + "/sys-role/delete"
export const SYSROLE_ADD_URL = HOST + "/sys-role/add"
export const SYSROLE_EDIT_URL = HOST + "/sys-role/edit"
export const SYSROLE_ALL_PERMISSIONS_URL = HOST + "/sys-role/role-permission"
export const SYSROLE_EDIT_ALL_PERMISSIONS_URL = HOST + "/sys-role/edit-role-permission"

// SysProperty地址
export const SYSPROPERTY_LIST_URL = HOST + "/sys-property/list"
export const SYSPROPERTY_DELETE_URL = HOST + "/sys-property/delete"
export const SYSPROPERTY_ADD_URL = HOST + "/sys-property/add"
export const SYSPROPERTY_EDIT_URL = HOST + "/sys-property/edit"
export const SYSPROPERTY_QUERY_ALL_URL = HOST + "/sys-property/properties"

// CoreMission地址
export const COREMISSION_LIST_URL = HOST + "/core-mission/list"
export const COREMISSION_DELETE_URL = HOST + "/core-mission/delete"
export const COREMISSION_ADD_URL = HOST + "/core-mission/add"
export const COREMISSION_EDIT_URL = HOST + "/core-mission/edit"
export const COREMISSION_DETAIL_STEP_URL = HOST + "/core-mission-detail-step/list"
export const COREMISSION_REVIEW_DETAIL_URL = HOST + "/social-review-chat/chat-list"

// CoreMissionRule地址
export const COREMISSIONRULE_LIST_URL = HOST + "/core-mission-rule/list"
export const COREMISSIONRULE_DELETE_URL = HOST + "/core-mission-rule/delete"
export const COREMISSIONRULE_ADD_URL = HOST + "/core-mission-rule/add"
export const COREMISSIONRULE_EDIT_URL = HOST + "/core-mission-rule/edit"

// CorePartner地址
export const COREPARTNER_LIST_URL = HOST + "/core-partner/list"
export const COREPARTNER_DELETE_URL = HOST + "/core-partner/delete"
export const COREPARTNER_ADD_URL = HOST + "/core-partner/add"
export const COREPARTNER_EDIT_URL = HOST + "/core-partner/edit"

// CoreMissionAccept地址
export const COREMISSIONACCEPT_LIST_URL = HOST + "/core-mission-accept/list"
export const COREMISSIONACCEPT_DELETE_URL = HOST + "/core-mission-accept/delete"
export const COREMISSIONACCEPT_ADD_URL = HOST + "/core-mission-accept/add"
export const COREMISSIONACCEPT_EDIT_URL = HOST + "/core-mission-accept/edit"

// CoreMissionDetail地址
export const COREMISSIONDETAIL_LIST_URL = HOST + "/core-mission-detail/list"
export const COREMISSIONDETAIL_DELETE_URL = HOST + "/core-mission-detail/delete"
export const COREMISSIONDETAIL_ADD_URL = HOST + "/core-mission-detail/add"
export const COREMISSIONDETAIL_EDIT_URL = HOST + "/core-mission-detail/edit"

// CoreMissionComplaint地址
export const COREMISSIONCOMPLAINT_LIST_URL = HOST + "/core-mission-complaint/list"
export const COREMISSIONCOMPLAINT_DELETE_URL = HOST + "/core-mission-complaint/delete"
export const COREMISSIONCOMPLAINT_ADD_URL = HOST + "/core-mission-complaint/add"
export const COREMISSIONCOMPLAINT_EDIT_URL = HOST + "/core-mission-complaint/edit"
export const COREMISSIONCOMPLAINT_HANDLE_COMPLAINT_URL = HOST + "/core-mission-complaint/handle-complaint"

// CoreCurrencyChange地址
export const CORECURRENCYCHANGE_LIST_URL = HOST + "/core-currency-change/list"
export const CORECURRENCYCHANGE_DELETE_URL = HOST + "/core-currency-change/delete"
export const CORECURRENCYCHANGE_ADD_URL = HOST + "/core-currency-change/add"
export const CORECURRENCYCHANGE_EDIT_URL = HOST + "/core-currency-change/edit"

// SocialNotice地址
export const SOCIALNOTICE_LIST_URL = HOST + "/social-notice/list"
export const SOCIALNOTICE_DELETE_URL = HOST + "/social-notice/delete"
export const SOCIALNOTICE_ADD_URL = HOST + "/social-notice/add"
export const SOCIALNOTICE_EDIT_URL = HOST + "/social-notice/edit"

// CorePartnerPurchase地址
export const COREPARTNERPURCHASE_LIST_URL = HOST + "/core-partner-purchase/list"
export const COREPARTNERPURCHASE_DELETE_URL = HOST + "/core-partner-purchase/delete"
export const COREPARTNERPURCHASE_ADD_URL = HOST + "/core-partner-purchase/add"
export const COREPARTNERPURCHASE_EDIT_URL = HOST + "/core-partner-purchase/edit"
export const COREPARTNERPURCHASE_SEND_URL = HOST + "/core-partner-purchase/send"

// FinancialWithdrawal地址
export const FINANCIALWITHDRAWAL_LIST_URL = HOST + "/financial-withdrawal/list"
export const FINANCIALWITHDRAWAL_DELETE_URL = HOST + "/financial-withdrawal/delete"
export const FINANCIALWITHDRAWAL_ADD_URL = HOST + "/financial-withdrawal/add"
export const FINANCIALWITHDRAWAL_REVIEW_WITHDRAWAL_URL = HOST + "/financial-withdrawal/review-withdrawal"

// CoreAgency地址
export const COREAGENCY_LIST_URL = HOST + "/core-agency/list"
export const COREAGENCY_DELETE_URL = HOST + "/core-agency/delete"
export const COREAGENCY_ADD_URL = HOST + "/core-agency/add"
export const COREAGENCY_EDIT_URL = HOST + "/core-agency/edit"

// CoreDepositPurchase地址
export const COREDEPOSITPURCHASE_LIST_URL = HOST + "/core-deposit-purchase/list"
export const COREDEPOSITPURCHASE_DELETE_URL = HOST + "/core-deposit-purchase/delete"
export const COREDEPOSITPURCHASE_ADD_URL = HOST + "/core-deposit-purchase/add"
export const COREDEPOSITPURCHASE_EDIT_URL = HOST + "/core-deposit-purchase/edit"

// FinancialTrade地址
export const FINANCIALTRADE_LIST_URL = HOST + "/financial-trade/list"
export const FINANCIALTRADE_DELETE_URL = HOST + "/financial-trade/delete"
export const FINANCIALTRADE_ADD_URL = HOST + "/financial-trade/add"
export const FINANCIALTRADE_EDIT_URL = HOST + "/financial-trade/edit"

// SysAdvice地址
export const SYSADVICE_LIST_URL = HOST + "/sys-advice/list"
export const SYSADVICE_DELETE_URL = HOST + "/sys-advice/delete"
export const SYSADVICE_ADD_URL = HOST + "/sys-advice/add"
export const SYSADVICE_EDIT_URL = HOST + "/sys-advice/edit"

// SocialFollow地址
export const SOCIALFOLLOW_LIST_URL = HOST + "/social-follow/list"
export const SOCIALFOLLOW_DELETE_URL = HOST + "/social-follow/delete"
export const SOCIALFOLLOW_ADD_URL = HOST + "/social-follow/add"
export const SOCIALFOLLOW_EDIT_URL = HOST + "/social-follow/edit"

// SocialReviewChat地址
export const SOCIALREVIEWCHAT_LIST_URL = HOST + "/social-review-chat/list"
export const SOCIALREVIEWCHAT_DELETE_URL = HOST + "/social-review-chat/delete"
export const SOCIALREVIEWCHAT_ADD_URL = HOST + "/social-review-chat/add"
export const SOCIALREVIEWCHAT_EDIT_URL = HOST + "/social-review-chat/edit"

// CoreTop地址
export const CORETOP_LIST_URL = HOST + "/core-top/list"
export const CORETOP_DELETE_URL = HOST + "/core-top/delete"
export const CORETOP_ADD_URL = HOST + "/core-top/add"
export const CORETOP_EDIT_URL = HOST + "/core-top/edit"

// CoreMissionDetailStep地址
export const COREMISSIONDETAILSTEP_LIST_URL = HOST + "/core-mission-detail-step/list"
export const COREMISSIONDETAILSTEP_DELETE_URL = HOST + "/core-mission-detail-step/delete"
export const COREMISSIONDETAILSTEP_ADD_URL = HOST + "/core-mission-detail-step/add"
export const COREMISSIONDETAILSTEP_EDIT_URL = HOST + "/core-mission-detail-step/edit"

// CoreVersion地址
export const COREVERSION_LIST_URL = HOST + "/core-version/list"
export const COREVERSION_DELETE_URL = HOST + "/core-version/delete"
export const COREVERSION_ADD_URL = HOST + "/core-version/add"
export const COREVERSION_EDIT_URL = HOST + "/core-version/edit"

// FinancialTransfer地址
export const FINANCIALTRANSFER_LIST_URL = HOST + "/financial-transfer/list"
export const FINANCIALTRANSFER_DELETE_URL = HOST + "/financial-transfer/delete"
export const FINANCIALTRANSFER_ADD_URL = HOST + "/financial-transfer/add"
export const FINANCIALTRANSFER_EDIT_URL = HOST + "/financial-transfer/edit"

// CoreQuestion地址
export const COREQUESTION_LIST_URL = HOST + "/core-question/list"
export const COREQUESTION_DELETE_URL = HOST + "/core-question/delete"
export const COREQUESTION_ADD_URL = HOST + "/core-question/add"
export const COREQUESTION_EDIT_URL = HOST + "/core-question/edit"

// CoreBanner地址
export const COREBANNER_LIST_URL = HOST + "/core-banner/list"
export const COREBANNER_DELETE_URL = HOST + "/core-banner/delete"
export const COREBANNER_ADD_URL = HOST + "/core-banner/add"
export const COREBANNER_EDIT_URL = HOST + "/core-banner/edit"

// CoreNotice地址
export const CORENOTICE_LIST_URL = HOST + "/core-notice/list"
export const CORENOTICE_DELETE_URL = HOST + "/core-notice/delete"
export const CORENOTICE_ADD_URL = HOST + "/core-notice/add"
export const CORENOTICE_EDIT_URL = HOST + "/core-notice/edit"

// 七牛云上传图片
export const QINIUKODO_UPLOAD_TOKEN_URL = HOST + "/qiniu/kodo/create-token"

// push
export const PUSH_CREATE_URL = HOST + "/push/create"
