/**
* @Author: limeng
* @Date: 2019-04-03 22:10
* @Description:
*/
<template>
  <div class="grayPageBG">
    <div ref="topBar">
      <van-nav-bar
        title="接单赚钱"
        right-text="我的任务"
        left-text="返回"
        left-arrow
        fixed
        @click-left="onNavBarLeftPressed"
        @click-right="onNavBarRightPressed">
        <form action="/" slot="title">
          <van-search
            class="search"
            v-model="searchData"
            shape="round"
            placeholder="请输入搜索关键词"
            @search="onSearch"
            @cancel="onCancel"
          />
        </form>
      </van-nav-bar>
    </div>
    <div class="under-nav main-height overflow-auto flex-row" ref="mainDiv">
      <!--左侧的订单类型删选-->
      <div class="overflow-auto left" v-show="showLeftList">
        <van-list class="left-list">
          <div v-for="(item,index) in missionTypeList" :key="index" class="category"
               :class="{'category-selected':index === leftListActiveIndex}"
               @click="onLeftListItemClick(item,index)">
            <span style="width: 50px">{{item.name}}</span>
          </div>
        </van-list>
      </div>

      <div class="right flex-column">
        <div ref="menuBar">
          <div class="orderBar">
            <van-button plain type="primary" @click="onLeftListShowPressed">{{currentMissionTypeName}}</van-button>
            <!--上边的排序筛选-->
            <div v-for="(item,index) in orderType" :key="index"
                 @click="onRightListBarItemClick(item,index)">
              <span :class="{'order-selected':rightListBarActiveIndex === index}">{{item}}</span>
            </div>
          </div>
          <div class="topBarBottomLine"></div>
        </div>
        <!--真正的内容-->
        <div class="overflow-auto" :style="rightListHeight">
          <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
            <van-list @load="onRightListLoad" v-model="isLoading"
                      :style="{minHeight: rightListHeight.height}"
                      ref="rightList"
                      :finished="isFinished"
                      finished-text="没有更多了"
                      error-text="请求失败，点击重新加载"
                      :error.sync="isError">
              <van-cell v-for="(item,index) in tableData" :key="index" @click="onRightListItemClick(item)">
                <accept-mission-item :mission-item="item"/>
              </van-cell>
            </van-list>
          </van-pull-refresh>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

  import AcceptMissionItem from "@/views/home/AcceptMissionItem"
  import { MISSION_LIST_URL } from "@/config/host"
  import { getDeviceType } from "@/service/device"

  export default {
    name: "AcceptMission",
    components: { AcceptMissionItem },
    data () {
      return {
        searchData: null,
        listHeight: {
          height: "0"
        },
        rightListHeight: {
          height: "0"
        },
        orderType: ["最新", "优先", "人气", this.deviceTypeName()],
        leftListActiveIndex: 0,
        rightListBarActiveIndex: 0,
        showLeftList: false,
        tableData: [],
        isRefreshing: false,
        isLoading: false,
        isError: false,
        isFinished: false,
        pageNo: 0,
        pageSize: 10
      }
    },
    computed: {
      missionTypeList: function () {
        let missionRuleList = this.$store.getters.missionRuleList
        let types = []
        for (const e of missionRuleList) {
          types.push(this.getPropertyById(e.typePropertyId))
        }
        types.unshift({ name: "全部" })
        return types
      },
      currentMissionTypeName: function () {
        const property = this.missionTypeList[this.leftListActiveIndex]
        return property.name
      }
    },
    mounted () {
      this.rightListHeight.height = this.$refs.mainDiv.offsetHeight - this.$refs.menuBar.offsetHeight + "px"
    },
    activated () {
      const needRemoveId = this.$store.state.missionAccept.needRemoveItemIdOnActive
      if (this.tableData && needRemoveId) {
        this.tableData.splice(this.tableData.findIndex(item => item.id === needRemoveId), 1)
        this.$store.commit("userNeedRemoveItemId", null)
      }
    },
    methods: {
      deviceTypeName () {
        const deviceType = getDeviceType()
        if (deviceType === 19) {
          return "安卓"
        }
        if (deviceType === 20) {
          return "苹果"
        }
        return "安卓&苹果"
      },
      async loadData (showLoading) {
        let func
        if (showLoading) {
          func = this.httpPostWithLoading
        } else {
          func = this.httpPost
        }
        const result = await func(MISSION_LIST_URL, {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          typePropertyId: this.leftListActiveIndex === 0 ? null : this.missionTypeList[this.leftListActiveIndex].id,
          orderType: this.rightListBarActiveIndex,
          deviceType: getDeviceType(),
          searchData: this.searchData
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
      onRefresh () {
        this.isFinished = true
        this.pageNo = 1
        this.loadData(true)
      },
      onNavBarLeftPressed () {
        this.$router.back()
      },
      onNavBarRightPressed () {
        this.$router.push({ name: "MyMission" })
      },
      onSearch () {
        this.isFinished = true
        this.pageNo = 1
        this.loadData(true)
      },
      onCancel () {
      },
      async onLeftListItemClick (item, index) {
        this.showLeftList = false
        if (this.leftListActiveIndex !== index) {
          this.leftListActiveIndex = index
          this.pageNo = 0
          this.isFinished = false
        }
      },
      async onRightListBarItemClick (item, index) {
        if (this.rightListBarActiveIndex !== index) {
          this.rightListBarActiveIndex = index
          this.pageNo = 0
          this.isFinished = false
        }
      },
      onRightListLoad () {
        this.pageNo++
        this.loadData(false)
      },
      onLeftListShowPressed () {
        this.showLeftList = !this.showLeftList
      },
      onRightListItemClick (item) {
        this.$router.push({ name: "MissionDetail", query: { id: item.id } })
      }
    }
  }
</script>

<style scoped lang="less">

  .search {
    padding: 6px 20px 0 0 !important;
    margin-left: 14px;
  }

  .left {
    height: 100%;
    flex-shrink: 0;

    .left-list {
      background-color: #f7f8f9;
      vertical-align: bottom; //解决inline-block和overflow组合导致基线多出一定的像素的问题

      ::-webkit-scrollbar {
        display: none;
      }
    }
  }

  .right {
    flex-grow: 1;
    flex-shrink: 1;
    /*flex: 1;*/
    /*width: 200px;*/
  }

  .category {
    padding: 12px 0;
    /*width: 100px;*/
    /*height: 50px;*/

    span {
      color: #717171;
      margin: 0 20px;
    }
  }

  .category-selected {
    background-color: white;

    span {
      color: #000;
      font-weight: bold;
    }
  }
</style>
