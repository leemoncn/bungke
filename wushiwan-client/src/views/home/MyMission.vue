/**
* @Author: limeng
* @Date: 2019-05-19 20:50
* @Description:
*/
<template>
  <div class="column-view">
    <div ref="topBar">
      <van-nav-bar
        title="我的任务"
        right-text="接任务"
        left-text="返回"
        fixed
        left-arrow
        @click-left="onNavBarLeftPressed"
        @click-right="onNavBarRightPressed">
      </van-nav-bar>
    </div>
    <div class="top-select-bar under-nav">
      <div class="tab-div" :class="{'show-border-bottom':showBottomLine[0]}" @click="onTopTabClick(0)">
        <span :class="{'selected-color':showBottomLine[0]}">接单记录</span>
      </div>
      <div class="center-div"></div>
      <div class="tab-div" :class="{'show-border-bottom':showBottomLine[1]}" @click="onTopTabClick(1)">
        <span :class="{'selected-color':showBottomLine[1]}">我的发布</span>
      </div>
    </div>
    <div style="flex: 1;">
      <my-mission-accept v-show="showBottomLine[0]"/>
      <my-mission-publish v-show="showBottomLine[1]" :will-show="showBottomLine[1]"/>
    </div>

  </div>
</template>

<script>
  import Vue from "vue"
  import MyMissionAccept from "@/views/home/MyMissionAccept"
  import MyMissionPublish from "@/views/home/MyMissionPublish"

  export default {
    name: "MyMission",
    components: { MyMissionPublish, MyMissionAccept },
    data () {
      return {
        showBottomLine: [true, false]
      }
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      onNavBarRightPressed () {
        this.$router.push({ name: "AcceptMission" })
      },
      onTopTabClick (index) {
        const arr = [false, false]
        if (index === 0) {
          arr[0] = true
          arr[1] = false
        } else {
          arr[1] = true
          arr[0] = false
        }
        Vue.set(this, "showBottomLine", arr)
      }
    }
  }
</script>

<style scoped lang="less">
  @top-select-bar-height: 38px;

  .top-select-bar {
    background-color: white;
    height: @top-select-bar-height;
    text-align: center;
    padding-left: 20px;
    padding-right: 20px;
    /*padding-bottom: 10px;*/
    border-bottom: #f3f3f3 1px solid;

    .tab-div {
      display: inline-block;
      width: 112px;
      height: @top-select-bar-height;
      text-align: center;
      line-height: normal;
      /*box-sizing: border-box;*/

      span {
        vertical-align: middle;
        font-size: 18px;
      }
    }

    .center-div {
      width: 70px;
      display: inline-block;
    }
  }

  .show-border-bottom {
    border-bottom: 1px solid #81d4cb;
  }

  .selected-color {
    color: #81d4cb;
  }
</style>
