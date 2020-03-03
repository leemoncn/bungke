/**
* @Author: leemon
* @Date: 2019-08-13 16:15
* @Description:
*/
<template xmlns:v-popover="http://www.w3.org/1999/xhtml">
  <div v-if="data">
    <el-popover
      ref="popover"
      placement="right"
      @show="onShow"
      trigger="click">
      <div class="flex-row flex-center" v-if="!userData">
        <span>加载中...</span>
      </div>
      <div v-else class="info">
        <p>{{`id: ${userData.id}`}}</p>
        <p>{{`真实姓名: ${userData.realName?userData.realName:""}`}}</p>
        <p>{{`支付宝账号: ${userData.alipay?userData.alipay:""}`}}</p>
        <p>{{`昵称: ${userData.nickname}`}}</p>
        <p>{{`电话: ${userData.phone?userData.phone:""}`}}</p>
        <p>{{`头像: ${userData.headImgUrl?userData.headImgUrl:""}`}}</p>
        <p>{{`用户类型: ${userData.userType === 1?"前台用户":"后台用户"}`}}</p>
        <p>{{`登陆状态: ${userData.loginStatusPropertyId === 3?"允许登陆":"禁止登陆"}`}}</p>
        <div class="flex-row flex-align-center" style="line-height: normal">
          <span>上级ID:&nbsp;&nbsp;</span>
          <user-popover :data="userData.superiorId" v-if="userData.superiorId"/>
        </div>
        <p>{{`充值平台返现: ${userData.rechargeByApp}%`}}</p>
        <p>{{`提现用户返现: ${userData.withdrawByUser}%`}}</p>
        <p>{{`合作商: ${userData.partnerName}`}}</p>
        <p>{{`保证金: ${userData.deposit / 100.0}元`}}</p>
        <p>{{`任务币: ${userData.missionCoin / 100.0}元`}}</p>
        <p>{{`收入: ${userData.earning / 100.0}元`}}</p>
        <p>{{`提现审核中: ${userData.moneyInReview / 100.0}元`}}</p>
        <p>{{`最后登陆IP: ${userData.loginIp}`}}</p>
        <p>{{`最后登陆时间: ${userData.loginTime}`}}</p>
      </div>
    </el-popover>
    <el-tag v-popover:popover>{{data}}</el-tag>
  </div>
</template>

<script>
  import { USER_INFO_URL } from "@/config/host"

  export default {
    name: "UserPopover",
    data () {
      return {
        userData: null
      }
    },
    props: {
      data: {
        type: Number
        // required: true
      }
    },
    methods: {
      async loadUserInfo () {
        this.httpPost(USER_INFO_URL, { id: this.data })
          .then((result) => {
            if (result.success) {
              this.userData = result.data
            } else {
              this.userData = {}
            }
          })
      },
      onShow () {
        if (this.data) {
          this.loadUserInfo()
        }
      }
    }
  }
</script>

<style scoped lang="less">
  .info {
    width: 300px;
    /*height: 200px;*/
    /*overflow: auto;*/

    p {
      color: black;
      font-size: 14px;
      margin: 4px 0;
    }

    span {
      color: black;
      font-size: 14px;
    }
  }
</style>
