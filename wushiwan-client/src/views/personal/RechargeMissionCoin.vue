/**
* @Author: limeng
* @Date: 2019-08-18 21:40
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="充值任务币"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>
    <div class="under-nav flex1 height0 recharge">
      <van-cell-group>
        <van-field label="充值金额" v-model="amount" placeholder="请输入充值金额">
          <span slot="right-icon">元</span>
        </van-field>
        <van-cell title="充值方式" value="支付宝" is-link/>
      </van-cell-group>
      <p class="risk">提示：支付过程中，如遇"本次交易存在风险"，选择【继续付款】即可，此提醒仅为支付宝尽到的风险提示义乌，多针对首次支付的用户。</p>
      <button @click="onRechargePressed">充&nbsp;&nbsp;&nbsp;值</button>
      <p>提示：</p>
      <p>1、充值任务币最低1元</p>
      <p>2、充值暂时只支持支付宝，后期会开通微信充值</p>
    </div>
  </div>
</template>

<script>
  import Decimal from "decimal.js"
  import { RECHARGE_MISSION_COIN_URL } from "@/config/host"
  import { alipayForApp, alipayForBrowser } from "@/service/pay"
  import { Dialog } from "vant"

  export default {
    name: "RechargeMissionCoin",
    data () {
      return {
        amount: null
      }
    },
    watch: {
      amount (newValue, oldValue) {
        this.amount = newValue.replace(/[^0-9]/ig, "")
      }
    },
    mounted () {

    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      async onRechargePressed () {
        if (!this.amount || parseInt(this.amount) < 1) {
          this.toastShortMsg("请输入正确的充值金额")
          return
        }
        let number = new Decimal(this.amount)
        const result = await this.httpPostWithLoading(RECHARGE_MISSION_COIN_URL, {
          amount: number.times(100).toNumber()
        })
        if (result.success) {
          if (this.isApp()) {
            alipayForApp(result.data, () => {
              this.$store.dispatch("refreshUserInfo")
              Dialog.alert({
                message: "充值任务币成功"
              }).then(() => {

              })
            })
          } else {
            alipayForBrowser(result.data)
          }
        }
      }
    }
  }
</script>

<style scoped lang="less">
  .recharge {
    button {
      width: 300px;
      height: 36px;
      line-height: normal;
      border: solid 1px #81d4cb;
      border-radius: 4px;
      color: white;
      font-size: 16px;
      background-color: #81d4cb;
      text-align: center;
      margin: 20px auto;
      display: block;
    }

    .risk {
      margin-top: 20px;
      color: #ff4444;
    }

    p {
      margin-left: 30px;
      margin-right: 30px;
      color: #969799;
    }
  }
</style>
