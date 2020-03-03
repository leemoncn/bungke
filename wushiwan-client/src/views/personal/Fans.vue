/**
* @Author: limeng
* @Date: 2019-10-23 20:37
* @Description: 粉丝关注
*/
<template>
  <div class="">
    <van-nav-bar
      title="粉丝关注"
      left-text="返回"
      fixed ref="topBar"
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>

    <div class="under-nav flex1 height0 flex-column">
      <div class="top" ref="top">
        <div class="info flex-row">
          <img :src="user.headImgUrl?user.headImgUrl:defaultHeadImg">
          <div class="flex-column name-info">
            <p class="name">
              {{user.nickname}}
            </p>
            <div class="flex1"></div>
            <p class="subordinates">用户ID: {{user.id}}</p>
          </div>
        </div>
      </div>
      <div class="tabs flex-row" ref="tab">
        <span :class="selectedTab === 0 ?'tab-selected':''" @click="onTabsSelected(0)">我的粉丝({{fansList.length}}人)</span>
        <span :class="selectedTab === 1 ?'tab-selected':''"
              @click="onTabsSelected(1)">我的关注({{followList.length}}人)</span>
      </div>
      <!--<div class="flex1">-->
      <div class="overflow-auto" :style="listHeight">
        <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
          <van-list v-model="isLoading"
                    :finished="isFinished"
                    finished-text=""
                    :style="{minHeight:listHeight.height}"
                    error-text="请求失败，点击重新加载"
                    :error.sync="isError">
            <van-cell v-for="(item,index) in dataList" :key="index" @click="cellPressed(item)">
              <div class="cell flex-row flex-align-center">
                <img :src="item.headImgUrl?item.headImgUrl:defaultHeadImg">
                <span class="name">{{item.name}}</span>
                <div class="flex1"></div>
                <span class="time right">{{item.time}}</span>
              </div>
            </van-cell>
          </van-list>
        </van-pull-refresh>
      </div>
      <!--</div>-->

    </div>
  </div>
</template>

<script>
  import { mapGetters } from "vuex"
  import { FANS_URL } from "@/config/host"

  export default {
    name: "Fans",
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
        fansList: [],
        followList: [],
        selectedTab: 0// 0或者1
      }
    },
    mounted () {
      this.listHeight.height = window.innerHeight - this.$refs.top.offsetHeight - this.$refs.tab.offsetHeight - this.$refs.topBar.offsetHeight + "px"
      this.loadData()
    },
    computed: {
      ...mapGetters([
        "user"
      ]),
      dataList () {
        return this.selectedTab === 0 ? this.fansList : this.followList
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      async loadData () {
        const result = await this.httpPost(FANS_URL, null)
        if (result.success) {
          this.fansList = result.data.fansList
          this.followList = result.data.followList
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
      cellPressed (item) {
        this.$router.push({ name: "Shop", query: { id: item.id, name: item.name } })
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
    height: 60px;
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
          font-size: 12px;
          color: #ffffff;
        }
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
      border-radius: 21px;
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
