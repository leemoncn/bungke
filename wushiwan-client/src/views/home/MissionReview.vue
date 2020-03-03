/**
* @Author: limeng
* @Date: 2019-05-19 20:50
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      ref="topBar"
      :title="isAcceptUser?'审核详情':'任务审核'"
      left-text="返回"
      fixed
      left-arrow
      :right-text="rightButtonText"
      @click-left="onNavBarLeftPressed"
      @click-right="onNavBarRightPressed"/>
    <div class="under-nav flex1 flex-column height0">
      <div class="overflow-auto flex1 " v-if="missionAcceptDetail" ref="mainView">
        <div class="top-section">
          <div class="status">
            <img :src="missionStatusImg" v-show="missionStatusImg" alt="">
          </div>
          <p>标&nbsp;&nbsp;&nbsp;题：{{missionAcceptDetail.title}}</p>
          <p>编&nbsp;&nbsp;&nbsp;号：{{missionAcceptDetail.missionId}}</p>
          <p>单&nbsp;&nbsp;&nbsp;价：{{missionAcceptDetail.price / 100}}元</p>
          <p v-show="missionAcceptDetail.publishUserId !== userId">发布方：{{missionAcceptDetail.publishUserName}}</p>
          <p v-show="missionAcceptDetail.textVerify">文字验证：{{missionAcceptDetail.textVerify}}</p>
          <p v-show="missionAcceptDetail.url" class="flex-row">
            <span style="flex-shrink: 0">链接：</span>
            <span
              style="cursor: pointer;text-decoration: underline;display: inline-block;word-break: break-all;"
              @click="onUrlPressed(missionAcceptDetail.url)">{{missionAcceptDetail.url}}</span>
          </p>

          <p>截止时间：{{missionAcceptDetail.deadlineTime}}</p>
          <p v-show="missionAcceptDetail.remark">备注：{{missionAcceptDetail.remark}}</p>
        </div>
        <div class="align-center">
          <div>
            <span>验证图</span>
            <span>(共{{missionAcceptDetail.uploadImgList.length}}张)</span>
          </div>
          <div v-for="(item,index) in missionAcceptDetail.uploadImgList" :key="'sample' + index">
            <img class="screen-img"
                 v-lazy="{src:imgFullPath(item),error:require('@/assets/img/accept-default.png'),loading:require('@/assets/img/accept-default.png')}"
                 alt=""/>
            <div>
              <span>▲ 图{{index + 1}} ▲</span>
            </div>
          </div>
          <div>
            <span>文字验证：</span>
            <span>{{missionAcceptDetail.textVerifyFromUser}}</span>
          </div>
        </div>
        <van-divider :style="{ borderColor: 'black',color: '#81d4cb'}">聊天内容</van-divider>
        <chat-list :me-user-id="userId" :chat-list="missionAcceptDetail.chatList"/>
      </div>

      <div v-if="missionAcceptDetail" class="submit" ref="bottomBar"
           v-show="canReply() || canComplaint() || canReview()">
        <button class="review-not-pass" @click="onNotPassMissionButtonPressed" v-show="canReview()">不合格</button>
        <button class="review-pass" @click="onPassMissionButtonPressed" v-show="canReview()">合&nbsp;&nbsp;格</button>
        <button class="complaint" v-show="canComplaint()" @click="complaintMission">申&nbsp;&nbsp;诉</button>
        <!--只要是被平台审核的，不管申诉处理中还是终审合格不合格，都不能聊天了-->
        <button class="reply" v-show="canReply()" @click="onReplyButtonPressed">回&nbsp;&nbsp;复</button>
      </div>
    </div>

    <upload-img-dialog :max-img-length="3" text-place-holder="请输入不合格原因" ref="notPassDialog"
                       :closable="notPassDialogClosable" @onDialogConfirm="onNotPassDialogConfirm"
                       title="审核不通过"
                       info="如接单用户所提交的验证图无后台数据或其他可以证明其没有完成任务的，可上传相关截图说明"/>

    <upload-img-dialog :max-img-length="3" text-place-holder="请输入回复内容" ref="replyDialog"
                       title="回复内容"
                       :closable="replyDialogClosable" @onDialogConfirm="onReplyDialogConfirm"/>

  </div>

</template>

<script>
  import { CHAT_REPLAY_URL, REVIEW_MISSION_DETAIL_URL, REVIEW_MISSION_URL } from "@/config/host"
  import { getPropertyById } from "@/service/property"
  import { mapGetters } from "vuex"
  import { Dialog } from "vant"
  import ChatList from "@/components/ChatList"
  import UploadImgDialog from "@/components/UploadImgDialog"
  import { uploadToQiniu } from "@/service/upload"
  import missionStatus1 from "@/assets/img/mission-status1.png"
  import missionStatus2 from "@/assets/img/mission-status2.png"
  import missionStatus3 from "@/assets/img/mission-status3.png"
  import missionStatus4 from "@/assets/img/mission-status4.png"
  import missionStatus5 from "@/assets/img/mission-status5.png"

  export default {
    name: "MissionReview",
    components: { UploadImgDialog, ChatList },
    data () {
      return {
        acceptId: null,
        isAcceptUser: false, // 当前用户是否是接受任务者
        missionId: null, // acceptid和missionid只有一个有值
        missionAcceptDetail: null,
        containerHeight: {
          height: "100%"
        },
        showComplaintDialog: false,
        notPassDialogClosable: true,
        replyDialogClosable: true
      }
    },
    props: {},
    computed: {
      ...mapGetters([
        "userId"
      ]),
      missionStatusImg () {
        if (this.missionAcceptDetail.proceedPropertyId === 23) {
          if (this.missionAcceptDetail.canComplaint) { // 没有申诉过
            return missionStatus5
          } else { // 申诉过
            switch (this.missionAcceptDetail.complaintResult) {
              case null:
                return missionStatus1
              case false:
                return missionStatus4
            }
          }
        }
        if (this.missionAcceptDetail.proceedPropertyId === 24) {
          if (this.missionAcceptDetail.complaintResult) {
            return missionStatus3
          }
          return missionStatus2
        }
        return null
      },
      rightButtonText () {
        if (!this.isAcceptUser) {
          return null
        }
        if (!this.missionAcceptDetail) {
          return null
        }
        if (this.missionAcceptDetail.proceedPropertyId === 23 && this.missionAcceptDetail.canComplaint && !this.missionAcceptDetail.finishTime) { // 不合格，可申诉,不超过12小时（finishTime是null）
          return "重新提交"
        }
        return null
      }
    },
    mounted () {
    },
    activated () {
      this.acceptId = this.$route.query.id
      this.missionId = this.$route.query.missionId
      this.isAcceptUser = this.$route.query.isAcceptUser === "1"
      this.loadData()
      this.containerHeight.height = window.innerHeight - this.$refs.topBar.offsetHeight - (this.$refs.bottomBar ? this.$refs.bottomBar.offsetHeight : 0) + "px"
    },
    methods: {
      async loadData () {
        const result = await this.httpPostWithLoading(REVIEW_MISSION_DETAIL_URL, {
          id: this.acceptId,
          missionId: this.missionId
        })
        if (result.success) {
          this.missionAcceptDetail = result.data
          if (this.$refs.mainView) {
            this.$refs.mainView.scrollTop = 0
          }
        } else {
          this.$router.back()
        }
      },
      onNavBarLeftPressed () {
        this.$router.back()
      },
      onNavBarRightPressed () {
        this.$router.push({ name: "FinishMission", query: { id: this.missionAcceptDetail.missionId } })
      },
      async onPassMissionButtonPressed () {
        Dialog.confirm({
          message: "确定合格？"
        }).then(async () => {
          const result = await this.httpPostWithLoading(REVIEW_MISSION_URL, {
            missionAcceptId: this.missionAcceptDetail.id,
            result: true
          })
          if (result.success) {
            if (this.missionId) {
              this.loadData()
            } else {
              this.toastSuccess("操作完成")
              this.$router.back()
            }
          }
        })
      },
      onNotPassMissionButtonPressed () {
        this.$refs.notPassDialog.showDialog()
      },
      getMissionTypeName () {
        const p = getPropertyById(this.missionAcceptDetail.typePropertyId)
        return p.name
      },
      complaintMission () {
        Dialog.confirm({
          title: "提示",
          message: "申诉内容商家看不到，不符合申诉条件的将不会受理，您确认是要申诉吗？"
        }).then(() => {
          this.$router.push({ name: "Complaint", query: { id: this.acceptId } })
        })
      },
      onReplyButtonPressed () {
        this.$refs.replyDialog.showDialog()
      },
      async onNotPassDialogConfirm (text, imgList) {
        if (!text) {
          this.toastFail("请填写原因")
          return
        }
        this.showLoading()
        await uploadToQiniu(imgList, (key) => {
          this.requestNotPass(key, text)
        }, () => {
          this.toastFail("图片上传失败，请重试")
        })
      },
      async requestNotPass (key, text) {
        const result = await this.httpPostWithLoading(REVIEW_MISSION_URL, {
          missionAcceptId: this.missionAcceptDetail.id,
          result: false,
          reason: text,
          key: key
        })
        if (result.success) {
          this.notPassDialogClosable = true
          if (this.missionId) {
            this.loadData()
          } else {
            this.toastSuccess("操作完成")
            this.$router.back()
          }
        } else {
          this.notPassDialogClosable = false
        }
      },
      async onReplyDialogConfirm (text, imgList) {
        this.showLoading()
        await uploadToQiniu(imgList, (key) => {
          this.requestReply(key, text)
        }, () => {
          this.toastFail("图片上传失败，请重试")
        })
      },
      async requestReply (key, text) {
        const result = await this.httpPostWithLoading(CHAT_REPLAY_URL, {
          text: text,
          key: key,
          missionId: this.missionAcceptDetail.missionId,
          missionAcceptId: this.missionAcceptDetail.id,
          toUserId: this.userId === this.missionAcceptDetail.acceptUserId ? this.missionAcceptDetail.publishUserId : this.missionAcceptDetail.acceptUserId
        })
        if (result.success) {
          this.hideLoading()
          this.replyDialogClosable = true
          this.loadData()
        } else {
          this.replyDialogClosable = false
        }
      },
      onUrlPressed (url) {
        if (this.isApp()) { // app环境
          this.openUrl(url)
        } else { // 浏览器环境,新标签页打开
          window.open(url, "_blank")
        }
      },
      canReview () { // 是否显示合格不合格的按钮
        return this.missionAcceptDetail.proceedPropertyId === 22 && this.missionAcceptDetail.publishUserId === this.userId
      },
      canReply () { // 是否显示回复的按钮
        return this.missionAcceptDetail.canComplaint
      },
      canComplaint () { // 是否显示申诉的按钮
        return this.missionAcceptDetail.proceedPropertyId === 23 && this.missionAcceptDetail.canComplaint && this.isAcceptUser
      }
    }
  }
</script>

<style scoped lang="less">
  .head-img {
    width: 36px;
    height: 36px;
    border-radius: 18px;
  }

  .info-container {
    background-color: white;
  }

  .container {
    height: 300px;
  }

  .screen-img {
    max-width: 300px;
  }

  .flex-column-center {
    flex-direction: column;
    display: flex;
    justify-content: center;
  }

  .align-center {
    text-align: center;
  }

  .text-section {
    background-color: white;
    padding: 10px 0;
    margin-bottom: 20px;
  }

  .submit {
    display: flex;
    justify-content: space-around;
    background-color: white;
    padding: 6px 0;

    button {
      width: 94px;
      height: 36px;
      line-height: 36px;
      border-radius: 10px;
      font-size: 16px;
      color: #ffffff;
    }

    .review-pass {
      background-color: #81d4cb;
    }

    .review-not-pass {
      background-color: #dd4944;
    }

    .complaint {
      background-color: #81d4cb;
    }

    .reply {
      background-color: #b1e46c;
    }
  }

  .top-section {
    background-color: white;
    padding: 10px 14px 10px;
    font-size: 14px;
    position: relative;

    .status {
      position: absolute;
      right: 0px;
      top: -8px;

      img {
        width: 140px;
        height: 140px;
      }
    }

    p {
      &:nth-child(n+3) {
        margin-top: 8px;
      }

      font-size: 14px;
      color: #424242;

    }

    span {
      &:nth-child(n+3) {
        margin-top: 8px;
      }

      font-size: 14px;
      color: #424242;

    }
  }
</style>
