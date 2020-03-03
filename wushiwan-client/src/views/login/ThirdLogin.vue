/**
* @Author: leemon
* @Date: 2019-11-01 16:45
* @Description:
*/
<template>
  <div>
    <p>欢迎使用帮客</p>
    <p>零碎时间赚钱花</p>
    <div style="text-align: center">
      <img src="@/assets/img/icon.png" class="icon">
    </div>
    <div class="button flex-row flex-center" @click="onWechatLoginPressed">
      <div class="flex flex-row flex-center">
        <img src="@/assets/img/wechat.png">
        <span>微信登陆</span>
      </div>
    </div>
  </div>
</template>

<script>
  import { REGIST_URL } from "@/config/host"

  export default {
    name: "ThirdLogin",
    methods: {
      onWechatLoginPressed () {
        this.showLoading()
        // 微信授权登录对象
        let weixinAuth = null
        // 当前环境支持的所有授权登录对象
        let auths = null
        let _this = this
        plus.oauth.getServices(function (services) {
          auths = services
          for (const auth of auths) {
            if (auth.id === "weixin") {
              weixinAuth = auth
              break
            }
          }
          if (!weixinAuth) {
            _this.toastMsg("请确认是否安装了微信")
            return
          }
          // target.id target.authResult.access_token code openid refresh_token scope unionid
          if (!weixinAuth.authResult) {
            weixinAuth.login(function (e) {
              weixinAuth.getUserInfo(function (e) {
                _this.checkIfRegistered(weixinAuth.authResult, weixinAuth.userInfo)
              }, function (e) {
                _this.toastMsg("获取用户信息失败")
              })
            }, function (e) {
              console.log("微信登录失败", e)
              // _this.toastMsg("微信登录失败")
            })
          } else { // 已经登录过了
            _this.checkIfRegistered(weixinAuth.authResult, weixinAuth.userInfo)
          }
        }, function (e) {
          _this.toastMsg("获取登录授权服务列表失败")
        })
      },
      checkIfRegistered (authResult, userInfo) {
        this.requestRegist(authResult, userInfo)
      },
      pushToMobileLogin (authResult, userInfo) {
        this.$router.push({
          name: "MobileLogin",
          params: {
            nickname: userInfo.nickname,
            headImgUrl: userInfo.headimgurl,
            openId: userInfo.openid,
            data: {
              authResult: authResult,
              userInfo: userInfo
            }
          }
        })
      },
      async requestRegist (authResult, userInfo) {
        let otherData = {
          account: authResult.openid,
          typePropertyId: 2
        }
        const requestData = {
          otherRegisterData: otherData
        }
        const result = await this.httpPost(REGIST_URL, requestData)
        if (result.success) { // 注册成功或者之前已注册过
          if (result.data) {
            requestData.otherRegisterData.data = {
              authResult: authResult,
              userInfo: userInfo
            }
            await this.requestLogin(requestData)
          } else {
            this.hideLoading()
            this.pushToMobileLogin(authResult, userInfo)
          }
        }
        // 只有两个请求都成功才需要hide，如果有一个失败，会toast失败的消息，会顶掉loading
      },
      async requestLogin (requestData) {
        const { success } = await this.$store.dispatch("login", requestData.otherRegisterData)
        if (success) { // user相关信息的store里面更新
          this.$store.commit("changeActiveItemIndex", 0)
          this.hideLoading()
          this.$router.replace("/")
        }
      }
    }
  }
</script>

<style scoped lang="less">
  p:nth-child(1) {
    font-size: 24px;
    color: #000000;
    margin-left: 37px;
    margin-top: 93px;
  }

  p:nth-child(2) {
    font-size: 14px;
    color: #424242;
    margin-left: 37px;
    margin-top: 13px;
  }

  .icon {
    width: 110px;
    height: 110px;
    margin-top: 80px;
  }

  .button {
    width: 300px;
    height: 50px;
    border-radius: 25px;
    border: solid 1px #81d4cb;
    margin: 97px auto 0 auto;

    img {
      width: 32px;
      height: 32px;
    }

    span {
      margin-left: 20px;
      font-size: 19px;
      color: #81d4cb;
    }
  }
</style>
