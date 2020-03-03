/**
* @Author: leemon
* @Date: 2019-08-08 10:25
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="意见反馈"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed"/>
    <div class="under-nav" style="background-color: white">
      <van-cell-group class="main" :border="false">
        <van-field
          v-model="message"
          type="textarea"
          placeholder="请输入意见反馈（必填）"
          rows="5"
          autosize
          :border="false" class="field"
        />
      </van-cell-group>
      <van-cell-group class="sub" title-style="title" :border="false" title="图片（可选）">
        <div class="flex-row flex-align-center">
          <div class="img-card" v-for="(item,index) in imgFileList"
               :key="index" @click.stop="imgCardPressed(index)">
            <img-card :add-size="50" @onReadImg="onReadImg" @onDeleteImg="onDeleteImg"
                      :img-content="item.content" :delete-size="25"/>
          </div>
          <div class="img-card" v-show="imgFileList.length < maxImgLength">
            <img-card :add-size="50" @onReadImg="onReadImg"/>
          </div>
        </div>
      </van-cell-group>
      <van-cell-group class="sub">
        <van-field v-model="contactInfo" placeholder="请输入微信号或者QQ号"/>
      </van-cell-group>
    </div>
    <div class="commit">
      <van-button plain size="large" @click="onCommitPressed">提交</van-button>
    </div>
  </div>
</template>

<script>
  import ImgCard from "@/components/ImgCard"
  import { ADVICE_URL } from "@/config/host"
  import { createFileContentArrayFromFileArray, uploadToQiniu } from "@/service/upload"
  import { ImagePreview } from "vant"

  export default {
    name: "Advice",
    components: { ImgCard },
    data () {
      return {
        message: "",
        imgFileList: [],
        maxImgLength: 3,
        show: false,
        contactInfo: ""
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      imgCardPressed (itemIndex) {
        ImagePreview({
          images: createFileContentArrayFromFileArray(this.imgFileList),
          startPosition: itemIndex
        })
      },
      onReadImg (obj) {
        this.imgFileList.push(obj)
      },
      onDeleteImg (index) {
        this.imgFileList.splice(index, 1)
      },
      async commitData (key) {
        const result = await this.httpPost(ADVICE_URL, {
          msg: this.message,
          contactInfo: this.contactInfo,
          key: key
        })
        if (result.success) {
          this.toastMsg("提交完成，我们会尽快和您取得联系")
          this.$router.back()
        }
      },
      async onCommitPressed () {
        if (!this.contactInfo) {
          this.toastMsg("请输入联系方式")
          return
        }
        if (!this.message) {
          this.toastMsg("请输入意见反馈")
          return
        }
        this.showLoading()
        await uploadToQiniu(this.imgFileList, (key) => {
          this.commitData(key)
        }, () => {
          this.toastFail("图片上传失败，请重试")
        })
      }
    }
  }
</script>

<style scoped lang="less">
  .main {
    padding-left: 20px;
    padding-right: 20px;
    margin-top: 10px;
  }

  .sub {
    padding-left: 20px;
    padding-right: 20px;
  }

  .field {
    border: 1px solid #7d7e80;
    border-radius: 8px;
  }

  .img-card {
    width: 70px;
    height: 70px;
  }

  .title {
    color: black;
  }

  .commit {
    margin: 20px 20px 0;
  }

</style>
