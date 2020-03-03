/**
* @Author: limeng
* @Date: 2019-12-02 21:04
* @Description:
*/
<template>
  <div>
    <van-nav-bar
      title="办理返现"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed"/>
    <div class="flex1 height0 overflow-auto flex-column">
      <div class="under-nav flex-row flex-justify-center">
        <table border="0"
               style="border-collapse:collapse;"
               cellpadding="0"
               cellspacing="0">
          <thead>
          <tr>
            <th width="50%">银行名称</th>
            <th width="50%">收益报价(元)</th>
          </tr>
          </thead>

          <tbody>
          <tr v-for="(item,index) in list" :key="index">
            <td>{{item.name}}</td>
            <td>{{item.price}}</td>
            <!--<td>{{item.name}}</td>-->
            <!--<td>{{item.feePercent}}</td>-->
            <!--<td>{{item.missionPaymentPercent}}</td>-->
            <!--<td>{{item.minFeePrice / 100}}元</td>-->
            <!--<td>{{item.adHour > 0?item.adHour + "小时/日":"-"}}</td>-->
          </tr>
          </tbody>
        </table>
      </div>
      <div class="info">
        <p>办卡说明：</p>
        <p>1、收到信用卡激活后，请在工作日联系客服QQ {{kefu_qq}} 或客服微信 {{kefu_wechat}} 领取返现奖励</p>
      </div>
    </div>
  </div>
</template>

<script>
  import { KEFU_QQ, KEFU_WECHAT } from "@/config/define"

  export default {
    name: "CreditCard",
    data () {
      return {
        kefu_qq: KEFU_QQ,
        kefu_wechat: KEFU_WECHAT,
        list: [{
          name: "招商储蓄卡",
          price: 10
        }, {
          name: "平安银行",
          price: 60
        }, {
          name: "兴业银行-核卡",
          price: 50
        }, {
          name: "交通银行-首刷版",
          price: 130
        }, {
          name: "浦发银行",
          price: 130
        }, {
          name: "上海银行",
          price: 100
        }, {
          name: "光大裕福联名卡",
          price: 130
        }, {
          name: "中信银行",
          price: 100
        }, {
          name: "中原银行",
          price: 10
        }, {
          name: "小赢钱包虚拟卡",
          price: 105
        }, {
          name: "招商银行",
          price: 45
        }, {
          name: "民生银行",
          price: 40
        }, {
          name: "华夏银行",
          price: 50
        }, {
          name: "招商银行学生卡",
          price: 20
        }]
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      }
    }
  }
</script>

<style scoped lang="less">
  table {
    width: 80%;
    margin: 20px 0;

    thead {
      th {
        font-size: 16px;
        color: #ffffff;
        background-color: #81d4cb;
      }
    }

    tbody {
      tr {
        font-size: 14px;
        text-align: center;
        color: #424242;
        height: 24px;
      }

      tr:nth-child(odd) {
        background: #ddfbf8;
      }

      tr:nth-child(even) {
        background: #f0fffd;
      }
    }
  }

  .info {
    margin: 0 20px;
  }
</style>
