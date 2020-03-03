/**
* @Author: limeng
* @Date: 2019-07-31 20:18
* @Description:
*/
<template>
  <van-dialog
    v-model="show"
    :title="title"
    class="upload-img-dialog"
    @confirm="onDialogConfirm"
    :before-close="beforeClose"
    show-cancel-button>
    <van-cell-group class="group" :border="false">
      <van-field
        v-model="text"
        type="textarea"
        :placeholder="textPlaceHolder"
        rows="3"
        :border="false" class="field"/>
      <div class="flex-row flex-align-center" style="">
        <div class="img-card" v-for="(item,index) in imgList"
             :key="index" @click.stop="imgCardPressed(index)">
          <img-card :add-size="50" @onReadImg="onReadImg" @onDeleteImg="onDeleteImg(index)"
                    :img-content="item.content"/>
        </div>
        <div class="img-card" v-show="imgList.length < maxImgLength">
          <img-card :add-size="50" @onReadImg="onReadImg"/>
        </div>
      </div>
      <span v-if="info">{{info}}</span>
    </van-cell-group>
  </van-dialog>
</template>

<script>
  import ImgCard from "@/components/ImgCard"
  import { ImagePreview } from "vant"
  import { createFileContentArrayFromFileArray } from "@/service/upload"

  export default {
    name: "UploadImgDialog",
    components: { ImgCard },
    data () {
      return {
        text: "",
        imgList: [],
        show: false
      }
    },
    watch: {},
    props: {
      maxImgLength: {
        type: Number
      },
      textPlaceHolder: {
        type: String
      },
      closable: {
        type: Boolean,
        required: true
      },
      info: {
        type: String
      },
      title: {
        type: String,
        required: true
      }
    },
    methods: {
      onDialogConfirm () {
        this.$emit("onDialogConfirm", this.text, this.imgList)
      },
      onReadImg (file, obj) {
        this.imgList.push(file)
      },
      onDeleteImg (index) {
        this.imgList.splice(index, 1)
      },
      imgCardPressed (index) {
        ImagePreview({
          images: createFileContentArrayFromFileArray(this.imgList),
          startPosition: index
        })
      },
      beforeClose (action, done) {
        if (action === "confirm") {
          if (!this.closable) {
            done(false)
            return
          }
        }
        done()
      },
      showDialog () {
        this.text = ""
        this.imgList = []
        this.show = true
      }
    }
  }
</script>

<style scoped lang="less">
  .upload-img-dialog {

    .group {
      padding-left: 20px;
      padding-right: 20px;
      margin-top: 10px;

      .img-card {
        width: 70px;
        height: 70px
      }

    }

    .field {
      border: 1px solid #7d7e80;
      border-radius: 8px;
    }

  }
</style>
