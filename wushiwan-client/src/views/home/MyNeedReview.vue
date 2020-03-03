/**
* @Author: limeng
* @Date: 2019-05-19 22:16
* @Description:
*/
<template>
  <div class="column-view">
    <div>
      <van-nav-bar
        ref="topBar"
        :title="title"
        left-text="返回"
        fixed
        left-arrow
        @click-left="onNavBarLeftPressed">
      </van-nav-bar>
    </div>
    <div ref="menuBar" class="under-nav">
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
          <van-cell v-for="(item,index) in tableData" :key="index" @click="onListItemClick(item)">
            <div class="cell">
              <div>
                <div class="top-line">
                  <span class="title">{{getMissionTypeName(item)}}</span>
                  <span
                    class="description">{{item.title}}</span>
                </div>
                <div class="bottom-line">
                  <span>任务编号：{{item.missionId}}</span>
                  <span class="price">单价：{{item.price / 100}}元</span>
                </div>
                <div class="bottom-line">
                  <span>提交时间：{{item.uploadTime}}</span>
                </div>
                <div class="bottom-line">
                  <span>提交者：{{item.uploadUserName}}</span>
                </div>
              </div>
            </div>
          </van-cell>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script>
  import { REVIEW_MISSION_LIST_URL } from "@/config/host"
  import { getPropertyById } from "@/service/property"

  export default {
    name: "MyNeedReview",
    data () {
      return {
        missionId: null,
        title: "",
        orderType: ["待审核", "不合格", "已完成"],
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
        }
      }
    },
    mounted () {
      this.missionId = this.$route.query.id
      this.title = this.$route.query.title
      this.listHeight.height = window.innerHeight - this.$refs.menuBar.offsetHeight - this.$refs.topBar.offsetHeight + "px"
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      async loadData () {
        let proceedPropertyId = null
        switch (this.topBarActiveIndex) {
          case 0:
            proceedPropertyId = 22
            break
          case 1:
            proceedPropertyId = 23
            break
          case 2:
            proceedPropertyId = 24
            break
        }
        const result = await this.httpPost(REVIEW_MISSION_LIST_URL, {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          missionId: this.missionId,
          proceedPropertyId: proceedPropertyId
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
          this.tableData = []
        }
      },
      getMissionTypeName (item) {
        const p = getPropertyById(item.typePropertyId)
        return p.name
      },
      getMissionProceedName (item) {
        const p = getPropertyById(item.proceedPropertyId)
        return p.name
      },
      onListLoadMore () {
        this.pageNo++
        this.loadData()
      },
      onListItemClick (item) {
        this.$router.push({ name: "MissionReview", query: { id: item.acceptId } })
      },
      onRefresh () {
        this.isFinished = true
        this.pageNo = 1
        this.loadData()
      }
    }
  }
</script>

<style scoped lang="less">

  .cell {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-weight: bold;
      font-size: 16px;
    }

    .description {
      margin-left: 20px;
      color: #676767;
      width: 200px;
      height: 30px;
      display: block;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
    }

    .top-line {
      display: flex;
    }

    .bottom-line {
      color: #a8a8a8;

      .price {
        margin-left: 20px;
      }
    }

  }
</style>
