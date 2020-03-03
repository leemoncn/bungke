/**
* @Author: leemon
* @Date: 2019-6-14 21:58:50
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow"
               width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="100px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <!--<el-form-item label="接受任务ID" prop="missionAcceptId">-->
        <!--<el-input v-model="dialogModal.missionAcceptId"/>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="任务申诉者的id" prop="complainterUserId">-->
        <!--<el-input v-model="dialogModal.complainterUserId"/>-->
        <!--</el-form-item>-->
        <el-form-item label="申诉内容" prop="text">
          <!--<el-input v-model="dialogModal.text"/>-->
          <span>{{dialogModal.text}}</span>
        </el-form-item>
        <template v-show="chatList && chatList.length > 0">
          <el-divider>▼ 聊天内容 ▼</el-divider>
          <chat-list :chat-list="chatList" :accept-user-id="dialogModal.complainterUserId"/>
          <el-divider>▲ 聊天内容 ▲</el-divider>
        </template>
        <el-form-item label="申诉结果" prop="result">
          <el-select v-model="dialogModal.result" value="">
            <el-option label="终审合格" :value="true"/>
            <el-option label="终审不合格" :value="false"/>
          </el-select>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="onCancelButtonPressed()">取消</el-button>
        <el-button type="primary" @click="onConfirmButtonPressed()">确定</el-button>
      </div>

    </el-dialog>

  </div>
</template>

<script>
  import { COREMISSION_REVIEW_DETAIL_URL, COREMISSIONCOMPLAINT_HANDLE_COMPLAINT_URL } from "@/config/host"
  import ChatList from "@/components/ChatList"

  const defaultDialogModal = {
    id: null,
    missionAcceptId: null,
    complainterUserId: null,
    text: null,
    result: null
  }

  export default {
    name: "EditDialog",
    components: { ChatList },
    props: {
      editDialogVisible: {
        type: Boolean,
        required: true
      },
      editDialogData: {
        type: Object
      },
      editDialogEditable: {
        type: Boolean
      }
    },
    computed: {
      isEditDialogShow: {
        get: function () {
          return this.editDialogVisible
        },
        set: function (value) {
          if (!value) {
            this.closeEditDialog()
          }
        }
      }
    },
    data () {
      return {
        dialogModal: _.cloneDeep(defaultDialogModal),
        dialogRules: {
          missionAcceptId: [{ required: true, message: "请输入missionAcceptId", trigger: "blur" }],
          complainterUserId: [{ required: true, message: "请输入任务申诉者的id", trigger: "blur" }],
          text: [{ required: true, message: "请输入申诉内容", trigger: "blur" }],
          result: [{ required: true, message: "请选择处理结果", trigger: "blur" }]
        },
        chatList: null
      }
    },
    methods: {
      getDialogTitle () {
        if (!this.editDialogEditable) {
          return "查看"
        }
        if (this.editDialogData) {
          return "编辑"
        }
        return "添加"
      },
      onConfirmButtonPressed () {
        if (!this.editDialogEditable) {
          this.onCancelButtonPressed()
          return
        }
        this.$refs.dialogForm.validate((valid) => {
          if (valid) {
            this.httpPostWithLoading(COREMISSIONCOMPLAINT_HANDLE_COMPLAINT_URL, {
              id: this.dialogModal.id,
              result: this.dialogModal.result
            }, ".el-dialog").then(({ data, success }) => {
              if (success) {
                this.$message({
                  message: "菜单编辑成功",
                  type: "success"
                })
                this.closeEditDialog()
                this.reloadParentData()
              }
            }).catch((err) => {
              console.log(err)
            })
          }
        })
      },
      onCancelButtonPressed () {
        this.closeEditDialog()
      },
      dialogOpened () {
        this.$refs.dialogForm.clearValidate()
      },
      dialogOpen () {
        this.dialogModal = this.editDialogData ? _.cloneDeep(this.editDialogData) : _.cloneDeep(defaultDialogModal)
        this.loadChatList()
      },
      dialogClose () {
        this.$refs.dialogView.$el.querySelector(".el-dialog__body").scrollTop = 0
      },
      closeEditDialog () {
        this.$emit("update:editDialogVisible", false)
      },
      reloadParentData () {
        this.$emit("reload")
      },
      async loadChatList () {
        const result = await this.httpPostWithLoading(COREMISSION_REVIEW_DETAIL_URL, {
          id: this.editDialogData.missionAcceptId
        }, "el-dialog")
        if (result.success) {
          this.chatList = result.data
        }
      }
    }
  }
</script>

<style scoped lang="css">

</style>
