/**
* @Author: limeng
* @Date: 2019-05-19 22:16
* @Description:
*/
<template>
  <div class="overflow-auto" :style="listHeight">
    <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
      <van-list @load="onListLoadMore" class="overflow-auto" v-model="isLoading"
                ref="list"
                :style="{minHeight: listHeight.height}"
                :offset="200"
                :finished="isFinished"
                finished-text="没有更多了"
                error-text="请求失败，点击重新加载"
                :error.sync="isError">
        <van-cell v-for="(item,index) in tableData" :key="index" @click="onListItemClick(item)">
          <div class="cell">
            <div>
              <div class="top-line">
                <span class="title">{{getMissionTypeName(item)}}</span>
                <span class="description">{{item.title}}</span>
              </div>
              <table>
                <tr>
                  <td>任务编号：{{item.missionId}}</td>
                  <td>单价：{{item.price / 100}}元</td>
                </tr>
                <tr>
                  <td>剩余数量：{{item.count}}</td>
                  <td v-show="item.statusPropertyId > 28">待审核数量：{{item.waitingReviewCount}}</td>
                </tr>
                <tr v-show="item.statusPropertyId > 28">
                  <td>已完成数量：{{item.finishCount}}</td>
                  <td>不合格数量：{{item.rejectedCount}}</td>
                </tr>
                <tr>
                  <td>任务状态：{{getMissionStatusName(item)}}</td>
                  <td v-show="item.reason && (item.statusPropertyId === 29 || item.statusPropertyId === 57)">
                    原因：{{item.reason}}
                  </td>
                </tr>
              </table>
              <div v-show="item.topEndTime">
                <span class="top">置顶结束时间：{{item.topEndTime}}</span>
              </div>
              <div v-if="item.statusPropertyId === 32" class="button">
                <button @click.stop="onReviewMissionClick(item)">审核</button>
                <button @click.stop="onOffMissionClick(item.missionId)">下架</button>
                <button @click.stop="onTopMissionClick(item)">置顶</button>
                <button @click.stop="onAddMissionCountClick(item)">追加数量</button>
                <button @click.stop="onAddMissionPriceClick(item)">上调出价</button>
              </div>
              <div class="button" v-else style="justify-content: normal">
                <template
                  v-show="item.statusPropertyId === 57 || item.statusPropertyId === 58 || item.statusPropertyId === 30 || item.statusPropertyId === 31">
                  <!--驳回或者下架-->
                  <button @click.stop="republishMission(item.missionId)" class="button-right">重发任务</button>
                </template>
                <!--待审核或者审核驳回-->
                <button @click.stop="deleteMission(item.missionId)">删除</button>
              </div>
            </div>
          </div>
        </van-cell>
      </van-list>
    </van-pull-refresh>

    <van-dialog
      v-model="showNumberDialog"
      :title="showNumberTitle"
      @confirm="onShowNumberDialogConfig"
      show-cancel-button>
      <van-cell-group>
        <van-field v-model="dialogNumber" type="number" :placeholder="showNumberPlaceholder"/>
      </van-cell-group>
    </van-dialog>
    <van-dialog
      v-model="showTopDialog"
      title="任务置顶"
      @confirm="onTopDialogConfirm"
      show-cancel-button>
      <van-cell-group>
        <van-field
          v-model="topNumber"
          type="tel"
          label="时间"
          input-align="right"
          :border="false"
          placeholder="请输入置顶时间（小时）"/>
        <div class="top-remark">
          <p style="color: #67D05C;">今日剩余免费置顶时间: {{topFreeHourToday}}小时({{partner.name}})</p>
          <p>备注:</p>
          <p>1、{{`置顶功能收费标准为${getPropertyById(55).value / 100}任务币/小时`}}</p>
          <p>2、置顶成功后未到期前可以续期，时间自动累加</p>
          <p>3、置顶只针对本任务有效，任务不能中途更换</p>
          <p>4、提前下架或置顶未到期任务提前完成，剩余置顶时间不会保留也不会折算成任务币返还</p>
        </div>
      </van-cell-group>
    </van-dialog>
  </div>
</template>

<script>
  import {
    ADD_MISSION_COUNT_URL, ADD_MISSION_PRICE_URL,
    DELETE_MISSION_URL,
    MY_PUBLISH_MISSION_LIST_URL,
    OFF_MISSION_URL, PARTNER_DETAIL_URL, TOP_MISSION_URL
  } from "@/config/host"
  import { getPropertyById } from "@/service/property"
  import { Dialog } from "vant"
  import { mapGetters } from "vuex"
  import Decimal from "decimal.js"

  const SHOW_NUMBER_DIALOG_TITLE = ["追加数量", "上调出价"]

  export default {
    name: "MyMissionPublish",
    data () {
      return {
        tableData: [],
        isRefreshing: false,
        isLoading: false,
        isError: false,
        isFinished: false,
        pageNo: 0,
        pageSize: 10,
        listHeight: {
          height: "0"
        },
        gotoRepublishMissionPage: false,
        showNumberDialog: false,
        showNumberTitle: "",
        showNumberPlaceholder: "",
        dialogNumber: null,
        numberDialogItem: null,
        showTopDialog: false,
        topNumber: null,
        topDialogItem: null,
        topFreeHourToday: null
      }
    },
    props: {
      willShow: {
        type: Boolean
      }
    },
    computed: {
      ...mapGetters([
        "user",
        "partner"
      ])
    },
    mounted () {
      this.listHeight.height = this.$el.parentNode.offsetHeight + "px"
    },
    activated () {
      if (this.gotoRepublishMissionPage) {
        this.loadData()
      }
    },
    watch: {
      willShow: function (newData, oldData) {
        if (newData) {
          if (this.tableData.length === 0) {
            this.loadData()
          }
        }
      }
    },
    methods: {
      async loadData () {
        const result = await this.httpPost(MY_PUBLISH_MISSION_LIST_URL, {
          pageNo: this.pageNo,
          pageSize: this.pageSize
        })
        if (result.success) {
          if (result.data.current === 1) {
            this.tableData = result.data.records
          } else {
            this.tableData = this.tableData.concat(result.data.records)
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
      onTopBarItemClick (item, index) {
        if (this.topBarActiveIndex !== index) {
          this.topBarActiveIndex = index
          this.pageNo = 0
          this.isFinished = false
        }
      },
      getMissionTypeName (item) {
        const p = getPropertyById(item.typePropertyId)
        return p.name
      },
      onListLoadMore () {
        this.pageNo++
        this.loadData()
      },
      onListItemClick (item) {
        if (item.statusPropertyId === 30 ||// 截止
          item.statusPropertyId === 31 ||// 全部完成
          item.statusPropertyId === 32 ||// 已发布
          item.statusPropertyId === 58) { // 已下架
          this.$router.push({ name: "MyNeedReview", query: { id: item.missionId, title: item.title } })
        }
      },
      onRefresh () {
        this.isFinished = true
        this.pageNo = 1
        this.loadData()
      },
      getMissionStatusName (item) {
        const property = getPropertyById(item.statusPropertyId)
        return property.name
      },
      republishMission (missionId) {
        this.gotoRepublishMissionPage = true
        this.$router.push({ name: "CreateMission", query: { id: missionId } })
      },
      deleteMission (missionId) {
        Dialog.confirm({
          message: "确认删除吗？"
        }).then(async () => {
          const result = await this.httpPost(DELETE_MISSION_URL, {
            id: missionId
          })
          if (result.success) {
            this.tableData.splice(this.tableData.findIndex(v => v.missionId === missionId), 1)
          }
        })
      },
      async onReviewMissionClick (item) {
        if (item.waitingReviewCount <= 0) {
          this.toastShortMsg("暂无需要审核的任务")
          return
        }
        this.$router.push({ name: "MissionReview", query: { missionId: item.missionId } })
      },
      onOffMissionClick (missionId) {
        Dialog.confirm({
          title: "任务下架",
          message: "下架后当前任务进行中及未审核的任务都降视为合格，剩余未完成任务币将退回，您确认下架吗？"
        }).then(async () => {
          const result = await this.httpPostWithLoading(OFF_MISSION_URL, {
            id: missionId
          })
          if (result.success) {
            this.$store.commit("updateUser", result.data.user)
            this.toastSuccess("任务下架成功")
            this.loadData()
          }
        })
      },
      async onTopMissionClick (item) {
        this.httpPost(PARTNER_DETAIL_URL, null).then((result) => {
          if (result.success) {
            this.topFreeHourToday = result.data.freeHourToday
            this.$store.commit("updatePartner", result.data.partner)
          }
        })
        this.topDialogItem = item
        this.topNumber = null
        this.showTopDialog = true
      },
      async onAddMissionCountClick (item) {
        this.numberDialogItem = item
        this.dialogNumber = null
        this.showNumberTitle = SHOW_NUMBER_DIALOG_TITLE[0]
        this.showNumberPlaceholder = "请输入需要追加的数量"
        this.showNumberDialog = true
      },
      async onAddMissionPriceClick (item) {
        this.numberDialogItem = item
        this.dialogNumber = null
        this.showNumberTitle = SHOW_NUMBER_DIALOG_TITLE[1]
        this.showNumberPlaceholder = "请输入调整后的价格(元)"
        this.showNumberDialog = true
      },
      onShowNumberDialogConfig () {
        let url = ""
        if (!this.dialogNumber) {
          return
        }
        let number = new Decimal(this.dialogNumber)
        if (!this.showNumberTitle) {
          this.toastShortMsg("系统错误，请联系客服")
          return
        }
        let fee = null
        let missionCoin = null
        if (this.showNumberTitle === SHOW_NUMBER_DIALOG_TITLE[0]) { // 追加数量
          url = ADD_MISSION_COUNT_URL
          if (number <= 0) {
            this.toastShortMsg("追加数量应大于0")
            return
          }
          if (!/^\d+$/.test(number)) {
            this.toastShortMsg("追加数量应为整数")
            return
          }
          missionCoin = number.times(this.numberDialogItem.price).ceil()
          fee = missionCoin.times(this.numberDialogItem.feePercent).dividedBy(100.0).ceil()
        } else { // 上调出价
          url = ADD_MISSION_PRICE_URL
          if (number <= 0) {
            this.toastShortMsg("价格应大于0")
            return
          }
          if (number <= new Decimal(this.numberDialogItem.price).dividedBy(100.0)) {
            this.toastShortMsg("出价应大于原始单价")
            return
          }
          if (!number.equals(new Decimal(number).toFixed(2))) {
            this.toastShortMsg("最多2位小数")
            return
          }
          missionCoin = number.times(100.0).minus(this.numberDialogItem.price).times(this.numberDialogItem.count).ceil()
          console.log("mmmco = ", missionCoin)
          fee = missionCoin.times(this.numberDialogItem.feePercent).dividedBy(100.0).ceil()
          console.log("fee = ", fee)
        }
        let total = fee.plus(missionCoin)
        let msg = `需要扣除任务花费${missionCoin.dividedBy(100.0).toFixed(2)}元及手续费${fee.dividedBy(100.0).toFixed(2)}元,总共${total.dividedBy(100.0).toFixed(2)}元`
        if (this.user.missionCoin < total) {
          msg += `,当前任务币剩余${new Decimal(this.user.missionCoin).dividedBy(100.0).toFixed(2)}元,请先充值`
          Dialog.confirm({
            message: msg
          })
        } else {
          if (this.showNumberTitle === SHOW_NUMBER_DIALOG_TITLE[0]) { // 追加数量
            msg += ",确认追加数量吗？"
          } else { // 上调出价
            msg += ",确认上调出价吗？"
          }
          Dialog.confirm({
            message: msg
          }).then(async () => {
            const result = await this.httpPostWithLoading(url, {
              missionId: this.numberDialogItem.missionId,
              number: this.showNumberTitle === SHOW_NUMBER_DIALOG_TITLE[1] ? number.times(100.0) : number
            })
            if (result.success) {
              this.$store.commit("updateUser", result.data)
              if (this.showNumberTitle === SHOW_NUMBER_DIALOG_TITLE[0]) { // 追加数量
                this.toastSuccess("追加数量成功")
              } else { // 上调出价
                this.toastSuccess("上调出价成功")
              }
              this.loadData()
            }
          })
        }
      },
      async onTopDialogConfirm () {
        if (this.topNumber <= 0) {
          this.toastShortMsg("时间应大于0")
          return
        }
        if (!/^\d+$/.test(this.topNumber)) {
          this.toastShortMsg("置顶时间应为整数")
          return
        }
        const result = await this.httpPostWithLoading(TOP_MISSION_URL, {
          missionId: this.topDialogItem.missionId,
          hours: this.topNumber
        })
        if (result.success) {
          this.toastSuccess("任务置顶成功")
          this.loadData()
          if (this.$store.getters.isLogin) {
            this.$store.dispatch("refreshUserInfo")
          }
        }
      }
    }
  }
</script>

<style scoped lang="less">

  .cell {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .top-line {
      font-size: 0;
      line-height: normal;

      .title {
        font-weight: bold;
        font-size: 16px;
        vertical-align: middle;
        /*display: inline-block;*/
      }

      .description {
        margin: auto 0 auto 20px;
        color: #676767;
        width: 200px;
        font-size: 16px;
        /*height: 30px;*/
        display: inline-block;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        vertical-align: middle;
      }
    }
  }

  .top-remark {
    margin: 0 15px;
    padding-bottom: 10px;
  }

  .button {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;

    button {
      color: white;
      font-size: 14px;
      line-height: normal;
      border-radius: 4px;
      width: 80px;
      height: 30px;
      background-color: #81d4cb;

      &:nth-child(n+5) {
        margin-top: 8px;
      }
    }

    .button-right {
      margin-right: 8px;
    }
  }

  .top {
    font-size: 14px;
    color: #a8a8a8;
  }

  table {
    td {
      font-size: 14px;
      color: #a8a8a8;

      &:first-child {
        width: 180px;
      }
    }
  }
</style>
