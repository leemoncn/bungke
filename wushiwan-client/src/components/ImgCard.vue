/**
* 这个组件在用的使用，一定要有一个外层的DIV控制他的大小
* @Author: limeng
* @Date: 2019-07-23 20:26
* @Description:
*/
<template>
  <div class="parent">
    <div class="main flex-row flex-center">
      <div class="bg bg-border bg-size flex-row flex-center" v-show="!imgContent"><!--显示没有图片的时候的加号-->
        <!--app环境使用plus，浏览器环境使用van-uploader-->
        <van-icon name="add-o" :size="px2rem(addSize?addSize:80) + 'rem'" color="#969799"
                  @click="()=>{ showImgActionSheet = true}"
                  v-if="isApp()"/>
        <van-uploader v-else
                      name="imgCard"
                      :after-read="onRead">
          <van-icon name="add-o" :size="px2rem(addSize?addSize:80) + 'rem'" color="#969799" style="margin-top: 6px"/>
        </van-uploader>
      </div>
      <div v-show="imgContent" class="bg-size " style="position: relative"><!--显示图片和删除符号-->
        <div class="delete flex-row flex-center">
          <van-icon name="clear" :size="px2rem(deleteSize?deleteSize:30) + 'rem'"
                    color="#969799"
                    @click.stop="onDeleteImg"/>
        </div>
        <img :src="realImgContent" class="bg" style="width: 100%;height: 100%" alt="" @click="onImgPressed">
      </div>
    </div>
    <van-action-sheet
      v-model="showImgActionSheet"
      :actions="actions"
      description="请选择"
      cancel-text="取消"
      @cancel="onCancel"
      @select="onActionSheetSelect"
    />
  </div>
</template>

<script>

  import { imgFullPath } from "@/util/img"

  export default {
    name: "ImgCard",
    data () {
      return {
        // imgContent: null
        showImgActionSheet: false,
        actions: [
          { name: "相机" },
          { name: "照片图库" }
        ]
      }
    },
    props: {
      deleteSize: {
        type: Number
      },
      addSize: {
        type: Number
      },
      imgContent: {
        type: String
      },
      name: {
        type: String
      }
    },
    computed: {
      realImgContent () {
        if (!this.imgContent) {
          return this.imgContent
        }
        if (this.imgContent.startsWith("data:image")) { // base64图片
          return this.imgContent
        }
        return imgFullPath(this.imgContent)
      }
    },
    watch: {},
    methods: {
      async onRead (file, obj) { // 网页版选择图片的回调
        this.$emit("onReadImg", file, this.name)
      },
      onDeleteImg () {
        // 默认将imgcontent传回去，但是一般用这个组件的父类，都会复写这个方法，接受index参数
        this.$emit("onDeleteImg", this.imgContent)
      },
      onImgPressed () {
        // 这个方法要保留，不然点击会有偏移，不知道为什么
      },
      onCancel () {
        this.showImgActionSheet = false
      },
      onActionSheetSelect (item, index) {
        this.showImgActionSheet = false
        if (index === 0) { // 相机
          this.pickFromCamera(true)
        }
        if (index === 1) { // 照片图库
          this.pickFromCamera(false)
        }
      },
      emitParentImg (path) {
        let _this = this
        plus.io.resolveLocalFileSystemURL(path, function (entry) {
          entry.file(async function (file) {
            let reader = new plus.io.FileReader()
            reader.onloadend = function (e) {
              let obj = {
                file: file,
                content: e.target.result
              }
              _this.$emit("onReadImg", obj, _this.name)
            }
            reader.readAsDataURL(file)
          }, function (e) {
            console.log("读取文件失败", e.message)
          })
        })
      },
      pickFromCamera (isFromCamera) {
        let _this = this
        if (isFromCamera) { // 相机
          let cmr = plus.camera.getCamera()
          let res = cmr.supportedImageResolutions[0]
          let fmt = cmr.supportedImageFormats[0]
          cmr.captureImage(function (path) {
              _this.emitParentImg(path)
            },
            function (error) {
              console.log("Capture image failed: " + error.message)
            },
            { resolution: res, format: fmt }
          )
        } else { // 照片图库
          plus.gallery.pick(async function (path) {
            _this.emitParentImg(path)
          }, function (e) {
            console.log("取消选择图片")
          }, { filter: "image" })
        }
      }
    }
  }
</script>

<style scoped lang="less">
  .bg-border {
    border: 1px solid #969799;
    box-sizing: border-box;
  }

  .bg {
    border-radius: 10px;
    background-color: #f3f5f9;
    object-fit: cover;
  }

  .bg-size {
    width: calc(100% - 8px);
    height: calc(100% - 8px);
  }

  .main {
    width: 100%;
    height: 100%;

    .delete {
      background-color: white;
      position: absolute;
      border-radius: 50%;
      left: -4px; /*这个4像素是bg-size中减去像素的一半*/
      top: -4px;
      padding: 0.4px;
    }
  }

  .parent {
    width: 100%;
    height: 100%;
  }

  /*.swipe-item {*/
  /*display: flex;*/
  /*align-items: center;*/
  /*justify-content: center;*/

  /*.delete {*/
  /*display: flex;*/
  /*align-items: center;*/
  /*justify-content: center;*/
  /*width: 30px;*/
  /*height: 30px;*/
  /*background-color: white;*/
  /*position: absolute;*/
  /*border: 2px;*/
  /*border-radius: 50%;*/
  /*left: 0;*/
  /*top: 0*/
  /*}*/
  /*}*/

  /*.swipe-img {*/
  /*width: calc(100% - 8px);*/
  /*height: calc(100% - 8px);*/
  /*border: 2px;*/
  /*border-radius: 10px;*/
  /*background-color: white;*/
  /*object-fit: cover;*/
  /*}*/

  /*.plus-div {*/
  /*display: flex;*/
  /*justify-content: center;*/
  /*align-items: center;*/
  /*}*/
</style>
