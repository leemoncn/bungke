/**
* @Author: limeng
* @Date: 2019-11-21 22:21
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="帮客"
      left-text="返回"
      :right-text="isApplyCreditCardPage?'收益报价':null"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed"
      @click-right="onNavBarRightPressed">
    </van-nav-bar>
    <div class="under-nav flex1 height0">
      <div v-html="html" v-if="isHtml"></div>
      <iframe :src="html" allowfullscreen frameborder="0" width="100%" height="100%" v-else ref="iframe"></iframe>
    </div>
  </div>
</template>

<script>
  export default {
    name: "HtmlPage",
    data () {
      return {
        isHtml: false,
        html: null,
        isApplyCreditCardPage: null,
        historyLength: history.length
      }
    },
    mounted () {
      this.isApplyCreditCardPage = this.$route.query.isApplyCreditCardPage
      this.html = this.$store.state.htmlPage.html
      this.isHtml = this.html.substring(0, 4) !== "http"
      if (!this.isHtml) { // 如果是一个第三方网址
        this.showLoading()
        let iframe = this.$refs.iframe
        let _this = this
        if (iframe.attachEvent) {
          iframe.attachEvent("onload", function () {
            _this.hideLoading()
          })
        } else { // 后台编辑的网页内容
          iframe.onload = function (e1, e2) {
            _this.hideLoading()
          }
        }
      }
    },
    destroyed () {
      this.hideLoading()
    },
    methods: {
      onNavBarLeftPressed () {
        if (this.isHtml) {
          this.$router.back()
        } else {
          let len = this.historyLength - history.length - 1// -1是不进入iframe页面的下级页面直接退出的话，执行后退一步的操作
          this.$router.go(len)
        }
      },
      onNavBarRightPressed () {
        this.$router.push({ name: "CreditCard" })
      }
    }
  }
</script>

<style scoped>

</style>
