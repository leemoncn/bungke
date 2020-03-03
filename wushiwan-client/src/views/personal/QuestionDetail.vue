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

    <div class="under-nav flex1 height0 overflow-auto main">
      <p class="title">{{title}}</p>
      <div class="line"></div>
      <p class="msg">{{msg}}</p>
    </div>
  </div>
</template>

<script>

  export default {
    name: "Question",
    data () {
      return {
        title: "",
        msg: ""
      }
    },
    mounted () {
      this.title = this.$route.query.title
      this.msg = this.$route.query.msg
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      }
    }
  }
</script>

<style scoped lang="less">
  .main {
    padding: 0 12px;

    .title {
      text-align: center;
      font-size: 18px;
      font-weight: 400;
      margin: 20px 0;
    }

    .line {
      height: 1px;
      background-color: #f5f6f7;
    }

    .msg {
      font-size: 16px;
      line-height: 28px;
      margin-top: 20px;
    }
  }

</style>
