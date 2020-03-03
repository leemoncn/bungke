/**
* @Author: leemon
* @Date: 2019-4-27 11:28:34
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
        <el-form-item label="图片类型，比如发布任务的审核图片，用户上传的待审核图片，意见反馈的图片" prop="typePropertyId">
          <el-input v-model="dialogModal.typePropertyId"/>
        </el-form-item>
        <el-form-item label="具体的ID,比如发布任务是core_mission_detail,提交任务是core_mission_accept的id,意见反馈是sys_advice的id"
                      prop="dataId">
          <el-input v-model="dialogModal.dataId"/>
        </el-form-item>
        <el-form-item label="任务发布者的ID，或者任务接取者的ID" prop="userId">
          <el-input v-model="dialogModal.userId"/>
        </el-form-item>
        <el-form-item label="图片的本地路径" prop="path">
          <el-input v-model="dialogModal.path"/>
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
  import { COREIMG_ADD_URL, COREIMG_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    typePropertyId: null,
    dataId: null,
    userId: null,
    path: null
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
          typePropertyId: [{ required: true, message: "请输入图片类型，比如发布任务的审核图片，用户上传的待审核图片，意见反馈的图片", trigger: "blur" }],
          dataId: [{
            required: true,
            message: "请输入具体的ID,比如发布任务是core_mission_detail,提交任务是core_mission_accept的id,意见反馈是sys_advice的id",
            trigger: "blur"
          }],
          userId: [{ required: true, message: "请输入任务发布者的ID，或者任务接取者的ID", trigger: "blur" }],
          path: [{ required: true, message: "请输入图片的本地路径", trigger: "blur" }]
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
            const url = this.dialogModal.id ? COREIMG_EDIT_URL : COREIMG_ADD_URL
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
