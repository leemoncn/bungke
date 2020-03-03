/**
* @Author: limeng
* @Date: 2019-03-31 11:15
* @Description:
*/
<template>
  <div>
    <van-nav-bar
      title="麻麻批"
      left-text="返回"
      right-text="按钮"
      fixed
      left-arrow
      @click-left="onClickLeft"
    />
  </div>
</template>

<script>

  export default {
    name: "Test",
    data: function () {
      return {
        username: "",
        password: ""
      }
    },
    methods: {
      onClickLeft () {
        this.$router.back()
      }
    }
  }
</script>

<style scoped lang="less">
  .loginButton {
    margin: 5px 20px;
  }

  .loadingBG {
    padding: 20px;
    border-radius: 3px;
    background-color: rgba(0, 0, 0, 0.5);
    margin-left: 50px;
    width: 30px;
    height: 30px;
  }
</style>
