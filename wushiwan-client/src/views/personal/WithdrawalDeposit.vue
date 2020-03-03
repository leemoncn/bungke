/**
* @Author: limeng
* @Date: 2019-08-18 21:40
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="保证金退款"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>
    <div class="under-nav flex1 height0 recharge">
      <van-cell-group>
        <van-cell title="保证金余额" :value="deposit"/>
        <van-cell title="提现方式" value="支付宝" is-link/>
      </van-cell-group>
      <button @click="onRechargePressed">提&nbsp;&nbsp;&nbsp;现</button>
      <p>提示：
        1、保证金退还，提交申请后20个工作日（即为除去周六周日及法定节假日后的天数），经审核无违规行为及投诉，保证金将全额退还，退还时收取1%的手续费
        注：如申请20个工作日后未到账，请在工作日联系客服QQ {{kefu_qq}} 或客服微信 {{kefu_wechat}} 协助您查询处理
      </p>
    </div>
  </div>
</template>

<script>
  import { WITHDRAWAL_DEPOSIT_URL } from "@/config/host"
  import { KEFU_QQ, KEFU_WECHAT } from "@/config/define"
  import { Dialog } from "vant"

  export default {
    name: "WithdrawalDeposit",
    data () {
      return {
        kefu_qq: KEFU_QQ,
        kefu_wechat: KEFU_WECHAT
      }
    },
    watch: {},
    mounted () {

    },
    computed: {
      deposit () {
        return this.$store.getters.user.deposit / 100.0 + "元"
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      async onRechargePressed () {
        if (!this.$store.getters.user.deposit) {
          this.toastMsg("当前没有保证金")
          return
        }
        console.log("this.$store.getters.user.deposit", this.$store.getters.user.deposit)
        if (!this.$store.getters.user.alipay) {
          Dialog.confirm({
            message: "提现需绑定支付宝账号",
            confirmButtonText: "去绑定"
          }).then(async () => {
            this.$router.push({ name: "AlipayAccount" })
          })
          return
        }
        const result = await this.httpPostWithLoading(WITHDRAWAL_DEPOSIT_URL)
        if (result.success) {
          this.toastMsg("提交成功，申请将在1个工作日审核完成")
          this.$store.commit("updateUser", result.data)
        }
      }
    }
  }
</script>

<style lang="less">
  .withdrawal-mission-coin {
    .van-field__error-message {
      color: #969799
    }
  }
</style>

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
      white-space: pre-wrap;
      margin-left: 10px;
      margin-right: 10px;
      color: #969799;
    }
  }
</style>
