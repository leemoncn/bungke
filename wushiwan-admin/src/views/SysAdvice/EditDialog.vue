/**
* @Author: leemon
* @Date: 2019-7-15 9:59:59
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow"
               width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="80px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <user-popover :data="dialogModal.userId"/>
        </el-form-item>
        <el-form-item label="内容" prop="message">
          <el-input v-model="dialogModal.message"/>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input v-model="dialogModal.contactInfo"/>
        </el-form-item>
        <el-form-item label="图片" prop="haveImg">
          <img-popover :img-data="{dataId:dialogModal.id,type:3}" v-if="dialogModal.haveImg"/>
          <img-popover :img-path-list="[]" v-else/>
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
  import { SYSADVICE_ADD_URL, SYSADVICE_EDIT_URL } from "@/config/host"
  import UserPopover from "@/components/UserPopover"
  import ImgPopover from "@/components/ImgPopover"

  const defaultDialogModal = {
    id: null,
    userId: null,
    message: null,
    contactInfo: null,
    haveImg: null
  }

  export default {
    name: "EditDialog",
    components: { ImgPopover, UserPopover },
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
          message: [{ required: true, message: "请输入意见反馈内容", trigger: "blur" }],
          contactInfo: [{ required: true, message: "请输入联系方式", trigger: "blur" }],
          haveImg: [{ required: true, message: "请输入是否包含图片", trigger: "blur" }]
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
            const url = this.dialogModal.id ? SYSADVICE_EDIT_URL : SYSADVICE_ADD_URL
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
