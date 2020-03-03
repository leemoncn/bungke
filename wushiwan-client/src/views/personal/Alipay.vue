/**
* @Author: limeng
* @Date: 2019-08-19 20:52
* @Description: 支付宝支付页面
*/
<template>
  <div>
    <van-nav-bar
      v-show="showNav"
      :title="title"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>
  </div>
</template>

<script>

  export default {
    name: "Alipay",
    data () {
      return {
        title: "",
        showNav: false
      }
    },
    async mounted () {
      this.title = this.$route.query.title
      const url = decodeURIComponent(this.$route.query.url)
      this.showNav = !this.isApp()
      this.showLoading()
      const result = await this.httpPost(url, {
        amount: parseInt(this.$route.query.amount)
      })
      if (result.success) { // 支付完成后会回调到recharge-finish页面
        let form = result.data
        const div = document.createElement("div")
        div.innerHTML = form
        document.body.appendChild(div)
        document.forms[0].submit()
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      }
    }
  }
</script>

<style scoped>

</style>
