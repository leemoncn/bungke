/**
* @Author: limeng
* @Date: 2019-10-23 20:37
* @Description: 我的店铺
*/
<template>
  <div class="">
    <van-nav-bar
      :title="navTitle"
      left-text="返回"
      fixed ref="topBar"
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>

    <div class="under-nav flex1 height0 overflow-auto flex-column">
      <div class="top" ref="top">
        <div class="info flex-row">
          <img :src="user.headImgUrl?user.headImgUrl:defaultHeadImg">
          <div class="flex-column name-info">
            <div style="font-size: 0">
              <span class="name">
                {{name}}
              </span>
              <button class="follow" @click="followPressed" v-if="shopDetail && queryId && user.id !== queryId">
                {{shopDetail.isFollowed?"取消":"关注"}}
              </button>
            </div>
            <div class="flex1"></div>
            <p class="subordinates">用户ID: {{queryId}}</p>

          </div>
        </div>
        <div class="line"></div>
        <div>
          <div class="flex-row shop-detail">
            <span class="flex1">发任务 {{shopDetail?shopDetail.publishedMissionCount:""}}个</span>
            <span class="flex1">总发单 {{shopDetail?shopDetail.publishedOrderCount:""}}单</span>
            <span class="flex1">总成交 {{shopDetail?shopDetail.finishedOrderCount:""}}单</span>
          </div>
          <div class="flex-row shop-detail">
            <span class="flex1">被申诉 {{shopDetail?shopDetail.beComplaintCount:""}}次</span>
            <span class="flex1">接任务 {{shopDetail?shopDetail.acceptMissionCount:""}}单</span>
            <span class="flex1">申 诉 {{shopDetail?shopDetail.complaintCount:""}}次</span>
          </div>
        </div>
      </div>
      <div class="tabs flex-row" ref="tab">
        <span :class="selectedTab === 0 ?'tab-selected':''" @click="onTabsSelected(0)">进行中({{shopDetail?shopDetail.processMissionList.length:0}})</span>
        <span :class="selectedTab === 1 ?'tab-selected':''"
              @click="onTabsSelected(1)">已下架({{shopDetail?shopDetail.offMissionList.length:0}})</span>
      </div>
      <div class="overflow-auto" :style="listHeight">
        <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
          <van-list v-model="isLoading"
                    :finished="isFinished"
                    finished-text=""
                    :style="{minHeight:listHeight.height}"
                    error-text="请求失败，点击重新加载"
                    :error.sync="isError">
            <van-cell v-for="(item,index) in dataList" :key="index">
              <shop-mission-cell :mission-item="item" :can-view="selectedTab === 0"/>
            </van-cell>
          </van-list>
        </van-pull-refresh>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapGetters } from "vuex"
  import { FOLLOW_URL, SHOP_URL } from "@/config/host"
  import ShopMissionCell from "@/components/ShopMissionCell"

  export default {
    name: "Fans",
    components: { ShopMissionCell },
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
        shopDetail: null,
        selectedTab: 0, // 0或者1
        queryId: null, // 查询店铺的用户id
        name: ""// 查询店铺的用户昵称
      }
    },
    mounted () {
      this.queryId = parseInt(this.$route.query.id)
      this.name = this.$route.query.name
      this.listHeight.height = window.innerHeight - this.$refs.top.offsetHeight - this.$refs.tab.offsetHeight - this.$refs.topBar.offsetHeight + "px"
      this.loadData()
    },
    computed: {
      ...mapGetters([
        "user"
      ]),
      dataList () {
        if (!this.shopDetail) {
          return []
        }
        const list = this.selectedTab === 0 ? this.shopDetail.processMissionList : this.shopDetail.offMissionList
        if (list) {
          return list
        }
        return []
      },
      navTitle () {
        if (this.queryId === this.user.id) {
          return "我的店铺"
        }
        return this.name + "的店铺"
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      async loadData () {
        const result = await this.httpPost(SHOP_URL, {
          id: this.queryId
        })
        if (result.success) {
          this.shopDetail = result.data
          // for (let i = 0; i < 20; i++) {
          //   this.followList.push(this.followList[0])
          // }
          this.isFinished = true
        } else {
          this.isError = true
        }
        this.isRefreshing = false
        this.isLoading = false
      },
      onRefresh () {
        this.isFinished = true
        this.loadData()
      },
      onTabsSelected (index) {
        if (this.selectedTab === index) {
          return
        }
        this.selectedTab = index
      },
      async followPressed () {
        const result = await this.httpPostWithLoading(FOLLOW_URL, {
          followId: this.queryId,
          followResult: !this.shopDetail.isFollowed
        })
        if (result.success) {
          this.shopDetail.isFollowed = !this.shopDetail.isFollowed
        }
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
    background-color: #81d4cb;
    padding: 10px 18px;

    .info {

      img {
        width: 58px;
        height: 58px;
        border-radius: 29px;
      }

      > div {
        margin-left: 14px;
        height: 50px;

        .name {
          font-size: 16px;
          color: #ffffff;
        }

        .subordinates {
          font-size: 12px;
          color: #ffffff;
        }

        .follow {
          width: 46px;
          height: 17px;
          line-height: 17px;
          border-radius: 8px;
          border: solid 0.5px #ffffff;
          font-size: 12px;
          background-color: transparent;
          color: white;
          margin-left: 100px;
        }
      }
    }

    .line {
      height: 1px;
      margin: 13px 0;
      background-color: #c9ece8;
    }

    .shop-detail {
      justify-content: space-around;

      span {
        font-size: 14px;
        color: #ffffff;
        padding-left: 20px;
      }
    }
  }

  @fans-tab-height: 44px;

  .tabs {
    background-color: white;
    height: @fans-tab-height;
    border-bottom: #f5f6f7 1px solid;

    span {
      flex: 1;
      display: inline-block;
      text-align: center;
      line-height: @fans-tab-height;
      font-size: 17px;
      color: #424242;
    }

    span:nth-child(1) {
      margin-left: 40px;
    }

    span:nth-child(2) {
      margin-right: 40px;
    }

    .tab-selected {
      color: #81d4cb;
    }
  }

  .cell {

    img {
      width: 42px;
      height: 42px;
    }

    .name {
      margin-left: 10px;
      font-size: 16px;
      color: #424242;
    }

    .time {
      font-size: 14px;
      color: #424242;
    }
  }
</style>
