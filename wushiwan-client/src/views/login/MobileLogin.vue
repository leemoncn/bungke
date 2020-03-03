/**
* @Author: limeng
* @Date: 2019-03-31 11:15
* @Description:
*/
<template>
  <div>
    <div class="top"></div>
    <div class="title">
      <span>帮客-零碎时间赚钱花</span>
    </div>
    <div class="sub-title">
      <span>{{openId?"绑定手机号":"手机号登录/注册"}}</span>
    </div>
    <div class="phone">
      <span class="prefix">+86 >  </span>
      <input class="number" v-model="phone" placeholder="请输入手机号码" type="tel" pattern="[0-9]*">
    </div>
    <div class="line1"></div>
    <div class="vcode">
      <input type="number" pattern="[0-9]*" placeholder="请输入验证码" v-model="vcode">
      <button class="vcode-button" @click="onVCodeButtonPressed">{{buttonText}}</button>
    </div>
    <div class="line2"></div>
    <div class="top-button"></div>
    <div style="text-align: center" class="flex-column flex-align-center login-button">
      <button class="button" @click="onLoginButtonPressed">{{openId?"绑&nbsp;&nbsp;定":"登&nbsp;&nbsp;录"}}</button>
    </div>
    <div class="bottom-text-top"></div>
    <div class="bottom-text">
      <span>登录代表你已同意</span>
      <span>{{appName}}用户协议、</span>
      <span>隐私政策</span>
    </div>
    <!--<wechat-tip v-show="showWechatTip" @onTipClose="onTipClose"/>-->
  </div>
</template>

<script>
  import { APP_NAME } from "@/config/define"
  import { REGIST_URL, VCODE_URL } from "@/config/host"
  import { Dialog, Toast } from "vant"
  import { isAndroid, isWechat } from "@/service/device"
  import WechatTip from "@/components/Download"
  import { goDownloadPage } from "@/service/function"

  const defaultButtonText = "获取验证码"

  export default {
    name: "MobileLogin",
    components: { WechatTip },
    data: function () {
      return {
        phone: "",
        vcode: "",
        openId: "", // 三方登录的唯一ID
        data: "",
        thirdLoginType: 2, // 2是微信
        appName: APP_NAME,
        countDownSecond: 0,
        timer: null,
        buttonText: defaultButtonText,
        showWechatTip: false

      }
    },
    mounted () {
      this.openId = this.$route.params.openId
      this.data = this.$route.params.data
    },
    beforeDestroy () {
      if (this.timer) {
        clearInterval(this.timer)
      }
    },
    methods: {
      async onLoginButtonPressed () {
        if (this.checkPhone() && this.checkVCode()) {
          this.requestRegist()
        }
      },
      async requestRegist () {
        let otherData = null
        if (this.openId) {
          otherData = {
            account: this.openId,
            password: null,
            typePropertyId: this.thirdLoginType,
            data: this.data
          }
        }
        // private RegisterData mobileRegisterData;
        // private RegisterData otherRegisterData;
        // private String uuid;
        const requestData = {
          mobileRegisterData: {
            account: this.phone,
            password: this.vcode,
            typePropertyId: 35 // 手机验证码方式
          },
          otherRegisterData: otherData
        }
        // 通过别人的分享二维码注册
        const uuid = this.$route.query.uuid
        if (uuid) {
          requestData["uuid"] = uuid
        }
        this.showLoading()
        const result = await this.httpPost(REGIST_URL, requestData)
        if (result.success && result.data) { // 注册成功或者之前已注册过
          await this.requestLogin(requestData)
        }
        // 只有两个请求都成功才需要hide，如果有一个失败，会toast失败的消息，会顶掉loading
      },
      async requestLogin (requestData) {
        const { success } = await this.$store.dispatch("login", requestData.otherRegisterData ? requestData.otherRegisterData : requestData.mobileRegisterData)
        if (success) { // user相关信息的store里面更新
          this.hideLoading()
          if (!this.isApp() && isAndroid()) {
            Dialog.confirm({
              title: "提示",
              messageAlign: "left",
              message: "下载帮客APP版本可获取更高收益，是否下载？"
            }).then(() => {
              goDownloadPage()
            }).catch(() => {
              this.$store.commit("changeActiveItemIndex", 0)
              this.$router.replace("/")
            })
          } else {
            this.$store.commit("changeActiveItemIndex", 0)
            this.$router.replace("/")
          }
        }
      },
      async onVCodeButtonPressed () {
        if (this.buttonText !== defaultButtonText) {
          return
        }
        if (this.checkPhone()) {
          const result = await this.httpPostWithLoading(VCODE_URL, { str: this.phone })
          if (result.success) {
            this.toastSuccess("验证码已发送")
            this.countDownSecond = 60
            this.countDown()
            this.timer = setInterval(this.countDown, 1000)
          }
        }
      },
      countDown () {
        if (this.countDownSecond === 0) {
          clearInterval(this.timer)
          this.timer = null
          this.buttonText = defaultButtonText
          return
        }
        this.countDownSecond--
        this.buttonText = `${this.countDownSecond}秒`
      },
      checkPhone () {
        if (this.phone) {
          if (this.phone.length === 11) {
            return true
          } else {
            this.toastShortMsg("手机号不合法")
          }
        } else {
          this.toastShortMsg("请输入手机号")
        }
        return false
      },
      checkVCode () {
        if (this.vcode) {
          if (this.vcode.length === 6) {
            return true
          } else {
            this.toastShortMsg("请输入6位验证码")
          }
        } else {
          this.toastShortMsg("请输入验证码")
        }
        return false
      }
    }
  }
</script>

<style scoped lang="less">
  .help {
    font-size: 48px;
    float: right;
    margin-right: 6px;
    margin-top: 35px;
    color: #61d6cb;
    padding: 50px;
  }

  .top {
    height: 60px;
    flex-shrink: 1;
  }

  .title {
    /*margin-top: 112px;*/
    margin-left: 41px;
    flex-shrink: 1;

    span {
      font-size: 28px;
      color: #424242;
    }
  }

  .sub-title {
    /*margin-top: 112px;*/
    margin-left: 41px;
    flex-shrink: 1;

    span {
      font-size: 16px;
      color: #424242;
    }
  }

  .phone {
    color: #424242;
    margin-top: 60px;
    margin-left: 38px;

    .prefix {
      font-size: 15px;
    }

    .number {
      font-size: 17px;
      width: 200px;
    }

    .text {
      margin-top: 17px;
      font-size: 38px;
      color: #9f9d9d;
    }
  }

  .vcode {

    height: 30px;
    margin-left: 42px;
    display: flex;
    margin-top: 8px;
    width: 296px;
    line-height: 30px;
    flex-shrink: 0;

    input {
      ::-webkit-input-placeholder { /* WebKit, Blink, Edge */
        color: #9f9d9d;
      }

      :-moz-placeholder { /* Mozilla Firefox 4 to 18 */
        color: #9f9d9d;
      }

      ::-moz-placeholder { /* Mozilla Firefox 19+ */
        color: #9f9d9d;
      }

      :-ms-input-placeholder { /* Internet Explorer 10-11 */
        color: #9f9d9d;
      }

      font-size: 17px;
      color: black;
      width: 150px;
      flex-grow: 1;
      padding: 0 0;
    }

    .vcode-button {
      border-radius: 5px;
      border: 1px solid #929090;
      background-color: transparent;
      width: 100px;
      height: 30px;
      font-size: 14px;
      vert-align: middle;
      color: #929090;
    }
  }

  .line1 {
    margin-top: 8px;
    height: 1px;
    width: 296px;
    background-color: #b3b3b3;
    margin-left: 40px;
    flex-shrink: 0;
  }

  .line2 {
    margin-top: 7px;
    height: 1px;
    width: 296px;
    background-color: #b3b3b3;
    margin-left: 40px;
    flex-shrink: 0;
  }

  .top-button {
    height: 80px;
  }

  .button {
    width: 298px;
    height: 46px;
    background-color: #83d3cb;
    background-repeat: no-repeat;
    background-size: contain;
    font-size: 17px;
    color: #fbfafa;
    text-align: center;
    border-radius: 23px;
  }

  .bottom-text-top {
    flex: 1;
  }

  .bottom-text {
    font-size: 13px;
    color: #9f9d9d;
    text-align: center;
    width: 100%;
    margin-bottom: 23px;

    span:nth-child(2) {
      color: #61d6cb;
    }

    span:nth-child(3) {
      color: #61d6cb;
    }
  }

  .login-button {
    flex-shrink: 0;
  }
</style>
