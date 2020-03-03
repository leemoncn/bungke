/**
* @Author: limeng
* @Date: 2019-08-26 22:26
* @Description: 支付宝账号绑定页面
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="支付宝绑定"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed">
    </van-nav-bar>
    <div class="under-nav flex1 height0 recharge">
      <van-cell-group>
        <van-field label="支付宝账号" v-model="account" placeholder="请输入支付宝账号" :readonly="readonly"/>
        <van-field label="真实姓名" v-model="realName" placeholder="请输入真实姓名" :readonly="readonly"/>
      </van-cell-group>
      <button @click="onSetPressed" v-show="!readonly">绑&nbsp;&nbsp;&nbsp;定</button>
      <p>提示：
        1、因支付宝为实名账户，请输入对应账户的真实姓名，否则会造成支付或提现失败，支付宝账户只能修改一次，请勿随意改动
        2、如后期需修改绑定的支付宝账号或姓名，请联系客服QQ {{kefu_qq}} 或客服微信 {{kefu_wechat}}
      </p>
    </div>
  </div>
</template>

<script>
  import { mapGetters } from "vuex"
  import { SET_ALIPAY_ACCOUNT_URL } from "@/config/host"
  import { Dialog } from "vant"
  import { KEFU_QQ, KEFU_WECHAT } from "@/config/define"

  export default {
    name: "AlipayAccount",
    data () {
      return {
        account: null,
        realName: null,
        readonly: false,
        kefu_qq: KEFU_QQ,
        kefu_wechat: KEFU_WECHAT
      }
    },
    watch: {},
    mounted () {
      if (this.user.alipay || this.user.realName) {
        this.readonly = true
        this.account = this.user.alipay
        this.realName = this.user.realName
      }
    },
    computed: {
      ...mapGetters([
        "user"
      ])
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      onSetPressed () {
        // TODO 支付宝账号应为邮箱或者手机，需要验证下
        if (!this.account) {
          this.toastMsg("请输入支付宝账号")
          return
        }
        if (!this.realName) {
          this.toastMsg("请输入真实姓名")
          return
        }
        Dialog.confirm({
          title: "绑定信息",
          messageAlign: "left",
          message: `支付宝账号：${this.account}\n真实姓名：${this.realName}\n\n账号只能绑定一次，后续想要修改请联系客服，确认信息吗？`
        }).then(async () => {
          const result = await this.httpPostWithLoading(SET_ALIPAY_ACCOUNT_URL, {
            account: this.account,
            realName: this.realName
          })
          if (result.success) {
            this.toastMsg("绑定成功")
            this.$store.commit("updateUser", result.data)
            this.readonly = true
          }
        })
      }
    }
  }
</script>

<style scoped lang="less">
  .recharge {
    button {
      width: 300px;
      height: 36px;
      line-height: normal;
      border: solid 1px #81d4cb;
      border-radius: 4px;
      color: white;
      font-size: 16px;
      background-color: #81d4cb;
      text-align: center;
      margin: 20px auto;
      display: block;
    }

    .risk {
      margin-top: 20px;
      color: #ff4444;
    }

    p {
      white-space: pre-wrap;
      margin-left: 10px;
      margin-right: 10px;
      color: #969799;
    }
  }
</style>
