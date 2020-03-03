/**
* @Author: leemon
* @Date: 2019-8-18 14:40:21
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
        <el-form-item label="版本号" prop="versionCode">
          <el-input v-model="dialogModal.versionCode"
                    :placeholder="lastVersion?'上个版本号: ' + lastVersion.versionCode:'1'"/>
        </el-form-item>
        <el-form-item label="版本名" prop="versionName">
          <el-input v-model="dialogModal.versionName"
                    :placeholder="lastVersion?'上个版本名: ' + lastVersion.versionName:'1.0.0'"/>
        </el-form-item>
        <el-form-item label="强制更新" prop="forceUpdate">
          <el-select v-model="dialogModal.forceUpdate" placeholder="请选择" value="">
            <el-option label="否" :value="false"/>
            <el-option label="是" :value="true"/>
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
  import { COREVERSION_ADD_URL, COREVERSION_EDIT_URL, DEV_SERVER, HOST } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    versionCode: null,
    versionName: null,
    forceUpdate: null
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
      },
      lastVersion: {
        type: Object
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
          versionCode: [{ required: true, message: "请输入版本号", trigger: "blur" }],
          versionName: [{ required: true, message: "请输入版本名", trigger: "blur" }],
          forceUpdate: [{ required: true, message: "请选择是否强制更新", trigger: "blur" }]
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
        if (HOST !== DEV_SERVER) {
          this.$message.error("只有在测试服务器(wushiwan)上可以进行版本发布")
          return
        }
        if (!this.editDialogEditable) {
          this.onCancelButtonPressed()
          return
        }
        this.$refs.dialogForm.validate((valid) => {
          if (valid) {
            const url = this.dialogModal.id ? COREVERSION_EDIT_URL : COREVERSION_ADD_URL
            this.httpPostWithLoading(url, this.dialogModal, ".el-dialog").then(({ data, success }) => {
              if (success) {
                this.$message({
                  message: "菜单编辑成功",
                  type: "success"
                })
              } else {
                this.$message({
                  message: "部署请求已提交，请5分钟后刷新结果",
                  type: "success"
                })
              }
              this.closeEditDialog()
              this.reloadParentData()
            }).catch(() => {

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
