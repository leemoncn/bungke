/**
* @Author: limeng
* @Date: 2019-05-19 09:46
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      ref="topBar"
      title="任务详情"
      right-text="接单规则"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed"
      @click-right="onNavBarRightPressed"/>

    <div class="under-nav overflow-auto" v-if="missionDetail">
      <div class="top-section">
        <p>标&nbsp;&nbsp;&nbsp;题：{{missionDetail.title}}</p>
        <p>编&nbsp;&nbsp;&nbsp;号：{{missionId}}</p>
        <p>单&nbsp;&nbsp;&nbsp;价：{{missionDetail.price / 100}}元</p>
        <p>发布方：{{missionDetail.publishUserName}}</p>

        <div class="flex-row buttons">
          <button class="button" v-if="missionDetail.deposit">
            <img src="@/assets/img/bao.png" alt=""/>
            {{missionDetail.deposit / 100}}元
          </button>
          <button class="button" @click="changeFollowStatus(!missionDetail.isFollowed)">
            {{missionDetail.isFollowed?"取消关注":"+ 关注"}}
          </button>
          <button class="button" @click="toShopPressed()">店&nbsp;&nbsp;&nbsp;铺</button>
        </div>
        <p v-show="missionDetail.textVerify">文字验证：{{missionDetail.textVerify}}</p>
        <p v-show="missionDetail.url" class="flex-row">
          <span style="flex-shrink: 0">任务链接：</span>
          <span style="cursor: pointer;text-decoration: underline;display: inline-block;word-break: break-all;"
                @click="onUrlPressed(missionDetail.url)">
        {{missionDetail.url}}</span>
        </p>
        <p>支持设备：{{getSupportDeviceName()}}</p>
        <p>截止时间：{{missionDetail.deadlineTime}}</p>
        <p v-show="missionDetail.remark">备注：{{missionDetail.remark}}</p>
      </div>

      <!--<div>90</div>-->
      <!--<div>fff</div>-->
      <div class="flex-column flex-align-center margin20">
        <div class="section sample-section">
          <p class="section-title">验证结果展示样例:</p>
          <div style="height: 1px;background-color: #b3b3b3"></div>
          <div class="img-div">
            <img v-for="(item,index) in missionDetail.sampleImgList" :key="index" :src="imgFullPath(item)" alt=""
                 @click="onSampleImgClick(index)">
          </div>
        </div>
        <div class="section explain-section" v-if="missionDetail.explainList && missionDetail.explainList.length > 0">
          <p class="section-title">操作说明:</p>
          <div style="height: 1px;background-color: #b3b3b3"></div>
          <div class="flex-row flex-align-center explain" v-for="(item,index) in missionDetail.explainList"
               :key="'explain' + index">
            <img alt="" :src="imgFullPath(item.img)" @click="onExplainImgClick(index)">
            <div class="flex-row flex-align-center info">
              <div class="badge flex-row flex-center">
                <div class="inner flex-row flex-center">
                  <span>{{index + 1}}</span>
                </div>
              </div>
              <span>{{item.text}}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="bottom-text" v-if="missionDetail.userId !== user.id">
        <p>1、完成任务后，请回到【我的任务】上传任务截图</p>
        <p>2、平台禁止发布一切法律所禁止的内容，如发现请向客服举报</p>
      </div>
      <div class="submit" ref="bottomBar" v-if="missionDetail.userId !== user.id">
        <button class="left-button" @click="onNavBarLeftPressed">放&nbsp;&nbsp;弃</button>
        <button @click="onAcceptMissionButtonPressed">抢&nbsp;&nbsp;单</button>
      </div>
    </div>

    <van-button type="default" size="mini" class="button-top"
                v-show="showImagePreview" @click="onRecognizedQRCode">识别二维码
    </van-button>
    <van-button type="default" size="mini" class="button-bottom"
                v-show="showImagePreview && isApp()" @click="onDownloadImg">保存图片
    </van-button>

  </div>
</template>

<script>
  import { ACCEPT_MISSION_URL, FOLLOW_URL, MISSION_DETAIL_WITH_PUBLISHED_URL } from "@/config/host"
  import { getPropertyById } from "@/service/property"
  import { Decoder } from "@nuintun/qrcode"
  import { Dialog, ImagePreview, Toast } from "vant"
  import { mapGetters } from "vuex"
  import { getDeviceType, isAndroid } from "@/service/device"
  import { goDownloadPage } from "@/service/function"

  export default {
    name: "MissionDetail",
    data () {
      return {
        missionId: null,
        missionDetail: null,
        showImagePreview: false,
        imagePreviewList: [],
        imagePreviewCurrentIndex: 0
      }
    },
    computed: {
      ...mapGetters([
        "user"
      ])
    },
    props: {},
    async mounted () {
      this.missionId = this.$route.query.id
      const result = await this.httpPostWithLoading(MISSION_DETAIL_WITH_PUBLISHED_URL, { id: this.missionId })
      if (result.success) {
        this.missionDetail = result.data
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      onNavBarRightPressed () {
      },
      getSupportDeviceName () {
        const p = getPropertyById(this.missionDetail.mobilePropertyId)
        return p.name
      },
      onSampleImgClick (index) {
        let imgs = []
        for (const e of this.missionDetail.sampleImgList) {
          imgs.push(this.imgFullPath(e))
        }
        this.imagePreviewList = imgs
        this.imagePreviewCurrentIndex = 0
        let _this = this
        ImagePreview({
          images: imgs,
          startPosition: index,
          onClose () {
            _this.showImagePreview = false
          },
          onChange (index) {
            _this.imagePreviewCurrentIndex = index
          }
        })
        this.showImagePreview = true
      },
      onExplainImgClick (index) {
        if (!this.missionDetail.explainList[index].img) {
          return
        }
        let imgs = [this.imgFullPath(this.missionDetail.explainList[index].img)]
        this.imagePreviewList = imgs
        this.imagePreviewCurrentIndex = 0
        let _this = this
        ImagePreview({
          images: imgs,
          onClose () {
            _this.showImagePreview = false
          },
          onChange (index) {
            _this.imagePreviewCurrentIndex = index
          }
        })
        this.showImagePreview = true
      },
      async onAcceptMissionButtonPressed () {
        const acceptDevice = getPropertyById(this.missionDetail.mobilePropertyId)
        if (acceptDevice && acceptDevice.id !== 18) {
          if (acceptDevice.id !== getDeviceType()) {
            Dialog.alert({
              message: `此任务只支持${acceptDevice.name}设备`
            }).then(() => {
            })
            return
          }
        }
        const result = await this.httpPostWithLoading(ACCEPT_MISSION_URL, { id: this.missionId })
        if (result.success) {
          this.toastSuccess("抢单成功")
          this.$store.commit("userNeedRemoveItemId", this.missionId)
          this.$router.back()
          if (!this.isApp() && isAndroid()) {
            Dialog.confirm({
              title: "提示",
              messageAlign: "left",
              message: "下载帮客APP版本可获取更高收益，是否下载？"
            }).then(() => {
              goDownloadPage()
            }).catch(() => {

            })
          }
        }
      },
      getMissionTypeName () {
        const p = getPropertyById(this.missionDetail.typePropertyId)
        return p.name
      },
      async onRecognizedQRCode () {
        this.showLoading("识别中...")
        const url = this.imagePreviewList[this.imagePreviewCurrentIndex]
        console.log("url = ", url)
        const qrcode = new Decoder()
        try {
          const result = await qrcode.scan(url)
          const recoUrl = result.data
          this.hideLoading()
          if (this.isApp()) { // app环境
            this.openUrl(recoUrl)
          } else { // 浏览器环境
            Dialog.confirm({
              message: "即将离开帮客，打开二维码的网址，是否打开？"
            }).then(() => {
              window.open(recoUrl)
            })
          }
        } catch {
          this.hideLoading()
          Dialog.alert({
            message: "识别失败"
          }).then(() => {
          })
        }
      },
      savePicture (url) {
        let _this = this
        Toast.loading({
          forbidClick: true,
          duration: 0,
          message: "下载中..."
        })
        var dtask = plus.downloader.createDownload(url, {}, function (d, status) {
          // 下载完成
          if (status === 200) {
            plus.gallery.save(d.filename, function () {
              _this.toastShortMsg("保存图片成功")
            }, function () {
              _this.toastShortMsg("保存图片失败")
            })
          } else {
            _this.toastShortMsg("保存图片失败")
          }
        })
        dtask.start()
      },
      onDownloadImg () {
        this.showLoading("保存中...")
        const url = this.imagePreviewList[this.imagePreviewCurrentIndex]
        this.savePicture(url)
      },
      async changeFollowStatus (followResult) {
        const result = await this.httpPost(FOLLOW_URL, {
          followId: this.missionDetail.userId,
          followResult: followResult
        })
        if (result.success) {
          let msg = ""
          if (followResult) {
            msg = "关注成功"
          } else {
            msg = "取消关注成功"
          }
          this.toastSuccess(msg)
          this.missionDetail.isFollowed = followResult
        }
      },
      onUrlPressed (url) {
        if (this.isApp()) { // app环境
          this.openUrl(url)
        } else { // 浏览器环境,新标签页打开
          window.open(url, "_blank")
        }
      },
      toShopPressed () {
        this.$router.push({
          name: "Shop",
          query: { id: this.missionDetail.userId + "", name: this.missionDetail.publishUserName }
        })
      }
    }
  }
</script>

<style scoped lang="less">

  /*.dash-line {*/
  /*box-sizing: border-box;*/
  /*height: 1px;*/
  /*border-bottom: 1px dashed #b3b3b3;*/
  /*margin: 0 14px;*/
  /*}*/

  .top-section {
    background-color: white;
    padding: 19px 14px;
    font-size: 14px;
    position: relative;

    p {
      &:not(:first-child) {
        margin-top: 8px;
      }

      font-size: 14px;
      color: #424242;

    }

    span {
      &:not(:first-child) {
        margin-top: 8px;
      }

      font-size: 14px;
      color: #424242;

    }
  }

  .section {
    background-color: white;
    border-radius: 8px;
    width: 360px;
    margin-top: 9px;
  }

  .sample-section {

    .img-div {
      padding: 11px 0;
      display: flex;
      overflow: auto;
      width: 100%;

      img {
        width: 68px;
        height: 120px;
        object-fit: cover;

        &:not(:first-child) {
          margin-left: 11px;
        }

        &:first-child {
          padding-left: 14px;
        }

        &:last-child {
          padding-right: 14px;
        }
      }
    }
  }

  .section-title {
    font-size: 12px;
    color: #424242;
    margin-left: 16px;
    margin-top: 10px;
    margin-bottom: 9px;
  }

  .explain-section {

  }

  .explain {
    margin: 0 14px;
    padding: 6px;
    box-sizing: border-box;
    /*height: 1px;*/

    &:not(:last-child) {
      border-bottom: 1px dashed #b3b3b3;
    }

    img {
      width: 68px;
      height: 120px;
      object-fit: cover;
      flex-shrink: 0;
    }

    .info {
      margin-left: 11px;
    }

    .badge {
      width: 24px;
      height: 24px;
      line-height: 24px;
      border: 1px solid #009de2;
      border-radius: 50%;
      box-sizing: border-box;
      flex-shrink: 0;
      margin-right: 8px;

      .inner {
        width: 18px;
        height: 18px;
        line-height: 18px;
        background-color: #009de2;
        border-radius: 50%;

        span {
          font-size: 13px;
          color: white;
          font-weight: bold;
        }
      }
    }

    span {
      font-size: 14px;
      color: #424242;
    }
  }

  .head-img {
    width: 36px;
    height: 36px;
    border-radius: 18px;
  }

  .info-container {
    background-color: white;
  }

  .container {
    height: 300px;
  }

  .screen-img {
    width: 300px;
  }

  .middle {
    background-color: white;
  }

  .flex-column-center {
    flex-direction: column;
    display: flex;
    justify-content: center;
  }

  .align-center {
    text-align: center;
  }

  .text-section {
    background-color: white;
    padding: 10px 0;
    margin-bottom: 20px;
  }

  .margin20 {
    margin-bottom: 20px;
  }

  .bottom-text {
    padding-left: 10px;
    padding-right: 10px;

    p:nth-child(1) {
      font-size: 12px;
      color: #dd4944;
      /*margin-top: 20px;*/
    }

    p:nth-child(2) {
      font-size: 12px;
      color: #868181;
    }
  }

  .submit {
    display: flex;
    background-color: transparent;
    justify-content: center;
    padding: 30px 40px;

    .left-button {
      margin-right: 40px
    }

    button {
      width: 94px;
      height: 36px;
      line-height: 36px;
      border-radius: 10px;
      font-size: 16px;
      color: #ffffff;
    }

    button:nth-child(1) {
      background-color: #dd4944;
    }

    button:nth-child(2) {
      background-color: #81d4cb;
    }
  }

  .button-top {
    top: 20px;
    right: 20px;
    position: fixed;
    z-index: 2147483647;
  }

  .button-bottom {
    bottom: 20px;
    right: 20px;
    position: fixed;
    z-index: 2147483647;
  }

  .buttons {
    position: absolute;
    right: 10px;
    top: 42px;

    .button {
      /*width: 858px;*/
      height: 30px;
      padding: 0 8px;
      line-height: normal;
      background-color: transparent;
      font-size: 12px;
      color: #424242;
      border-radius: 15px;
      border: 1px solid #81d4cb;
      display: flex;
      align-items: center;
      margin-left: 4px;
      flex-shrink: 0;

      img {
        height: 28px
      }
    }
  }

</style>
