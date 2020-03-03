/**
* @Author: leemon
* @Date: 2019-08-13 16:15
* @Description:
*/
<template xmlns:v-popover="http://www.w3.org/1999/xhtml">
  <div v-if="imgPathList || imgData">
    <el-popover
      ref="popover"
      placement="right"
      @show="onShow"
      trigger="click">
      <div class="flex-row flex-center" v-if="!imgList">
        <span>加载中...</span>
      </div>
      <div v-else class="img">
        <template v-if="imgList && imgList.length > 0">
          <div v-for="(item,index) in imgList" :key="index">
            <img :src="imgFullPath(item)" alt="">
            <el-divider>↑ 图片{{index + 1}} ↑</el-divider>
          </div>
        </template>
        <div v-else>无图片</div>
      </div>
    </el-popover>
    <el-tag v-popover:popover>{{imgCount}}</el-tag>
  </div>
</template>

<script>
  import { COREIMG_QUERY_URL } from "@/config/host"

  export default {
    name: "ImgPopover",
    data () {
      return {
        imgList: null
      }
    },
    props: {
      imgPathList: {
        type: Array
      },
      imgData: {
        // 两个属性：dataId type
        type: Object
      }
    },
    computed: {
      imgCount () {
        if (this.imgPathList) {
          if (this.imgPathList.length === 0) {
            return "无图片"
          }
          return `图片${this.imgPathList.length}张`
        }
        if (this.imgData) {
          return "点击查看"
        }
        return "图片数据未加载"
      }
    },
    watch: {
      // imgData: {
      //   async handler (newObj, oldObj) {
      //     console.log("this = ", this)
      //     if (!oldObj || newObj.dataId !== oldObj.dataId || newObj.type !== oldObj.type) {
      //       // this.loadUserInfo()
      //     }
      //   },
      //   immediate: true,
      //   deep: true
      // }
    },
    mounted () {

    },
    methods: {
      loadUserInfo () {
        this.httpPost(COREIMG_QUERY_URL, this.imgData)
          .then((result) => {
            if (result.success) {
              let list = []
              for (const item of result.data) {
                list.push(item.path)
              }
              this.imgList = list
            } else {
              this.imgList = []
            }
          }).catch(() => {
          this.imgList = []
        })
      },
      onShow () {
        if (this.imgData) {
          this.loadUserInfo()
        }
        if (this.imgPathList) {
          this.imgList = this.imgPathList
        }
      }
    }
  }
</script>

<style scoped lang="less">
  .img {
    max-width: 30vw;
    max-height: 70vh;
    text-align: center;
    overflow-x: hidden;
    overflow-y: auto;

    img {
      max-width: 30vw;
    }
  }
</style>
