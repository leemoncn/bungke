/**
* @Author: limeng
* @Date: 2019-10-23 20:37
* @Description: 我的下线
*/
<template>
  <div class="">
    <van-nav-bar
      title="我的团队"
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
            <p class="name">
              {{user.nickname}}
            </p>
            <div class="flex1"></div>
            <p class="subordinates" v-if="agency">我的团队: {{subordinateList.length}}人</p>
          </div>
        </div>
        <div class="description" v-show="agency">
          <span class="normal">下线充值您将获得充值金额</span>
          <span class="important" v-if="agency">&nbsp;&nbsp;{{agency.rechargeByApp}}%&nbsp;&nbsp;</span>
          <span class="normal">的奖励，下线提现您将获得提现金额</span>
          <span class="important" v-if="agency">&nbsp;&nbsp;{{agency.withdrawByUser}}%&nbsp;&nbsp;</span>
          <span class="normal">的奖励！</span>
        </div>
      </div>
      <div class="overflow-auto" :style="listHeight">
        <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
          <van-list v-model="isLoading"
                    :finished="isFinished"
                    finished-text=""
                    :style="{minHeight: listHeight.height}"
                    error-text="请求失败，点击重新加载"
                    :error.sync="isError">
            <van-cell v-for="(item,index) in subordinateList" :key="index" @click="cellPressed(item)">
              <div class="cell flex-row flex-align-center">
                <img :src="item.headImgUrl?item.headImgUrl:defaultHeadImg">
                <span class="name">{{item.name}}</span>
                <div class="flex1"></div>
                <span class="time right">{{item.registerTime}}</span>
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
  import { SUBORDINATE_URL } from "@/config/host"

  export default {
    name: "Subordinate",
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
        agency: null,
        subordinateList: []
      }
    },
    mounted () {
      this.listHeight.height = window.innerHeight - this.$refs.top.offsetHeight - this.$refs.topBar.offsetHeight + "px"
      this.loadData()
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
      async loadData () {
        const result = await this.httpPost(SUBORDINATE_URL, null)
        if (result.success) {
          this.agency = result.data.coreAgency
          this.subordinateList = result.data.subordinateList
          this.$store.commit("updateAgency", this.agency)
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
