/**
* @Author: limeng
* @Date: 2019-10-27 14:52
* @Description:
*/
<template>
  <div class="">
    <van-nav-bar
      title="常见问题"
      left-text="返回"
      fixed ref="topBar"
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>

    <div class="under-nav flex1 height0">
      <div class="overflow-auto" :style="listHeight">
        <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
          <van-list v-model="isLoading"
                    :finished="isFinished"
                    finished-text=""
                    :style="{minHeight:listHeight.height}"
                    error-text="请求失败，点击重新加载"
                    :error.sync="isError">
            <van-cell is-link v-for="(item,index) in questionList" :key="index" @click="cellPressed(item)"
                      :title="item.title">
            </van-cell>
          </van-list>
        </van-pull-refresh>
      </div>
    </div>
  </div>
</template>

<script>
  import { QUESTION_LIST_URL } from "@/config/host"

  export default {
    name: "Question",
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
        questionList: []
      }
    },
    mounted () {
      this.listHeight.height = window.innerHeight - this.$refs.topBar.offsetHeight + "px"
      this.loadData()
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      async loadData () {
        const result = await this.httpPost(QUESTION_LIST_URL, null)
        if (result.success) {
          this.questionList = result.data
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
        this.$router.push({ name: "QuestionDetail", query: { title: item.title, msg: item.msg } })
      }
    }
  }
</script>

<style scoped>

</style>
