/**
* @Author: limeng
* @Date: 2019-08-18 21:40
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="充值保证金"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>
    <div class="under-nav flex1 height0 recharge overflow-auto">
      <van-cell-group>
        <van-field label="充值金额" v-model="amount" placeholder="请输入充值金额">
          <span slot="right-icon">元</span>
        </van-field>
        <van-cell title="充值方式" value="支付宝" is-link/>
      </van-cell-group>
      <button @click="onRechargePressed">充&nbsp;&nbsp;&nbsp;值</button>
      <p>提示：</p>
      <p>1、保证金最低金额为1000元，可以大于1000元。保证金越高，您发布任务的可信度越高，有利于您快速的完成任务</p>
      <p>2、缴纳保证金的商家发布的任务将显示特殊标识，提高商家信誉度，增加成交机会</p>
      <p>3、部分金融类任务，只有缴纳保证金才可以发布</p>
      <p>4、如果被投诉，平台将根据相关规定和证据进行仲裁，并依照相应任务金额及用户损失进行保证金处罚：
        1)发布的任务如存在私下交易、私下发布其他任务、恶意篡改任务链接、二维码等，但任务内容不存在违规违法行为，视投诉情况及任务单价范围，将处以200~500元之间的罚款，罚款直接从保证金中扣除
        2)通过加粉或通过平台渠道联系到用户，私下进行推广违规任务，如：推广同类平台、赌博类、涉黄类、挂机软件类、投资类等造成大量投诉但并未给用户造成严重的经济损失，将处以500~1000元之间的罚款，罚款直接从保证金中扣除
        3)通过发布加粉类、投资理财类、投票类、关注类等任务聚集用户，私下实施诈骗或发布违法任务，将罚没全部保证金，并永久冻结账户
        4)冒充平台工作人员，收取会费或私下以本平台名义发布任务的，将罚没全部保证金，并永久冻结账户
        5)态度恶劣，不能主动承认错误、找各种借口和理由拒不悔改、辱骂、威胁平台及工作人员的将从重加倍处罚，严重者将交由公安机关处理</p>
      <p>5、保证金可申请退还，提交申请后20个工作日（即为除去周六周日及法定节假日后的天数），经审核无违规行为及投诉，保证金将全额退还，退还时收取1%的手续费
        注：如申请20个工作日后未到账，请在工作日联系客服QQ {{kefu_qq}} 客服微信 {{kefu_wechat}} 协助您查询处理</p>
    </div>
  </div>
</template>

<script>
  import Decimal from "decimal.js"
  import { RECHARGE_DEPOSIT_URL } from "@/config/host"
  import { KEFU_QQ, KEFU_WECHAT } from "@/config/define"
  import { alipayForApp, alipayForBrowser } from "@/service/pay"
  import { Dialog } from "vant"

  export default {
    name: "RechargeMissionCoin",
    data () {
      return {
        amount: null,
        kefu_qq: KEFU_QQ,
        kefu_wechat: KEFU_WECHAT
      }
    },
    watch: {
      amount (newValue, oldValue) {
        this.amount = newValue.replace(/[^0-9]/ig, "")
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      async onRechargePressed () {
        if (!this.amount) {
          this.toastShortMsg("请输入正确的充值金额")
          return
        }
        let number = new Decimal(this.amount)
        if (number < 1000) {
          this.toastShortMsg("保证金最低金额为1000元")
          return
        }
        const result = await this.httpPostWithLoading(RECHARGE_DEPOSIT_URL, {
          amount: number.times(100).toNumber()
        })
        if (result.success) {
          if (this.isApp()) {
            alipayForApp(result.data, () => {
              this.$store.dispatch("refreshUserInfo")
              Dialog.alert({
                message: "充值保证金成功"
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
      margin-left: 10px;
      margin-right: 10px;
      color: #969799;
      white-space: pre-wrap;
    }
  }
</style>
