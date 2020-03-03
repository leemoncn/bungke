/**
* @Author: limeng
* @Date: 2019-03-31 21:35
* @Description:
*/
<template>
  <div class="column-view">
    <div ref="topBar">
      <van-nav-bar
        title="个人"
        left-text="切换账号"
        right-text="刷新"
        @click-left="onLogoutButtonPressed"
        @click-right="onRefreshPresed"
        fixed>
      </van-nav-bar>
    </div>
    <div class="under-nav overflow-auto tab-main">
      <div class="" v-if="user">
        <div class="top-bg">
          <div class="main">
            <img :src="user.headImgUrl?user.headImgUrl:defaultHeadImg" class="head" alt=""/>
            <div class="user-info">
              <div style="display: flex;align-items: center;flex: 1">
                <span class="name">{{user.nickname}}</span>
                <div class="shiming">
                  <span>实名认证</span>
                </div>
              </div>
              <div class="user-id">
                <span>用户ID: </span>
                <span>{{user.id}}</span>
                <span>{{partner.name}}</span>
              </div>
              <div class="phone">
                <span>手机号: </span>
                <span>{{user.phone}}</span>
              </div>
            </div>
            <!--<van-icon name="arrow" class="arrow" color="white"/>-->
          </div>
        </div>
        <div class="middle-bg">
          <div class="left">
            <div class="row flex-row flex-align-center earning">
              <span>{{user.earning / 100}}元</span>
              <div style="flex: 1"></div>
              <button class="blue-bg button" @click="()=>{$router.push({'name':'WithdrawalEarning'})}">提现</button>
            </div>
            <div class="row flex-row flex-align-center">
              <p class="info">收入分红</p>
              <div style="flex: 1"></div>
              <button class="orange-bg button" @click="()=>{$router.push({ name: 'CurrencyDetail' })}">明细</button>
            </div>
            <div class="in-review flex-row">
              <span>{{user.moneyInReview / 100}}元</span>
            </div>
            <div class="row flex-row">
              <span class="info">系统派发中</span>
            </div>
          </div>
          <div class="line"></div>
          <div class="right">
            <div class="div">
              <div class="row flex-row flex-align-center deposit">
                <span>{{user.deposit / 100}}{{isInReview()?"":"元"}}</span>
                <div style="flex: 1"></div>
                <button class="blue-bg" @click="()=>{$router.push({'name':'RechargeDeposit'})}" v-show="!isInReview()">
                  充值
                </button>
              </div>
              <div class="row flex-row flex-align-center">
                <span class="info">{{isInReview()?"信用分":"保证金"}}</span>
                <div style="flex: 1"></div>
                <button class="red-bg" @click="()=>{$router.push({'name':'WithdrawalDeposit'})}" v-show="!isInReview()">
                  退款
                </button>
              </div>
              <div class="row-line"></div>
              <div class="row flex-row flex-align-center mission-coin">
                <span>{{user.missionCoin / 100}}元</span>
                <div style="flex: 1"></div>
                <button class="blue-bg" @click="()=>{$router.push({'name':'RechargeMissionCoin'})}">充值</button>
              </div>
              <div class="row flex-row flex-align-center">
                <span class="info">任务币</span>
                <div style="flex: 1"></div>
                <button class="blue-bg button" @click="()=>{$router.push({'name':'WithdrawalMissionCoin'})}">提现</button>
              </div>
            </div>
          </div>
        </div>
        <div class="flex-column bottom-bg">
          <div class="flex-row flex-align-center header">
            <span>工具与服务</span>
          </div>
          <div class="bottom">
            <div class="flex-row" style="justify-content: space-around">
              <div class="flex-column flex-align-center flex1" @click="()=>{$router.push({ name: 'QRCode' })}">
                <img src="../../assets/img/me-icon1.png" alt="">
                <span>推广码</span>
              </div>
              <div class="flex-column flex-align-center flex1" @click="()=>{$router.push({ name: 'Subordinate' })}">
                <img src="../../assets/img/me-icon2.png" alt="">
                <span>我的团队</span>
              </div>
              <div class="flex-column flex-align-center flex1" @click="()=>{$router.push({ name: 'Partner' })}">
                <img src="../../assets/img/me-icon3.png" alt="">
                <span>合作商</span>
              </div>
              <div class="flex-column flex-align-center flex1"
                   @click="()=>{$router.push({ name: 'Shop',query: { id: user.id,name: user.nickname } })}">
                <img src="../../assets/img/me-icon4.png" alt="">
                <span>我的店铺</span>
              </div>
            </div>
            <div class="flex-row bottomDiv" style="justify-content: space-around">
              <div class="flex-column flex-align-center flex1" @click="()=>{$router.push({ name: 'AlipayAccount' })}">
                <img src="../../assets/img/me-icon5.png" alt="">
                <span>支付宝</span>
              </div>
              <div class="flex-column flex-align-center flex1" @click="()=>{$router.push({ name: 'Fans' })}">
                <img src="../../assets/img/me-icon6.png" alt="">
                <span>粉丝关注</span>
              </div>
              <div class="flex-column flex-align-center flex1" @click="()=>{$router.push({ name: 'Question' })}">
                <img src="../../assets/img/me-icon7.png" alt="">
                <span>常见问题</span>
              </div>
              <div class="flex-column flex-align-center flex1" @click="advicePressed">
                <img src="../../assets/img/me-icon8.png" alt="">
                <span>意见反馈</span>
              </div>
            </div>
          </div>
        </div>

        <p class="bottom-text">©帮客版权所有&nbsp;&nbsp;&nbsp;{{appVersion?`客户端版本: ${appVersion} `:" "}}{{wgtVersion?`软件版本:
          ${wgtVersion}`:""}}</p>
        <p class="bottom-text">客服QQ {{kefu_qq}} 客服微信 {{kefu_wechat}}</p>
      </div>
    </div>
  </div>
</template>

<script>
  import { RECHARGE_DEPOSIT_URL } from "@/config/host"
  import { mapGetters } from "vuex"
  import { Dialog } from "vant"
  import { wechatLogout } from "@/service/oauth"
  import { KEFU_QQ, KEFU_WECHAT } from "@/config/define"

  export default {
    name: "Personal",
    data () {
      return {
        kefu_qq: KEFU_QQ,
        kefu_wechat: KEFU_WECHAT,
        appVersion: null,
        wgtVersion: null
      }
    },
    mounted () {
      if (this.isApp() && process.env.VUE_APP_DIST) {
        this.appVersion = plus.runtime.version
        let _this = this
        plus.runtime.getProperty(plus.runtime.appid, async function (wgtinfo) {
          _this.wgtVersion = wgtinfo.version
        })
      }
    },
    computed: {
      ...mapGetters([
        "user", "partner"
      ])
    },
    methods: {
      onLogoutButtonPressed () {
        Dialog.confirm({
          message: "切换账号？"
        }).then(() => {
          wechatLogout()
          this.$store.dispatch("logout").finally(() => {
            this.$router.replace({ name: "ThirdLogin" })
          })
        })
      },
      onRefreshPresed () {
        if (this.$store.getters.isLogin) {
          this.$store.dispatch("refreshUserInfo")
          this.toastShortMsg("刷新用户信息完成")
        }
      },
      async onRechangeDepositPressed () {
        const result = await this.httpPostWithLoading(RECHARGE_DEPOSIT_URL, {
          amount: 1
        })
        if (result.success) {
          let form = result.data
          const div = document.createElement("div")
          div.innerHTML = form
          document.body.appendChild(div)
          document.forms[0].submit()
        }
      },
      advicePressed () {
        this.$router.push({ name: "Advice" })
      }
    }
  }
</script>

<style scoped lang="less">
  .top-bg {
    width: 100%;
    height: 120px;
    align-items: center;
    display: flex;
    background-color: #81d4cb;

    .main {
      display: flex;
      width: 100%;
      align-items: center;
      margin-bottom: 20px
    }

    .head {
      width: 58px;
      height: 58px;
      border-radius: 29px;
      margin: 0 18px 0;
    }

    .user-info {
      flex: 1;
      /*display: flex;*/
      /*flex-direction: column;*/

      .name {
        color: white;
        font-size: 16px;
      }

      .shiming {
        height: 15px;
        width: 64px;
        display: none;
        align-items: center;
        justify-content: center;
        background-color: #fffffff2;
        border-radius: 8px;

        span {
          color: #81d4cb;
          font-size: 12px;
        }
      }

      .user-id {
        /*display: flex;*/
        /*align-items: center;*/
        font-size: 14px;
        margin: 4px 0 4px;

        span {
          color: white;
          font-size: 14px;
        }

        span:nth-child(3) {
          margin-left: 14px;
        }
      }

      .phone {
        /*display: flex;*/
        /*vertical-align: top;*/
        /*align-items: end;*/
        /*font-size: 0;*/
        font-size: 14px;

        span {
          color: white;
          font-size: 14px;
          vertical-align: bottom;
        }
      }
    }

    .arrow {
      margin-right: 14px;
    }
  }

  .middle-bg {
    background-color: white;
    margin: -20px 7px 23px;
    padding: 12px 26px 12px 26px;
    border-radius: 10px;
    display: flex;

    button {
      color: white;
      font-size: 14px;
      border-radius: 10px;
      line-height: normal;
      width: 50px;
      height: 21px;
    }

    .blue-bg {
      background-color: #81d4cb;
    }

    .orange-bg {
      background-color: #e49558;
    }

    .red-bg {
      background-color: #e46d69;
    }

    .row {
      align-items: center;
    }

    .left {
      flex: 1;
      padding: 8px 0;

      .earning {
        margin-bottom: 12px;

        span {
          color: #424242;
          font-size: 17px;
        }
      }

      .info {
        color: #424242;
        font-size: 14px;
      }

      .in-review {
        margin-right: 33px;
        background-color: #f3f3f3;
        border-radius: 5px;
        padding-left: 8px;
        padding-top: 4px;
        padding-bottom: 4px;
        margin-top: 36px;
        margin-bottom: 12px;

        span {
          color: #9f9d9d;
          font-size: 14px;
        }
      }
    }

    .line {
      width: 1px;
      height: auto;
      background-color: #b3b3b3;
      margin-left: 10px;
      margin-right: 10px;
    }

    .right {
      flex: 1;
      padding: 8px 0;

      .div {
        padding-left: 10px;
      }

      .deposit {
        margin-bottom: 16px;
        white-space: nowrap;
      }

      span {
        color: #424242;
        font-size: 14px;
      }

      .mission-coin {
        margin-bottom: 18px;
      }

      .row-line {
        height: 1px;
        background-color: #b3b3b3;
        margin: 19px 0;
      }
    }
  }

  .bottom-bg {
    background-color: white;
    margin: 24px 7px 16px;
    border-radius: 10px;

    .header {
      border-bottom: 1px solid #b3b3b3;

      span {
        margin-left: 16px;
        margin-top: 13px;
        margin-bottom: 13px;
        font-size: 12px;
        color: #424242;
      }
    }

    .bottom {
      padding-top: 24px;
      padding-bottom: 24px;

      img {
        width: 24px;
        height: 24px;
      }

      span {
        margin-top: 12px;
        font-size: 14px;
        color: #424242;
      }

      .bottomDiv {
        margin-top: 30px;
      }
    }
  }

  .bottom-text {
    color: #b2b2b2;
    font-size: 12px;
    margin: 0 auto;
    text-align: center;
  }
</style>
