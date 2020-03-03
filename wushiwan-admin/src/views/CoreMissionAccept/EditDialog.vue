/**
* @Author: leemon
* @Date: 2019-5-19 22:30:51
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="120px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="接取任务ID" prop="missionId">
          <el-input v-model="dialogModal.missionId"/>
        </el-form-item>
        <el-form-item label="接单者ID" prop="userId">
          <el-input v-model="dialogModal.userId"/>
        </el-form-item>
        <el-form-item label="接受任务时间" prop="acceptTime">
          <el-input v-model="dialogModal.acceptTime"/>
        </el-form-item>
        <el-form-item label="完成任务时间" prop="finishTime">
          <el-input v-model="dialogModal.finishTime"/>
        </el-form-item>
        <el-form-item label="任务执行情况" prop="proceedPropertyId">
          <el-select v-model="dialogModal.proceedPropertyId" placeholder="请选择">
            <el-option
              v-for="(item,index) in missionProceedPropertyList"
              :key="index"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文字验证" prop="textVerify">
          <el-input v-model="dialogModal.textVerify"/>
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
  import { COREMISSIONACCEPT_ADD_URL, COREMISSIONACCEPT_EDIT_URL } from "@/config/host"
  import { getPropertyListByType, PropertyType } from "@/service/property"

  const defaultDialogModal = {
    id: null,
    missionId: null,
    userId: null,
    acceptTime: null,
    finishTime: null,
    proceedPropertyId: null,
    textVerify: null
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
      },
      missionProceedPropertyList: function () {
        return getPropertyListByType(PropertyType.MISSION_PROCEED)
      }
    },
    data () {
      return {
        dialogModal: _.cloneDeep(defaultDialogModal),
        dialogRules: {
          missionId: [{ required: true, message: "请输入接取的任务ID", trigger: "blur" }],
          userId: [{ required: true, message: "请输入接单者ID", trigger: "blur" }],
          acceptTime: [{ required: true, message: "请输入接受任务时间", trigger: "blur" }],
          proceedPropertyId: [{ required: true, message: "请输入任务执行情况", trigger: "blur" }]
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
            const url = this.dialogModal.id ? COREMISSIONACCEPT_EDIT_URL : COREMISSIONACCEPT_ADD_URL
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
