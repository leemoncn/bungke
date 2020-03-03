/**
* @Author: limeng
* @Date: 2019-03-31 11:15
* @Description:
*/
<template>
  <div>
    <!--<span class="help">帮助</span>-->
    <!--<div class="title">-->
    <!--<span>欢迎登录{{appName}}</span>-->
    <!--</div>-->
    <!--<div class="phone">-->
    <!--<span class="prefix">+86 >  </span>-->
    <!--<input class="number">-->
    <!--<div class="line"/>-->
    <!--<div>-->
    <!--<span class="text">未注册的手机号验证后自动创建账户</span>-->
    <!--</div>-->
    <!--</div>-->
    <input v-model="username" placeholder="请输入账号" style="background-color: aquamarine">
    <div style="text-align: center">
      <button class="button" @click="onLoginButtonPressed">获取短信验证码</button>
    </div>
    <!--<div class="bottom-text">-->
    <!--<span>登录代表你已同意</span>-->
    <!--<span>{{appName}}用户协议、</span>-->
    <!--<span>隐私政策</span>-->
    <!--</div>-->
  </div>
</template>

<script>
  import { APP_NAME } from "@/config/define"

  export default {
    name: "Login",
    data: function () {
      return {
        username: "limeng1",
        password: "123456",
        appName: APP_NAME,
        wechatResult: null
      }
    },
    mounted () {
      this.$navigation.cleanRoutes()
    },
    methods: {
      async onLoginButtonPressed () {
        let _this = this
        console.log("result = ", JSON.stringify(this.wechatResult))
        plus.oauth.getServices(function (services) {
          if (!_this.wechatResult) {
            for (let s of services) {
              if (s["id"] === "weixin") {
                _this.wechatResult = s
                break
              }
            }
          }

          if (!_this.wechatResult) {
            console.log("当前环境不支持微信登录")
            return
          }
          // {
          //   "target": {
          //   "id": "weixin",
          //     "description": "微信",
          //     "authResult": {
          //     "access_token": "22_QSG6Hl2cwt3tIfJrhnvEIHPNkFd2LegeRNxP-1VDVFvbPjEeSZFho1Zw-juCgaaCh2r7GyPu9ngw73awBc1ny_o1s9pKxCTUkxBfHptiw8I",
          //       "code": "043PP7O92GczlK0HwOO92NPON92PP7Oy",
          //       "expires_in": 7200,
          //       "openid": "oRrdQtytviZG05A-R_GV_DsfaFjw",
          //       "refresh_token": "22_p-mfZyCwgwq6IkPV0HpMO4dfZUYNWuqiut0XCzcri8ArBMwexI3w9TR-QrMzbK9Stkm3IxRGfQgiL-ksAbnGecuXHX_lHlPif9WPOYf74IQ",
          //       "scope": "snsapi_userinfo",
          //       "unionid": "oU5Yyt99po2Rs0BbQj_7jDYpSKPE"
          //   }
          // }
          // }
          console.log("result = ", JSON.stringify(_this.wechatResult))
          if (!_this.wechatResult.authResult) {
            _this.wechatResult.login(function (e) {
              console.log("登录认证成功!", JSON.stringify(e))
              _this.wechatResult = e.target
            }, function (e) {
              console.log("登录认证失败: " + JSON.stringify(e))
            })
          } else {
            console.log("已经登录认证!")
          }
        }, function (e) {
          console.log("plus err= ", e)
        })
        // const { success } = await this.$store.dispatch("login", {
        //   account: this.username,
        //   password: this.password,
        //   typePropertyId: 1
        // })
        // if (success) {
        //   this.$store.commit("changeActiveItemIndex", 0)
        //   this.$store.commit("clearProperty")
        //   this.$router.replace("/")
        // }
      },
      onTestButtonPressed () {
        console.log("test")
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

  .title {
    margin-top: 322px;
    margin-left: 119px;

    span {
      font-size: 80px;
      color: #424242;
    }
  }

  .phone {
    color: #424242;
    margin-top: 174px;
    margin-left: 112px;

    .line {
      margin-top: 30px;
      height: 1px;
      width: 851px;
      background-color: #b3b3b3;
    }

    .prefix {
      font-size: 42px;
    }

    .number {
      font-size: 48px;
      width: 600px;
      height: 100px;
    }

    .line {
      margin-top: 30px;
      height: 1px;
      width: 851px;
      background-color: #b3b3b3;
    }

    .text {
      margin-top: 17px;
      font-size: 38px;
      color: #9f9d9d;
    }
  }

  .button {
    margin-top: 79px;
    width: 858px;
    height: 132px;
    line-height: normal;
    background-color: #83d3cb;
    background-size: cover;
    font-size: 48px;
    color: #fbfafa;
  }

  .bottom-text {
    font-size: 38px;
    color: #9f9d9d;
    text-align: center;
    position: absolute;
    bottom: 66px;
    width: 100%;

    span:nth-child(2) {
      color: #61d6cb;
    }

    span:nth-child(3) {
      color: #07d6fa;
    }
  }
</style>
