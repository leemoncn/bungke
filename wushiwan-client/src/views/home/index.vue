/**
* @Author: limeng
* @Date: 2019-03-31 21:35
* @Description:
*/
<template>
  <div class="column-view">
    <div class="overflow-auto no-nav-tab">
      <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
        <div v-if="homeData">
          <div class="top">
            <div class="bg">
              <div class="nav-bar flex-column flex-center van-nav-bar__title van-ellipsis">
                帮客
              </div>
              <swiper :options="bannerSwiperOption" ref="bannerSwiper">
                <swiper-slide v-for="(banner, index) in homeData.bannerList" :key="index"
                              @click.native="onBannerItemPressed(banner,index)">
                  <div class="flex-column flex-center">
                    <!--swiper组件的loop属性和懒加载不兼容，有问题-->
                    <img
                      v-lazy="{src:imgFullPath(banner.img),error:require('@/assets/img/banner_default.png'),loading:require('@/assets/img/banner_default.png')}"/>
                  </div>
                </swiper-slide>
                <div class="swiper-pagination" slot="pagination"></div>
              </swiper>
            </div>
          </div>
          <div class="notice flex-row flex-align-center">
            <img class="icon" src="@/assets/img/laba.png"/>
            <swiper :options="noticeSwiperOption" class="swiper" ref="noticeSwiper">
              <swiper-slide v-for="(notice, index) in homeData.noticeList" :key="index"
                            @click.native="onNoticeItemPressed(notice,index)">
                <p>{{notice.text}}</p>
              </swiper-slide>
            </swiper>
          </div>

          <div class="bottom" v-show="!isInReview() && !isWechat()">
            <!--<p class="title">精品娱乐</p>-->
            <!--<div class="line"></div>-->
            <div class="flex-row flex-align-center">
              <div class="flex1 flex-column flex-align-center column" @click="onBottomButtonPressed(0)">
                <img src="@/assets/img/bottom-menu-1.png" style="width: 40px;height: 40px">
                <span style="font-size: 12px ">影视大片</span>
              </div>
              <div class="flex1 flex-column flex-align-center column" @click="onBottomButtonPressed(1)">
                <img src="@/assets/img/bottom-menu-2.png" style="width: 40px;height: 40px">
                <span style="font-size: 12px ">精彩小说</span>
              </div>
              <div class="flex1 flex-column flex-align-center column" @click="onBottomButtonPressed(2)">
                <img src="@/assets/img/bottom-menu-3.png" style="width: 40px;height: 40px">
                <span style="font-size: 12px ">极品漫画</span>
              </div>
              <div class="flex1 flex-column flex-align-center column" @click="onBottomButtonPressed(3)">
                <img src="@/assets/img/bottom-menu-4.png" style="width: 40px;height: 40px">
                <span style="font-size: 12px ">内涵段子</span>
              </div>
              <div class="flex1 flex-column flex-align-center column" @click="onBottomButtonPressed(4)">
                <img src="@/assets/img/bottom-menu-5.png" style="width: 40px;height: 40px">
                <span style="font-size: 12px ">爆笑视频</span>
              </div>
            </div>
          </div>
          <div class="main">
            <div style="height: 50%" class="flex-row">
              <div style="width: 50%;height: 100% ;background:#fff  ;" class="flex-column flex-align-center"
                   @click="onAcceptMissionButtonPressed">
                <img src="@/assets/img/main-menu1.png">
                <span class="title">接单赚钱</span>
                <span class="info" v-show="!isInReview()">投票、关注、浏览都可赚钱</span>
              </div>
              <div style="width: 50%;height: 100% ;background:#fff;margin-left: 10px" class="flex-column flex-align-center"
                   @click="onCreateMissionButtonPressed">
                <img src="@/assets/img/main-menu2.png">
                <span class="title">发布任务</span>
                <span class="info" v-show="!isInReview()">要粉丝？要投票？有事您说话</span>
              </div>
            </div>
            <div style="height: 50%;margin-top: 10px" class="flex-row">
              <div style="width: 50%;height: 100% ;background:#fff" class="flex-column flex-align-center"
                   @click="onMyQRCodeButtonPressed">
                <img src="@/assets/img/main-menu3.png">
                <span class="title">我的二维码</span>
                <span class="info" v-show="!isInReview()">推荐好友赚佣金，好友够多你可不劳而获</span>
              </div>
              <div style="width: 50%;height: 100% ;background:#fff ;margin-left: 10px" class="flex-column flex-align-center"
                   @click="onMyMissionButtonPressed">
                <img src="@/assets/img/main-menu4.png">
                <span class="title">我的任务</span>
                <span class="info" v-show="!isInReview()">已接单的任务，完成以后来这儿上传验证图</span>
              </div>
            </div>
          </div>
          <div class="flex-row flex-align-center middle-menu" style="margin-top: 20px; margin-bottom: 20px" v-show="!isInReview()">
            <img class="left" src="@/assets/img/middle-button-left.png" @click="onMiddleLeftPressed">
            <div class="flex1"></div>
            <img class="right" src="@/assets/img/middle-button-right.png" @click="onMiddleRightPressed">
          </div>

        </div>




      </van-pull-refresh>
    </div>
  </div>
</template>

<script>

  import { HOME_DATA_URL } from "@/config/host"
  import { isAndroid, isWechat } from "@/service/device"
  import { Dialog } from "vant"
  import { goDownloadPage } from "@/service/function"

  export default {
    name: "Home",
    data () {
      return {
        homeData: null,
        isRefreshing: false,
        bannerSwiperOption: {
          autoplay: {
            delay: 3000
          },
          pagination: {
            el: ".swiper-pagination",
            type: "bullets",
            // clickable: true,
            click () {
              console.log("fasdfd")
            }
          }
        },
        noticeSwiperOption: {
          direction: "vertical",
          autoplay: {
            delay: 2000
          },
          speed: 1500
        }
      }
    },
    async mounted () {
      this.loadData()
      if (!this.isApp() && isAndroid() && isWechat()) {
        Dialog.confirm({
          title: "提示",
          messageAlign: "left",
          message: "下载帮客APP版本可观看腾讯视、爱奇艺、优酷、芒果等VIP影视及各类小说、漫画，是否下载？"
        }).then(() => {
          goDownloadPage()
        }).catch(() => {

        })
      }
    },
    activated () {
      this.reloadSwiper()
    },
    computed: {},
    watch: {
      "$store.state.tabbar.activeItemIndex": function (index) {
        if (index === 0) {
          this.reloadSwiper()
        }
      }
    },
    methods: {
      async loadData () {
        const result = await this.httpPostWithLoading(HOME_DATA_URL)
        if (result.success) {
          this.homeData = result.data
        }
      },
      onCreateMissionButtonPressed () {
        this.$router.push({ name: "CreateMission" })
      },
      onAcceptMissionButtonPressed () {
        this.$router.push({ name: "AcceptMission" })
      },
      onMyMissionButtonPressed () {
        this.$router.push({ name: "MyMission" })
      },
      onMyQRCodeButtonPressed () {
        this.$router.push({ name: "QRCode" })
      },
      dialog (text) {
        alert(text)
      },
      reloadSwiper () {
        if (this.$refs.bannerSwiper) {
          this.$refs.bannerSwiper.swiper.autoplay.stop()
          this.$refs.bannerSwiper.swiper.autoplay.start()
        }
        if (this.$refs.noticeSwiper) {
          this.$refs.noticeSwiper.swiper.autoplay.stop()
          this.$refs.noticeSwiper.swiper.autoplay.start()
        }
      },
      onNoticeItemPressed (notice, index) {
        if (notice.html) {
          this.$store.commit("updateHtml", notice.html)
          this.$router.push({ name: "HtmlPage" })
        }
      },
      onBannerItemPressed (banner, index) {
        if (banner.html) {
          this.$store.commit("updateHtml", banner.html)
          this.$router.push({ name: "HtmlPage" })
        }
      },
      onRefresh () {
        this.loadData()
        this.isRefreshing = false
      },
      onBottomButtonPressed (index) {
        if (!this.isApp() && isAndroid()) {
          Dialog.confirm({
            title: "提示",
            messageAlign: "left",
            message: "下载帮客APP版本可获得更快的加载速度，是否下载？"
          }).then(() => {
            goDownloadPage()
          }).catch(() => {

          })
        }
        let track = ""
        switch (index) {
          case 0:// 影视
            this.$store.commit("updateHtml", "https://app.movie")
            track = "movie"
            break
          case 1:// 晓说
            this.$store.commit("updateHtml", "https://dingdian.org.cn")
            track = "book"
            break
          case 2:// 漫画
            this.$store.commit("updateHtml", "https://www.wenyamh.com/")
            track = "cartoon"
            break
          case 3:// 内涵段子
            this.$store.commit("updateHtml", "https://heibaimanhua.xiner.store/duanzishou")
            track = "neihanduanzi"
            break
          case 4:// 爆笑视频
            this.$store.commit("updateHtml", "https://cpu.baidu.com/1033/aa16521e")
            track = "baoxiaoshipin"
            break
        }
        this.$router.push({ name: "HtmlPage", query: { track: track } })
      },
      onMiddleLeftPressed () {
        this.$store.commit("updateHtml", "http://www.tuzibanghuan.com//index.php/Api/Wqtweb/index.html?uid=530")
        this.$router.push({ name: "HtmlPage", query: { isApplyCreditCardPage: "1", track: "middle-left" } })
      },
      onMiddleRightPressed () {
        this.$store.commit("updateHtml", "http://baiying.tlswpay.com/index.php/Distribution/Share/reg/bg/1/req_user_id/0159YM9XOP")
        this.$router.push({ name: "HtmlPage", query: { track: "middle-right" } })
      }
    }
  }
</script>

<style scoped lang="less">
  .top {
    height: 206px;

    .bg {
      height: 177px;
      background-image: linear-gradient(#d63828, #f6e18f);
      font-size: 0;

      .nav-bar {
        height: @nav-height;
        color: white;
      }

      img {
        width: 356px;
        height: 154px;
        border-radius: 10px;
        object-fit: fill;
      }
    }
  }

  .notice {
    background-color: white;
    color: #787878;

    .swiper {
      margin-left: 10px;
      margin-right: 0;
      height: 29px;
    }

    p {
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      font-size: 14px;
      width: 100%;
      line-height: 29px;
    }

    .icon {
      width: 17px;
      height: 16px;
      margin-left: 11px;
    }
  }

  .middle-menu {
    margin-top: 10px;

    .left {
      margin-left: 12px;
      width: 170px;
      height: 62px;
      border-radius: 4px;
    }

    .right {
      margin-right: 12px;
      width: 170px;
      height: 62px;
      border-radius: 4px;
    }
  }

  .main {
    width: 348px;
    height: 339px;
    background-color: #00000000;
    border-radius: 5px;
    margin: 12px auto 0 auto;

    img {
      width: 65px;
      height: 65px;
      margin-top: 16px;
    }

    .title {
      margin-top: 11px;
      font-size: 16px;
      color: #424242;
    }

    .info {
      font-size: 12px;
      color: #949393;
      text-align: center;
      margin: 7px 10px 0 10px;
    }
  }

  .bottom {
    width: 346px;
    height: 80px;
    background-color: #ffffff;
    border-radius: 5px;

    margin: 10px auto;
    /*padding-top: 9px;*/

    .title {
      font-size: 16px;
      color: #424242;
      margin: 0 0 9px 9px;
    }

    .line {
      background-color: #f3f3f3;
      height: 1px;
    }

    img {
      width: 52px;
      height: 52px;
    }

    .column {
      margin-top: 12px;
      justify-content: space-around;

      span {
        margin-top: 6px;
      }
    }
  }
</style>
