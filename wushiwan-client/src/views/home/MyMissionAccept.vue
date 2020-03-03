/**
* @Author: limeng
* @Date: 2019-05-19 22:16
* @Description:
*/
<template>
  <div>
    <div ref="menuBar">
      <div class="orderBar">
        <!--上边的排序筛选-->
        <div v-for="(item,index) in orderType" :key="index"
             @click="onTopBarItemClick(item,index)">
          <span :class="{'order-selected':topBarActiveIndex === index}">{{item}}</span>
        </div>
      </div>
      <div class="topBarBottomLine"></div>
    </div>
    <div class="overflow-auto" :style="listHeight">
      <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
        <van-list @load="onListLoadMore" v-model="isLoading"
                  ref="list"
                  :style="{minHeight: listHeight.height}"
                  :offset="200"
                  :finished="isFinished"
                  finished-text="没有更多了"
                  error-text="请求失败，点击重新加载"
                  :error.sync="isError">
          <van-cell v-for="(item,index) in tableData" :key="index" @click="onItemPressed(item)">
            <div class="flex-row">
              <div class="flex-column" style="flex: 1">
                <div class="flex-row" style="align-items: center">
                  <span class="title">{{getMissionTypeName(item) + "&nbsp;&nbsp;" + item.title}}</span>
                  <button class="tag" :class="[tagClass(item)]">{{tagName(item)}}</button>
                </div>

                <div class="description">
                  <span>编号：{{item.missionId}}</span>
                  <span class="price">{{`单价：${item.price / 100}元`}}</span>
                </div>
                <div class="description">
                  <span>截止：{{item.deadlineTime}}</span>
                </div>
              </div>
              <div class="button-div flex-column">
                <button class="button" @click.stop="onUploadImgPressed(item)"
                        v-show="item.proceedPropertyId === 21">上传
                </button>
                <button class="button" @click.stop="onUploadImgPressed(item)"
                        v-show="item.proceedPropertyId === 23 && item.finishTime === null && item.canComplaint">编辑
                </button>
              </div>
            </div>
          </van-cell>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script>
  import { MY_ACCEPT_MISSION_LIST_URL } from "@/config/host"
  import { getPropertyById } from "@/service/property"

  export default {
    name: "MyMissionAccept",
    data () {
      return {
        orderType: ["全部", "待提交", "待审核", "不合格", "已完成"],
        topBarActiveIndex: 0,
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
        isGotoFinishPage: false
      }
    },
    activated () {
      if (this.isGotoFinishPage) {
        this.loadData()
        this.isGotoFinishPage = false
      }
    },
    mounted () {
      this.listHeight.height = this.$el.parentNode.offsetHeight - this.$refs.menuBar.offsetHeight + "px"
    },
    methods: {
      async loadData () {
        const result = await this.httpPost(MY_ACCEPT_MISSION_LIST_URL, {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          orderType: this.topBarActiveIndex
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
      async onTopBarItemClick (item, index) {
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
      onUploadImgPressed (item) {
        // TODO 测试不合格但是finishtime有值的情况
        this.isGotoFinishPage = true
        this.$router.push({ name: "FinishMission", query: { id: item.missionId } })
      },
      onItemPressed (item) {
        if (item.proceedPropertyId > 21) {
          this.$router.push({ name: "MissionReview", query: { id: item.acceptId, isAcceptUser: "1" } })
        }
      },
      onRefresh () {
        this.isFinished = true
        this.pageNo = 1
        this.loadData()
      },
      tagClass (item) {
        switch (item.proceedPropertyId) {
          case 21:
            return "wait-commit"
          case 22:
            return "in-review"
          case 23:
            return "rejected"
          case 24:
            return "finished"
        }
        return ""
      },
      tagName (item) {
        switch (item.proceedPropertyId) {
          case 21:
            return "待提交"
          case 22:
            return "待审核"
          case 23:
            if (!item.canComplaint) {
              if (item.complaintResult === true) {
                return "平台终审合格"// 这个理论上不可能出现
              }
              if (item.complaintResult === false) {
                return "平台终审不合格"// 这个理论上不可能出现
              }
              if (item.complaintResult === null) {
                return "平台终审中"// 这个理论上不可能出现
              }
            }
            return "已驳回"
          case 24:
            return "已完成"
        }
        return ""
      }
    }
  }
</script>

<style scoped lang="less">

  .description {
    /*margin-top: 5px;*/
    /*font-size: 14px;*/
    line-height: normal;
    /*line-height: 14px;*/
    /*display: inline;*/

    span {
      font-size: 14px;
      color: #424242;
    }

    .price {
      margin-left: 17px;
    }
  }

  .title {
    font-size: 16px;
    color: #424242;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    display: inline-block;
    max-width: 240px;
  }

  .button-div {
    width: 50px;
    height: auto;
    justify-content: center;
    align-items: flex-end;

    button {
      width: 44px;
      height: 21px;
      line-height: normal;
      border: solid 1px #81d4cb;
      border-radius: 4px;
      color: #81d4cb;
      font-size: 12px;
      background-color: white;
      text-align: center;
    }
  }

  .tag {
    height: 17px;
    font-size: 12px;
    color: #ffffff;
    line-height: normal;
    margin-left: 6px;

    &.rejected {
      background-color: #d32d25;
    }

    &.wait-commit {
      background-color: #7ded7d;
    }

    &.in-review {
      background-color: #e49558;
    }

    &.finished {
      background-color: #90ccf0;
    }
  }
</style>
