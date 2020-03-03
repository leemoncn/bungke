/**
* @Author: limeng
* @Date: 2019-12-14 16:30
* @Description:
*/
<template>
  <div class="weixin-tip" ref="tip" v-show="isShow">
    <p>
      <img src="@/assets/img/live_weixin.png" alt="微信打开">
      <span id="close" title="关闭" class="close" @click="onClosePressed">×</span>
    </p>
  </div>
</template>

<script>

  export default {
    name: "Download",
    data () {
      return {
        isShow: true
        // apkUrl: "http://apk.bungke.com/bungke.apk"
      }
    },
    created () {

    },
    mounted () {
      let winHeight = typeof window.innerHeight !== "undefined" ? window.innerHeight : document.documentElement.clientHeight // 兼容IOS，不需要的可以去掉
      this.$refs.tip.height = winHeight + "px" // 兼容IOS弹窗整屏
      // if (isWechat()) {
      //   this.isShow = true
      // } else {
      //   // Toast.loading({
      //   //   forbidClick: true,
      //   //   duration: 0,
      //   //   message: "文件下载中..."
      //   // })
      //   // window.location = this.apkUrl
      // }
    },
    methods: {
      onClosePressed () {
        // this.isShow = false
        // this.$store.commit("changeActiveItemIndex", 0)
        // this.$router.replace("/")
      }
    }
  }
</script>

<style scoped lang="less">
  .weixin-tip {
    position: fixed;
    left: 0;
    top: 0;
    background: rgba(0, 0, 0, 0.8);
    filter: alpha(opacity=80);
    width: 100%;
    height: 100%;
    z-index: 100;

    p {
      text-align: center;
      margin-top: 10%;
      padding: 0 5%;
      position: relative;
    }

    img {
      width: 100%;
    }

    .close {
      color: #fff;
      padding: 5px;
      font: bold 20px/24px simsun;
      text-shadow: 0 1px 0 #ddd;
      position: absolute;
      top: 0;
      left: 5%;
    }
  }
</style>
