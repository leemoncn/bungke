/**
* @Author: leemon
* @Date: 2019-7-1 13:35:02
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
        <el-form-item label="用户id" prop="userId">
          <el-input v-model="dialogModal.userId"/>
        </el-form-item>
        <el-form-item label="保证金金额,单位分" prop="deposit">
          <el-input v-model="dialogModal.deposit"/>
        </el-form-item>
        <el-form-item label="申退保证金日期" prop="refundTime">
          <el-input v-model="dialogModal.refundTime"/>
        </el-form-item>
        <el-form-item label="申退保证金完成" prop="refundFinishTime">
          <el-input v-model="dialogModal.refundFinishTime"/>
        </el-form-item>
        <el-form-item label="当前合作商是否可用" prop="usable">
          <el-input v-model="dialogModal.usable"/>
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
  import { COREDEPOSITPURCHASE_ADD_URL, COREDEPOSITPURCHASE_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    userId: null,
    deposit: null,
    refundTime: null,
    refundFinishTime: null,
    usable: null
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
		      userId: [{ required: true, message: "请输入用户id", trigger: "blur" }],
		      deposit: [{ required: true, message: "请输入保证金金额,单位分", trigger: "blur" }],
		      refundTime: [{ required: true, message: "请输入申退保证金日期", trigger: "blur" }],
		      refundFinishTime: [{ required: true, message: "请输入申退保证金完成", trigger: "blur" }],
		      usable: [{ required: true, message: "请输入当前合作商是否可用", trigger: "blur" }]
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
          const url = this.dialogModal.id ? COREDEPOSITPURCHASE_EDIT_URL : COREDEPOSITPURCHASE_ADD_URL
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
