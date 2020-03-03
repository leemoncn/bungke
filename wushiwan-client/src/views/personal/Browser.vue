/**
* @Author: limeng
* @Date: 2019-08-19 22:52
* @Description: 此页面专门用于生成一个wenview将真正的支付页面包进去，这样外层页面可以返回
*/
<template>
  <div class="column-view">
    <van-nav-bar
      :title="title"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>
  </div>
</template>

<script>
  import { CLIENT_URL } from "@/config/host"

  export default {
    name: "Browser",
    data () {
      return {
        title: "",
        webview: null
      }
    },
    mounted () {
      this.title = this.$route.query.title
      const amount = this.$route.query.amount
      const requestUrl = this.$route.query.url
      const url = CLIENT_URL + `/#/alipay?amount=${amount}&url=${requestUrl}`
      this.webview = plus.webview.create(url, "", { top: "46px", bottom: "0px" })
      this.webview.show() // 显示窗口
      let _this = this
      this.webview.onclose = function (e) {
        _this.onNavBarLeftPressed()
      }
    },
    methods: {
      onNavBarLeftPressed () {
        // h5的在RechargeFinish类里面刷新
        this.$store.dispatch("refreshUserInfo")
        this.webview.onclose = null
        this.webview.close()
        this.$router.back()
      }
    }
  }
</script>

<style scoped>

</style>
