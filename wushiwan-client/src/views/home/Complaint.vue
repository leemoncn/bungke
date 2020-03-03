/**
* @Author: limeng
* @Date: 2019-06-16 16:16
* @Description:
*/
<template>
  <div class="column-view">
    <div ref="topBar">
      <van-nav-bar
        title="申诉"
        left-text="返回"
        fixed
        left-arrow
        @click-left="onNavBarLeftPressed">
      </van-nav-bar>
    </div>
    <div class="under-nav fill">
      <van-field
        class="text"
        v-model="text"
        type="textarea"
        placeholder="请输入申诉内容，最多500字"
        size="large"
        rows="10"
        border
        autosize/>

      <van-button class="button" type="primary" round size="large" @click="commitComplaint">提交</van-button>
      <div class="bottomInfo">
        <span>申诉原则：</span>
        <span>1、沟通内容表述不清，使用不文明语言的将不予处理。</span>
        <span>2、平台将秉承公正立场予以裁定，但无法确保每次裁定均为客观正确，无论审核是否通过，您都需要接受终审裁定。</span>
        <span>3、频繁申诉者，被驳回的概率会增加。</span>
        <span>4、如果终审被驳回，此单将被释放，您还可以重新抢单。</span>
      </div>
    </div>

  </div>
</template>

<script>
  import { COMPLAINT_URL } from "@/config/host"
  import { Dialog } from "vant"

  export default {
    name: "Complaint",
    data () {
      return {
        text: "",
        acceptId: null
      }
    },
    mounted () {
      this.acceptId = this.$route.query.id
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      commitComplaint () {
        if (this.text.length > 500) {
          this.toastMsg("申诉内容最多500个字")
          return
        }
        if (this.text.length === 0) {
          this.toastMsg("请输入申诉内容")
          return
        }
        Dialog.confirm({
          message: "确认申诉吗？"
        }).then(() => {
          this.complaintRequest()
        })
      },
      async complaintRequest () {
        const result = await this.httpPostWithLoading(COMPLAINT_URL, { acceptId: this.acceptId, text: this.text })
        if (result.success) {
          Dialog.alert({
            message: "成功提交，请耐心等待申诉结果"
          }).then(() => {
            this.onNavBarLeftPressed()
          })
        }
      }
    }
  }
</script>

<style scoped lang="less">
  .text {
    margin-top: 10px;
    height: 200px;
  }

  .button {
    margin-top: 20px;
    line-height: normal;
  }

  .bottomInfo {
    margin-top: 20px;

    span {
      color: #b0aeb1;
      display: inline-block;
      padding: 0 10px;
    }
  }
</style>
