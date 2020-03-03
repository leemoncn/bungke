/**
* @Author: limeng
* @Date: 2019-09-01 21:59
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="合作商"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>
    <div class="under-nav flex1 height0 overflow-auto" style="width: 100%" v-if="currentPartnerName">
      <div class="my-partner flex-row flex-align-center">
        <span class="left-title">合作商级别: </span>
        <span class="my">{{$store.getters.partner.name}}</span>
        <span class="my-time">{{$store.getters.partner.endTime?"有效期: " + $store.getters.partner.endTime:""}}</span>
      </div>
      <p class="partner-info">
        注：合作商一旦购买成功，便不能退改，请考虑成熟后再行购买；同级别续费，时间自动累计顺延；升级级别，原合作商的剩余时间将作废。
      </p>
      <div class="main">
        <p class="title">
          成为合作商，享受专享权益：
        </p>
        <table border="0"
               style="width: 100%;border-collapse:collapse;"
               cellpadding="0"
               cellspacing="0">
          <thead>
          <tr>
            <th width="20%">级别</th>
            <th width="20%">交易手续费%</th>
            <th width="20%">任务币提现手续费%</th>
            <th width="20%">发布任务最低服务费/个</th>
            <th width="20%">免费推荐</th>
          </tr>
          </thead>

          <tbody>
          <tr v-for="(item,index) in partnerList" :key="index">
            <td>{{item.name}}</td>
            <td>{{item.feePercent}}</td>
            <td>{{item.missionPaymentPercent}}</td>
            <td>{{item.minFeePrice / 100}}元</td>
            <td>{{item.adHour > 0?item.adHour + "小时/日":"-"}}</td>
          </tr>
          </tbody>
        </table>
        <div class="bottom">
          <div v-for="(item,index) in partnerList" :key="index">
            <template v-if="index > 0"><!--不显示普通合作商-->
              <div class="flex-row flex-align-center title">
                <img :src="getPartnerRankImg(index)">
                <span>{{item.name}}</span>
              </div>
              <div class="price flex-row flex-align-center">
                <span style="margin-left: 12px">月卡：</span>
                <span>{{item.mouthPrice / 100}}元/月</span>
                <div class="flex1"></div>
                <button style="margin-right: 12px" @click="buyPartner(item,false)">购买</button>
              </div>
              <div class="price flex-row flex-align-center">
                <span style="margin-left: 12px">年卡：</span>
                <span>{{item.yearPrice / 100}}元/月</span>
                <div class="flex1"></div>
                <button style="margin-right: 12px" @click="buyPartner(item,true)">购买</button>
              </div>
            </template>
          </div>
          <div>
            <p>合作商权益：</p>
            <p>1、合作商一天可以提现任务币三次，非合作商任务币7天内只能提现一次，任务币提现需人工审核，审核时间为工作日9:00-17:00，非工作日不能提现</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { BUY_PARTNER_URL, PARTNER_INFO_URL } from "@/config/host"
  import { Dialog } from "vant"
  import { alipayForApp, alipayForBrowser } from "@/service/pay"

  export default {
    name: "Partner",
    data () {
      return {
        currentPartnerName: null,
        partnerList: []
      }
    },
    async mounted () {
      const result = await this.httpPostWithLoading(PARTNER_INFO_URL)
      if (result.success) {
        this.currentPartnerName = result.data.name
        this.partnerList = result.data.list
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      getPartnerRankImg (index) {
        return require("@/assets/img/partner" + index + ".png")
      },
      async buyPartner (item, isYear) {
        if (item.level < this.$store.getters.partner.level) {
          this.toastMsg("合作商只能升级，不能降级")
          return
        }
        Dialog.confirm({
          title: "提示",
          messageAlign: "left",
          message: `确认购买<${item.name}>吗？`
        }).then(async () => {
          const result = await this.httpPostWithLoading(BUY_PARTNER_URL, {
            isYear: isYear,
            partnerId: item.id
          })
          if (result.success) {
            if (this.isApp()) {
              alipayForApp(result.data, () => {
                this.$store.dispatch("refreshUserInfo")
                Dialog.alert({
                  message: "充值合作商成功"
                }).then(() => {

                })
              })
            } else {
              alipayForBrowser(result.data)
            }
          }
        })
      }
    }
  }
</script>

<style scoped lang="less">
  table {
    margin: 10px 0;

    thead {
      th {
        font-size: 12px;
        color: #ffffff;
        background-color: #81d4cb;
      }
    }

    tbody {
      tr {
        font-size: 12px;
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

  .partner-price {
    div {
      height: 50px;
    }
  }

  .my-partner {
    width: 354px;
    height: 41px;
    background-color: #ffffff;
    border-radius: 5px;
    margin: 6px auto 0 auto;
    vertical-align: middle;

    .left-title {
      font-size: 15px;
      color: #424242;
      margin-left: 12px;

    }

    .my {
      font-size: 16px;
      color: #81d4cb;
      margin-left: 18px;
    }

    .my-time {
      font-size: 12px;
      color: #81d4cb;
      margin-left: 10px;
    }
  }

  .partner-info {
    font-size: 12px;
    line-height: 14px;
    color: #bf1a1a;
    margin: 14px 15px;
  }

  .main {
    background-color: white;
    padding: 8px 10px;

    .title {
      font-size: 14px;
      color: #424242;
    }

    .bottom {

      div:not(:first-child) {
        margin-top: 10px;
      }

      .title {
        span {
          font-size: 14px;
          color: #424242;
          margin-left: 7px;
        }

        img {
          width: 20px;
          height: 18px;
        }
      }

      .price {
        width: 100%;
        height: 29px;
        background-color: #81d4cb;
        border-radius: 5px;
        margin-top: 8px;

        span {
          font-size: 14px;
          color: #ffffff;
        }

        button {
          width: 42px;
          height: 19px;
          background-color: #e49558;
          border-radius: 5px;
          font-size: 12px;
          color: #ffffff;
        }
      }
    }
  }

</style>
