/**
* @Author: limeng
* @Date: 2019-10-23 20:37
* @Description: 我的下线
*/
<template>
  <div class="">
    <van-nav-bar
      title="收支明细"
      left-text="返回"
      fixed ref="topBar"
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>

    <div class="under-nav flex1 height0 overflow-auto flex-column">
      <!--<div class="top" ref="top">-->
      <!--<div class="info flex-row">-->
      <!--<img :src="user.headImgUrl?user.headImgUrl:defaultHeadImg">-->
      <!--<div class="flex-column name-info">-->
      <!--<p class="name">-->
      <!--{{user.nickname}}-->
      <!--</p>-->
      <!--<div class="flex1"></div>-->
      <!--<p class="subordinates" v-if="agency">我的团队: {{subordinateList.length}}人</p>-->
      <!--</div>-->
      <!--</div>-->
      <!--<div class="description" v-show="agency">-->
      <!--<span class="normal">下线充值您将获得充值金额</span>-->
      <!--<span class="important" v-if="agency">&nbsp;&nbsp;{{agency.rechargeByApp}}%&nbsp;&nbsp;</span>-->
      <!--<span class="normal">的奖励，下线提现您将获得提现金额</span>-->
      <!--<span class="important" v-if="agency">&nbsp;&nbsp;{{agency.withdrawByUser}}%&nbsp;&nbsp;</span>-->
      <!--<span class="normal">的奖励！</span>-->
      <!--</div>-->
      <!--</div>-->
      <div class="overflow-auto" :style="listHeight">
        <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
          <van-list @load="onListLoad" v-model="isLoading"
                    :finished="isFinished"
                    finished-text=""
                    :style="{minHeight: listHeight.height}"
                    error-text="请求失败，点击重新加载"
                    :error.sync="isError">
            <van-cell v-for="(item,index) in currencyChangeList" :key="index" v-show="item.typePropertyId !== 41">
              <div class="cell flex-row flex-align-center">
                <span class="title">{{getTitle(item.reasonPropertyId)}}</span>
                <span class="price">{{fenToYuan(item.record,item.typePropertyId)}}</span>
                <span class="time right">{{getTime(item.createTime)}}</span>
              </div>
            </van-cell>
          </van-list>
        </van-pull-refresh>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapGetters } from "vuex"
  import { CURRENCY_DETAIL_URL } from "@/config/host"
  import { getPropertyById } from "@/service/property"

  export default {
    name: "CurrencyDetail",
    data () {
      return {
        tableData: [],
        isRefreshing: false,
        isLoading: false,
        isError: false,
        isFinished: false,
        listHeight: {
          height: "0"
        },
        currencyChangeList: [],
        pageNo: 0,
        pageSize: 20
      }
    },
    mounted () {
      this.listHeight.height = window.innerHeight - this.$refs.topBar.offsetHeight + "px"
      // this.loadData()
    },
    computed: {
      ...mapGetters([
        "user"
      ])
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      fenToYuan (fen, type) {
        return getPropertyById(type).name + (fen > 0 ? "+" : "") + (fen / 100.0).toFixed(2) + "元"
      },
      getTime (time) {
        let date = time.split(" ")[0]
        let timeStr = time.split(" ")[1]
        let dateArr = date.split("-")
        let timeArr = timeStr.split(":")
        return dateArr[1] + "-" + dateArr[2] + " " + timeArr[0] + ":" + timeArr[1]
      },
      async loadData () {
        const result = await this.httpPost(CURRENCY_DETAIL_URL, {
          pageNo: this.pageNo,
          pageSize: this.pageSize
        })
        if (result.success) {
          if (result.data.current === 1) {
            this.currencyChangeList = result.data.records
          } else {
            this.currencyChangeList = this.currencyChangeList.concat(result.data.records)
          }
          if (result.data.current === result.data.pages || result.data.records.length === 0) {
            this.isFinished = true
          } else {
            this.isFinished = false
          }
        } else {
          this.isError = true
        }
        this.isRefreshing = false
        this.isLoading = false
      },
      onRefresh () {
        this.isFinished = true
        this.pageNo = 1
        this.loadData()
      },
      onListLoad () {
        this.pageNo++
        this.loadData()
      },
      getTitle (reason) {
        // FINISH_MISSION(38),//完成任务,获得收入
        //   BE_PARTNER(39),//充值合作商
        //   PUBLISH_MISSION(40),//发布任务，消耗任务币
        //   RECHARGE_MISSION_COIN(42),//充值任务币
        //   RECHARGE_CASH_DEPOSIT(43),//充值保证金
        //   WITHDRAWAL_EARNING(44),//收入提现
        //   OLD_PARTNER_REFUND(45),//升级合作商，旧的合作商退款
        //   WITHDRAWAL_MISSION_COIN(46),//任务币提现
        //   EARNING_BY_SUBORDINATE_RECHARGE_MISSION_COIN(50),//下级充值任务币，上级得收入
        //   WITHDRAWAL_MISSION_COIN_FEE(51),//任务币提现扣除的手续费
        //   WITHDRAWAL_EARNING_FEE(52),//收入提现扣除的手续费
        //   EARNING_BY_SUBORDINATE_WITHDRAWAL_EARNING(53),//下级提现收入，上级得手续费分成收入
        //   WITHDRAWAL_CASH_DEPOSIT(54),//保证金申退
        //   PUBLISH_MISSION_REVIEW_REJECT(59),//发布任务审核被拒，退还任务币
        //   PUBLISHED_MISSION_ADD_COUNT(60),//已发布的任务追加数量
        //   PUBLISHED_MISSION_CHANGE_PRICE(61),//已发布的任务上调价格
        //   OFF_MISSION(62),//已发布的任务下架
        //   TOP_MISSION(63),//任务置顶
        //   PUBLISH_MISSION_OVER_DEADLINE(67);//发布任务超过截止日期，退还没做完的任务币
        let title = ""
        switch (reason) {
          case 38:
            title = "完成任务"
            break
          case 39:
            title = "充值合作商"
            break
          case 40:
            title = "发布任务"
            break
          case 42:
            title = "充值任务币"
            break
          case 43:
            title = "充值保证金"
            break
          case 44:
            title = "收入提现"
            break
          case 46:
            title = "任务币提现"
            break
          case 50:
            title = "下级充值任务币分成"
            break
          case 51:
            title = "任务币提现手续费"
            break
          case 52:
            title = "收入提现手续费"
            break
          case 53:
            title = "下级提现收入分成"
            break
          case 54:
            title = "保证金申退"
            break
          case 59:
            title = "发布任务审核不合格"
            break
          case 60:
            title = "任务追加数量"
            break
          case 61:
            title = "任务上调价格"
            break
          case 62:
            title = "任务下架"
            break
          case 63:
            title = "任务置顶"
            break
          case 67:
            title = "任务超过截止日期"
            break
        }
        return title
      }
    }
  }
</script>

<style scoped lang="less">
  .name-info {
    height: auto;
    padding: 4px 0
  }

  .top {
    height: 100px;
    /*width: 100%;*/
    background-color: #81d4cb;
    padding: 10px 18px;

    .info {

      img {
        width: 58px;
        height: 58px;
        border-radius: 29px;
      }

      div {
        margin-left: 14px;
        height: 50px;

        .name {
          font-size: 16px;
          color: #ffffff;
        }

        .subordinates {
          font-size: 14px;
          color: #ffffff;
        }
      }
    }

    .description {
      font-size: 0;
      margin-top: 10px;

      .normal {
        font-size: 12px;
        color: #ffffff;
      }

      .important {
        font-size: 16px;
        line-height: 16px;
        color: red;
      }
    }

  }

  .cell {

    img {
      width: 42px;
      height: 42px;
      border-radius: 21px;
    }

    .title {
      font-size: 12px;
      color: #424242;
      flex: 1;
    }

    .price {
      font-size: 12px;
      color: #424242;
      width: 130px;
    }

    .time {
      font-size: 14px;
      color: #424242;
    }
  }
</style>
