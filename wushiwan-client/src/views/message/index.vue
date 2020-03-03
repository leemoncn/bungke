/**
* @Author: limeng
* @Date: 2019-03-31 21:35
* @Description:
*/
<template>
  <div class="column-view">
    <div ref="topBar">
      <van-nav-bar
        title="消息"
        fixed>
      </van-nav-bar>
    </div>
    <div class="under-nav overflow-auto tab-main">
      <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
        <van-list @load="onListLoad" v-model="isLoading"
                  :finished="isFinished" :offset="200"
                  finished-text=""
                  class="list"
                  error-text="请求失败，点击重新加载"
                  :error.sync="isError">
          <van-cell v-for="(item,index) in tableData" :key="index">
            <div class="cell flex-column">
              <p>{{item.createTime}}</p>
              <p>{{item.msg1}}</p>
            </div>
          </van-cell>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script>
  import { NOTICE_URL } from "@/config/host"

  export default {
    name: "Message",
    data () {
      return {
        tableData: [],
        isRefreshing: false,
        isLoading: false,
        isError: false,
        isFinished: false,
        pageNo: 0,
        pageSize: 10,
        dataFirstLoad: false
      }
    },
    mounted () {
      // this.onRefresh()
    },
    watch: {
      "$store.getters.tabbarActiveItemIndex": {
        handler: function (newValue, oldValue) {
          if (newValue === 1) {
            if (!this.dataFirstLoad) {
              this.onRefresh()
              this.dataFirstLoad = true
            }
          }
        }
      }
    },
    methods: {
      async loadData (isRefresh) {
        let func = isRefresh ? this.httpPostWithLoading : this.httpPost
        const result = await func(NOTICE_URL, {
          pageNo: this.pageNo,
          pageSize: this.pageSize
        })
        if (result.success) {
          this.$store.commit("updateNewMsgCount", 0)
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
      onListLoad () {
        this.pageNo++
        this.loadData(false)
      },
      onRefresh () {
        this.isFinished = true
        this.pageNo = 1
        this.loadData(true)
      }
    }
  }
</script>

<style scoped lang="less">
  .list {
    min-height: calc(100vh - @tab-height - @nav-height);
  }

  .cell {
    min-height: 50px;
    justify-content: space-around;
  }

</style>
