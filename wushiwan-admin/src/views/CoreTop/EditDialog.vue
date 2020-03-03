/**
* @Author: leemon
* @Date: 2019-7-25 9:09:34
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
        <el-form-item label="任务ID" prop="missionId">
          <el-input v-model="dialogModal.missionId"/>
        </el-form-item>
        <el-form-item label="免费小时数" prop="freeHours">
          <el-input v-model="dialogModal.freeHours"/>
        </el-form-item>
        <el-form-item label="收费的小时数" prop="paidHours">
          <el-input v-model="dialogModal.paidHours"/>
        </el-form-item>
        <el-form-item label="收费小时数花费的钱" prop="price">
          <el-input v-model="dialogModal.price"/>
        </el-form-item>
        <el-form-item label="置顶结束时间" prop="topEndTime">
          <el-input v-model="dialogModal.topEndTime"/>
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
  import { CORETOP_ADD_URL, CORETOP_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    userId: null,
    missionId: null,
    freeHours: null,
    paidHours: null,
    price: null,
    topEndTime: null
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
          missionId: [{ required: true, message: "请输入任务ID", trigger: "blur" }],
          freeHours: [{ required: true, message: "请输入免费小时数", trigger: "blur" }],
          paidHours: [{ required: true, message: "请输入收费的小时数", trigger: "blur" }],
          price: [{ required: true, message: "请输入收费小时数花费的钱", trigger: "blur" }],
          topEndTime: [{ required: true, message: "请输入置顶结束时间", trigger: "blur" }]
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
            const url = this.dialogModal.id ? CORETOP_EDIT_URL : CORETOP_ADD_URL
            this.httpPostWithLoading(url, this.dialogModal, ".el-dialog").then(({ data, success }) => {
              if (success) {
                this.$message({
                  message: "菜单编辑成功",
                  type: "success"
                })
                this.closeEditDialog()
                this.reloadParentData()
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
