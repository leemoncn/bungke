/**
* @Author: limeng
* @Date: 2019-06-25 23:48
* @Description:
*/
<template>
  <div :style="{backgroundImage: 'url('+bgPath+')'}" class="menu" @click="onMenuClick">
    <img :src="iconPath" class="icon" alt="">
    <p class="title">{{title}}</p>
    <p class="description">{{des}}</p>
  </div>
</template>

<script>
  export default {
    name: "HomeMenu",
    props: {
      bg: {
        type: String,
        required: true
      },
      icon: {
        type: String,
        required: true
      },
      title: {
        type: String,
        required: true
      },
      des: {
        type: String,
        required: true
      }
    },
    computed: {
      bgPath () {
        return require("@/assets/img/menu-bg" + this.bg + ".png")
      },
      iconPath () {
        return require("@/assets/img/menu" + this.icon + ".png")
      }
    },
    data () {
      return {}
    },
    methods: {
      onMenuClick () {
        this.$emit("menuClick")
      }
    }
  }
</script>

<style scoped lang="less">

  .menu {
    width: 170px;
    height: 170px;
    background-repeat: no-repeat;
    background-size: contain;
    text-align: center;
    position: relative;
  }

  .title {
    font-size: 19px;
    margin-top: 98px;
    color: #424242;
  }

  .description {
    font-size: 10px;
    color: #9f9d9d;
    width: 120px;
    display: block;
    margin-left: auto;
    margin-right: auto;
    margin-top: -2px;
  }

  .icon {
    position: absolute;
    width: 46px;
    height: 46px;
    top: 46px;
    left: 50%;
    margin-left: -23px;
  }
</style>
