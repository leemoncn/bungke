/**
* @Author: leemon
* @Date: 2019-7-15 10:49:16
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="80px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="关注者" prop="fromUserId">
          <el-input v-model="dialogModal.fromUserId"/>
        </el-form-item>
        <el-form-item label="被关注者" prop="toUserId">
          <el-input v-model="dialogModal.toUserId"/>
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
  import { SOCIALFOLLOW_ADD_URL, SOCIALFOLLOW_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    fromUserId: null,
    toUserId: null
  }

  export default {
    name: "EditDialog",
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
		      fromUserId: [{ required: true, message: "请输入关注者", trigger: "blur" }],
		      toUserId: [{ required: true, message: "请输入被关注者", trigger: "blur" }]
        }
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
          const url = this.dialogModal.id ? SOCIALFOLLOW_EDIT_URL : SOCIALFOLLOW_ADD_URL
            this.httpPostWithLoading(url, this.dialogModal, ".el-dialog").then(({ data, success }) => {
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
      },
      dialogClose () {
        this.$refs.dialogView.$el.querySelector(".el-dialog__body").scrollTop = 0
      },
      closeEditDialog () {
        this.$emit("update:editDialogVisible", false)
      },
      reloadParentData () {
        this.$emit("reload")
      }
    }
  }
</script>

<style scoped lang="css">

</style>
