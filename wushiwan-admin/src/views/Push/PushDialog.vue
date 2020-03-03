/**
* @Author: leemon
* @Date: 2019-11-18 20:04:03
*/
<template>
  <div>
    <el-dialog ref="dialogView" :title="getDialogTitle()" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose" :close-on-click-modal="false">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="80px">

        <el-form-item label="标题" prop="title">
          <el-input v-model="dialogModal.title" maxlength="20" type="textarea" autosize/>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="dialogModal.content" placeholder="必填" maxlength="40" type="textarea" autosize/>
        </el-form-item>
        <el-form-item label="推送类型" prop="payloadType">
          <el-select v-model="dialogModal.payloadType" placeholder="请选择" value="" disabled>
            <el-option label="Toast" :value="1"/>
            <el-option label="推送+消息列表" :value="2"/>
          </el-select>
        </el-form-item>
        <el-form-item label="指定用户" prop="pushId">
          <el-input v-model="dialogModal.pushId" placeholder="需要用户的pushId,不填则向全体用户发送" disabled/>
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
  import { PUSH_CREATE_URL } from "@/config/host"

  const defaultDialogModal = {
    title: null,
    content: null,
    payloadType: 2,
    userId: null
  }

  export default {
    name: "PushDialog",
    props: {
      editDialogVisible: {
        type: Boolean,
        required: true
      },
      userId: {
        type: Number
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
          content: [{ required: true, message: "请输入推送内容", trigger: "blur" }],
          title: [{ required: true, message: "请输入标题", trigger: "blur" }],
          payloadType: [{ required: true, message: "请选择推送类型", trigger: "blur" }]
        }
      }
    },
    methods: {
      getDialogTitle () {
        return "创建全局推送"
      },
      onConfirmButtonPressed () {
        this.$refs.dialogForm.validate((valid) => {
          if (valid) {
            this.httpPostWithLoading(PUSH_CREATE_URL, this.dialogModal, ".el-dialog").then(({ data, success }) => {
              if (success) {
                if (!data) {
                  this.$message({
                    message: "创建推送成功",
                    type: "success"
                  })
                  this.closeEditDialog()
                } else if (data === "RepeatedContent") {
                  this.$message({
                    message: "不可发送重复内容",
                    type: "error"
                  })
                } else {
                  this.$message({
                    message: "发送失败，错误码 = " + data,
                    type: "error"
                  })
                }
              }
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
        this.dialogModal = _.cloneDeep(defaultDialogModal)
        this.dialogModal.userId = this.userId
      },
      dialogClose () {
        this.$refs.dialogView.$el.querySelector(".el-dialog__body").scrollTop = 0
      },
      closeEditDialog () {
        this.$emit("update:editDialogVisible", false)
      }
    }
  }
</script>

<style scoped lang="css">
  .avatar-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 356px;
  }

  .avatar-uploader:hover {
    border-color: #409EFF;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 356px;
    height: 154px;
    line-height: 154px;
    text-align: center;
  }

  .avatar {
    width: 356px;
    height: 154px;
    display: block;
    object-fit: fill;
  }
</style>
