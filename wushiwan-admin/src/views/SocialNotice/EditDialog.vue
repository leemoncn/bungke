/**
* @Author: leemon
* @Date: 2019-6-26 16:48:29
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
        <el-form-item label="userId" prop="userId">
          <el-input v-model="dialogModal.userId"/>
        </el-form-item>
        <el-form-item label="发生时间" prop="time">
          <el-input v-model="dialogModal.time"/>
        </el-form-item>
        <el-form-item label="通知类型" prop="typePropertyId">
          <el-input v-model="dialogModal.typePropertyId"/>
        </el-form-item>
        <el-form-item label="msg1" prop="msg1">
          <el-input v-model="dialogModal.msg1"/>
        </el-form-item>
        <el-form-item label="msg2" prop="msg2">
          <el-input v-model="dialogModal.msg2"/>
        </el-form-item>
        <el-form-item label="msg3" prop="msg3">
          <el-input v-model="dialogModal.msg3"/>
        </el-form-item>
        <el-form-item label="msg4" prop="msg4">
          <el-input v-model="dialogModal.msg4"/>
        </el-form-item>
        <el-form-item label="msg5" prop="msg5">
          <el-input v-model="dialogModal.msg5"/>
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
  import { SOCIALNOTICE_ADD_URL, SOCIALNOTICE_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    userId: null,
    time: null,
    typePropertyId: null,
    msg1: null,
    msg2: null,
    msg3: null,
    msg4: null,
    msg5: null
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
		      userId: [{ required: true, message: "请输入userId", trigger: "blur" }],
		      time: [{ required: true, message: "请输入发生时间", trigger: "blur" }],
		      typePropertyId: [{ required: true, message: "请输入通知类型", trigger: "blur" }],
		      msg1: [{ required: true, message: "请输入msg1", trigger: "blur" }],
		      msg2: [{ required: true, message: "请输入msg2", trigger: "blur" }],
		      msg3: [{ required: true, message: "请输入msg3", trigger: "blur" }],
		      msg4: [{ required: true, message: "请输入msg4", trigger: "blur" }],
		      msg5: [{ required: true, message: "请输入msg5", trigger: "blur" }]
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
          const url = this.dialogModal.id ? SOCIALNOTICE_EDIT_URL : SOCIALNOTICE_ADD_URL
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
