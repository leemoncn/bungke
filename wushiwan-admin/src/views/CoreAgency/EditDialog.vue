/**
* @Author: leemon
* @Date: 2019-6-29 23:01:52
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="200px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="充值任务币时平台给上级的返现提成(%)" prop="rechargeByApp">
          <el-input v-model="dialogModal.rechargeByApp"/>
        </el-form-item>
        <el-form-item label="提现时用户从自己的钱拿出来给上级(%)" prop="withdrawByUser">
          <el-input v-model="dialogModal.withdrawByUser"/>
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
  import { COREAGENCY_ADD_URL, COREAGENCY_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    rechargeByApp: null,
    withdrawByUser: null
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
          rechargeByApp: [{ required: true, message: "请输入充值时平台给上级的返现提成，百分比，乘以100", trigger: "blur" }],
          withdrawByUser: [{ required: true, message: "请输入提现时用户从自己的钱拿出来给上级返现的比例", trigger: "blur" }]
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
            const url = this.dialogModal.id ? COREAGENCY_EDIT_URL : COREAGENCY_ADD_URL
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
