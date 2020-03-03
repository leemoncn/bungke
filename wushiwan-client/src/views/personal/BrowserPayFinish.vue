/**
* @Author: limeng
* @Date: 2019-08-19 23:55
* @Description: 使用系统浏览器支付，支付完成会跳回这个页面
*/
<template>
  <div>
    <van-nav-bar
      title="支付完成"
      fixed>
    </van-nav-bar>
  </div>
</template>

<script>
  import { Dialog } from "vant"

  export default {
    name: "BrowserPayFinish",
    data () {
      return {}
    },
    async mounted () {
      let paySuccess = this.$route.query["out_trade_no"] && this.$route.query["trade_no"]
      Dialog.alert({
        message: paySuccess ? "支付完成" : "支付失败"
      }).then(() => {
        this.$store.dispatch("refreshUserInfo")
        this.$router.replace({ path: "/" })
      })
    }
  }
</script>

<style scoped>

</style>
