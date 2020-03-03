/**
* @Author: limeng
* @Date: 2019-05-25 13:42
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      ref="topBar"
      title="提交任务"
      right-text="任务详情"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed"
      @click-right="onNavBarRightPressed">
    </van-nav-bar>
    <div class="under-nav flex1 flex-column height0 overflow-auto" v-if="missionDetail">
      <div class="top-section">
        <p>标&nbsp;&nbsp;&nbsp;题：{{missionDetail.title}}</p>
        <p>编&nbsp;&nbsp;&nbsp;号：{{missionId}}</p>
        <p>单&nbsp;&nbsp;&nbsp;价：{{missionDetail.price / 100}}元</p>
        <p>发布方：{{missionDetail.publishUserName}}</p>
        <p>支持设备：{{getSupportDeviceName()}}</p>
        <p>截止时间：{{missionDetail.deadlineTime}}</p>
        <!--<img alt="" src="">-->
        <!--<div class="img"></div>-->
      </div>
      <div class="flex-column flex-align-center">
        <div class="section sample-section">
          <p class="section-title">上传样例</p>
          <div style="height: 1px;background-color: #b3b3b3"></div>
          <div class="img-div">
            <img v-for="(item,index) in missionDetail.sampleImgList" :key="index" :src="imgFullPath(item)" alt=""
                 @click="onSampleImgClick(index)">
          </div>
        </div>
        <div class="section explain-section" v-if="missionDetail.explainList && missionDetail.explainList.length > 0">
          <p class="section-title">操作说明</p>
          <div style="height: 1px;background-color: #b3b3b3"></div>
          <div class="flex-row flex-align-center explain" v-for="(item,index) in missionDetail.explainList"
               :key="'explain' + index">
            <img alt="" :src="imgFullPath(item.img)" @click="onExplainImgClick(index)">
            <div class="flex-row flex-align-center info">
              <!--<span>{{index + 1}}</span>-->
              <div class="badge flex-row flex-center">
                <div class="inner flex-row flex-center">
                  <span>{{index + 1}}</span>
                </div>
              </div>
              <span>{{item.text}}</span>
            </div>
            <div class="dash-line"></div>
          </div>
        </div>

        <div class="section other-section">
          <p class="section-title">其他说明</p>
          <div style="height: 1px;background-color: #b3b3b3"></div>
          <p v-show="missionDetail.url" class="flex-row">
            <span style="flex-shrink: 0">链&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;接：</span>
            <span style="cursor: pointer;text-decoration: underline;display: inline-block;word-break: break-all;"
                  @click="onUrlPressed(missionDetail.url)">
        {{missionDetail.url}}</span>
          </p>
          <div class="dash-line"></div>
          <p>文字验证：{{missionDetail.textVerify}}</p>
          <div class="dash-line"></div>
          <p>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：{{missionDetail.remark}}</p>
        </div>

        <div class="section upload-section">
          <p class="section-title">我的上传</p>
          <div style="height: 1px;background-color: #b3b3b3"></div>
          <div class="upload">
            <p>1、上传图片</p>
            <div class="flex-row flex-align-center img-card-div">
              <div class="img-card" v-for="(item,index) in imgList"
                   :key="index" @click.stop="onImgItemClick(index)">
                <img-card :add-size="40" @onReadImg="onReadImg" @onDeleteImg="onDeleteImg(index)"
                          :img-content="item.content"/>
              </div>
              <div class="img-card" v-show="imgList.length < maxImgLength">
                <img-card :add-size="40" @onReadImg="onReadImg"/>
              </div>
            </div>
            <div v-show="missionDetail.textVerify">
              <p>2、文字验证</p>
              <textarea v-model="textVerify" class="text-verify-input" placeholder="请输入文字验证"></textarea>
            </div>
            <div class="flex-row flex-justify-center">
              <!--<button @click="onNavBarLeftPressed">取 消</button>-->
              <button @click="onFinishMissionButtonPressed">提 交</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { FINISH_MISSION_URL, MISSION_DETAIL_WITH_PUBLISHED_URL } from "@/config/host"
  import { getPropertyById } from "@/service/property"
  import { ImagePreview } from "vant"
  import ImgCard from "@/components/ImgCard"
  import { createFileContentArrayFromFileArray, uploadToQiniu } from "@/service/upload"

  export default {
    name: "FinishMission",
    components: { ImgCard },
    data () {
      return {
        missionId: null,
        missionDetail: null,
        vanSwipeWidth: 100,
        vanSwipeHeight: 130,
        textVerify: "",
        imgList: [],
        maxImgLength: 5
      }
    },
    async mounted () {
      this.missionId = this.$route.query.id
      const result = await this.httpPost(MISSION_DETAIL_WITH_PUBLISHED_URL, { id: this.missionId })
      if (result.success) {
        this.missionDetail = result.data
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      onNavBarRightPressed () {
        this.$router.push({ name: "MissionDetail", query: { id: this.missionId } })
      },
      getSupportDeviceName () {
        const p = getPropertyById(this.missionDetail.mobilePropertyId)
        return p.name
      },
      async onFinishMissionButtonPressed () {
        if (!this.imgList || this.imgList.length === 0) {
          this.toastFail("请添加验证图")
          return
        }
        if (this.missionDetail.textVerify && !this.textVerify) {
          this.toastFail("请添加文字验证")
          return
        }
        this.showLoading()
        await uploadToQiniu(this.imgList, (key) => {
          this.commitData(key)
        }, () => {
          this.toastFail("图片上传失败，请重试")
        })
      },
      async commitData (key) {
        const data = {
          missionId: this.missionId,
          textVerify: this.textVerify,
          key: key
        }
        const result = await this.httpPost(FINISH_MISSION_URL, data)
        if (result.success) {
          this.toastSuccess("任务上传完成，请等待审核")
          this.$router.back()
        }
      },
      onImgItemClick (itemIndex) {
        ImagePreview({
          images: createFileContentArrayFromFileArray(this.imgList),
          startPosition: itemIndex
        })
      },
      onSampleImgClick (index) {
        let imgs = []
        for (const e of this.missionDetail.sampleImgList) {
          imgs.push(this.imgFullPath(e))
        }
        ImagePreview({
          images: imgs,
          startPosition: index
        })
      },
      onExplainImgClick (index) {
        if (!this.missionDetail.explainList[index].img) {
          return
        }
        let imgs = [this.imgFullPath(this.missionDetail.explainList[index].img)]
        ImagePreview({
          images: imgs
        })
      },
      onReadImg (file, obj) {
        this.imgList.push(file)
      },
      onDeleteImg (index) {
        this.imgList.splice(index, 1)
      },
      onUrlPressed (url) {
        if (this.isApp()) { // app环境
          this.openUrl(url)
        } else { // 浏览器环境,新标签页打开
          window.open(url, "_blank")
        }
      }
    }
  }
</script>

<style scoped lang="less">

  .top-section {
    background-color: white;
    padding: 19px 14px;
    font-size: 14px;
    position: relative;

    .img {
      width: 58px;
      height: 58px;
      background-color: black;
      position: absolute;
      top: 56px;
      right: 54px;
    }

    p {
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
    font-size: 14px !important;
    color: #424242;
    margin-left: 16px;
    margin-top: 10px;
    margin-bottom: 9px;
  }

  .explain-section {

  }

  .other-section {
    p {
      font-size: 14px;
      color: #424242;
      margin: 6px 14px;
    }

    .dash-line {
      box-sizing: border-box;
      height: 1px;
      border-bottom: 1px dashed #b3b3b3;
      margin: 0 14px;
    }
  }

  .upload-section {
    p {
      font-size: 14px;
      color: #424242;
    }

    .upload {
      padding: 0 14px;
      margin-top: 6px;

      .text-verify-input {
        width: 250px;
        height: 50px;
        font-size: 14px;
        border: 1px solid #b3b3b3;
        border-radius: 4px;
        resize: none;
        color: black;

        &::-webkit-input-placeholder {
          font-size: 14px;
        }

        ::-moz-placeholder {
          font-size: 14px;
        }

        ::-moz-placeholder {
          font-size: 14px;
        }

        ::-ms-input-placeholder {
          font-size: 14px;
        }
      }

      .img-card-div {
        /*width: 140px;*/
        overflow-x: auto;
        overflow-y: hidden;
      }

      button {
        width: 200px;
        height: 36px;
        border-radius: 10px;
        text-align: center;
        font-size: 16px;
        color: #ffffff;
        margin-top: 60px;
        margin-bottom: 40px;

        /*&:first-child {*/
        /*background-color: #dd4944;*/
        /*}*/

        &:last-child {
          background-color: #81d4cb;
        }
      }
    }

    margin-bottom: 20px;
  }

  .img-card {
    width: 85px;
    height: 110px;
    flex-shrink: 0;
  }

  .title {
    color: black;
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
      border: 1px solid #009de2;
      border-radius: 50%;
      box-sizing: border-box;
      flex-shrink: 0;
      margin-right: 8px;

      .inner {
        width: 18px;
        height: 18px;
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

</style>
