/**
* @Author: limeng
* @Date: 2019-06-02 19:58
* @Description:
*/
<template>
  <div class="main">
    <div class="nav-bar flex-row flex-align-center">
      <div class="left flex-row flex-align-center" @click="()=>this.$router.back()">
        <i class="van-icon van-icon-arrow-left van-nav-bar__arrow"></i>
        <span class="">返回</span>
      </div>
      <div class="van-nav-bar__title van-ellipsis title">
        推广码
      </div>
      <div class="right flex-row flex-center" @click="sharePressed">{{isApp()?"分享":""}}</div>
    </div>
    <div class="top-bg">
      <div class="top-main">
        <img :src="user.headImgUrl?user.headImgUrl:defaultHeadImg" class="head" alt=""/>
        <div class="user-info">
          <div style="display: flex;align-items: center;flex: 1">
            <span class="name">{{user.nickname}}</span>
          </div>
          <div class="phone">
            <span>向您推荐</span>
            <span class="app"> "帮客APP"</span>
          </div>
        </div>
      </div>
    </div>
    <p class="info">
      投票、关注、浏览、注册...都可赚钱，无需任何投入
    </p>
    <p class="info">
      一元即可提现秒到账，大品牌安全放心，值得信赖
    </p>
    <p class="recommend">
      推荐好友，一起赚钱吧！
    </p>
    <div class="qrcode flex-column flex-align-center">
      <div class="top flex-row flex-center">
        <p>扫描二维码注册下载</p>
      </div>
      <div class=" flex-row flex-center">
        <img ref="qrcodeImg" :src="base64Img" alt="" :width="imgWidth" :height="imgHeight"/>
      </div>
      <div class="bottom flex-row flex-align-center" @click="sharePressed">
        <div v-show="isApp()" class="flex-row flex-center">
          <p>点击分享</p>
        </div>
      </div>
    </div>
    <img src="@/assets/img/share_bg.png" class="bg">
    <van-action-sheet
      v-model="showShareActionSheet"
      :actions="actions"
      description="请选择"
      cancel-text="取消"
      @cancel="onCancel"
      @select="onActionSheetSelect"
    />
  </div>
</template>

<script>
  import { QRCODE_IMG_URL } from "@/config/host"
  import { mapGetters } from "vuex"

  export default {
    name: "QRCode",
    data () {
      return {
        imgWidth: 150,
        imgHeight: 150,
        base64Img: null,
        url: null,
        showShareActionSheet: false,
        actions: [
          { name: "微信好友" },
          { name: "微信朋友圈" }
        ]
      }
    },
    computed: {
      ...mapGetters([
        "user"
      ])
    },
    async mounted () {
      const result = await this.httpPost(QRCODE_IMG_URL, {
        width: this.imgWidth,
        height: this.imgHeight
      })
      if (result.success) {
        this.base64Img = result.data.base64Img
        this.url = result.data.url
      }
    },
    methods: {
      wechatShare (scene) {
        let _this = this
        plus.share.getServices(function (s) {
          let wxShare
          for (const share of s) {
            if (share.id === "weixin") {
              wxShare = share
              break
            }
          }
          if (!wxShare) {
            _this.toastShortMsg("请先安装微信客户端")
            return
          }
          // scene 可取值： "WXSceneSession"分享到微信的“我的好友”； "WXSceneTimeline"分享到微信的“朋友圈”中； "WXSceneFavorite"分享到微信的“我的收藏”中。 默认值为"WXSceneSession"。
          let msg = {
            type: "web",
            title: "帮客APP，轻松赚取零花钱！",
            thumbs: ["http://img.bungke.com/icon-zhijiao.png"],
            content: "想赚零花钱？下载帮客APP，投票、关注、浏览、注册都可赚钱，一元即可提现！",
            href: _this.url,
            extra: { scene: scene }
          }
          // console.log("aaa = " + msg.href)
          // if (pic && pic.realUrl) {
          //   msg.pictures = [pic.realUrl]
          // }
          wxShare.send(msg, function () {
            // alert("分享完成")
            // this.toastMsg
          }, function (e) {
            // alert("分享失败：" + e.message)
          })
        }, function (e) {
          // alert("获取分享服务列表失败：" + e.message)
        })
      },
      sharePressed () {
        if (this.isApp()) {
          this.showShareActionSheet = true
        }
      },
      onCancel () {
        this.showShareActionSheet = false
      },
      onActionSheetSelect (item, index) {
        this.showShareActionSheet = false
        if (index === 0) { // 微信好友
          this.wechatShare("WXSceneSession")
        }
        if (index === 1) { // 微信朋友圈
          this.wechatShare("WXSceneTimeline")
        }
      },
      saveToDisk () {
        console.log("afdsa")
      }
    }
  }
</script>

<style scoped lang="less">
  .main {
    background-image: linear-gradient(#80d4cb, #56c5b9);
    height: 100%;
  }

  .nav-bar {
    height: @nav-height;

    .left {
      margin-left: 20px;
      height: @nav-height;
      width: 70px;
      color: white !important;

      span {
        color: white !important;
        margin-left: 6px;
      }
    }

    .right {
      margin-right: 20px;
      width: 70px;
      font-size: 14px;
      color: white !important;
      height: @nav-height;

      span {
        color: white !important;
      }
    }

    .title {
      color: white;

    }
  }

  .bg {
    width: 100%;
    position: absolute;
    bottom: 0;
    z-index: 0;
  }

  .top-bg {
    width: 100%;
    height: 80px;
    align-items: center;
    display: flex;

    .top-main {
      display: flex;
      width: 100%;
      align-items: center;
      margin-bottom: 20px
    }

    .head {
      width: 58px;
      height: 58px;
      border-radius: 29px;
      margin: 0 18px 0;
    }

    .user-info {
      flex: 1;
      /*display: flex;*/
      /*flex-direction: column;*/

      .name {
        color: white;
        font-size: 16px;
      }

      .phone {
        font-size: 16px;

        span {
          color: white;
          font-size: 16px;
          vertical-align: bottom;
        }

        .app {
          font-size: 16px;
          color: #faf107;
        }
      }
    }
  }

  .info {
    color: white;
    font-size: 14px;
    margin: 0 30px;
    white-space: nowrap;
  }

  .recommend {
    margin: 20px auto;
    font-size: 24px;
    color: #ffffff;
  }

  .qrcode {
    text-align: center;
    position: relative;
    z-index: 1;
    margin: 0 auto;
    width: 246px;
    height: 318px;
    background-image: url("../../assets/img/qrcode_bg.png");
    background-repeat: no-repeat;
    background-size: 100% 100%;
    -moz-background-size: 100% 100%;

    .top {
      height: 130px;

      p {
        font-size: 18px;
        color: #d63e3d;
      }
    }

    .bottom {
      height: 150px;

      div {
        width: 122px;
        height: 30px;
        border-radius: 15px;
        font-size: 14px;
        color: white;
        background-color: #d63e3d;
      }
    }
  }
</style>
