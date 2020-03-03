/**
* @Author: limeng
* @Date: 2019-04-05 09:44
* @Description:
*/
<template>
  <div>
    <home class="above-tab" :class="activeItemIndex !== 0?'tab-hidden':''"/>
    <message class="above-tab" :class="activeItemIndex !== 1?'tab-hidden':''"/>
    <personal class="above-tab" :class="activeItemIndex !== 2?'tab-hidden':''"/>
    <van-tabbar v-model="activeItemIndex" @change="onTabItemChange" active-color="#81d4cb">
      <van-tabbar-item icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item icon="chat-o"
                       :info="$store.state.setting.newMsgCount === 0?null:$store.state.setting.newMsgCount">消息
      </van-tabbar-item>
      <van-tabbar-item icon="manager-o">个人</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
  import { mapGetters } from "vuex"
  import Home from "@/views/home/index"
  import Message from "@/views/message/index"
  import Personal from "@/views/personal/index"

  export default {
    name: "TabPage",
    components: { Personal, Message, Home },
    computed: {
      ...mapGetters([
        "tabbarActiveItemIndex"
      ]),
      activeItemIndex: {
        get: function () {
          // return 0
          return this.tabbarActiveItemIndex
        },
        set: function (value) {
          this.$store.commit("changeActiveItemIndex", value)
        }
      }
    },
    methods: {
      onTabItemChange (activeIndex) {

      }
    }
  }
</script>

<style scoped lang="less">
  .tab-hidden {
    display: none;
  }
</style>
