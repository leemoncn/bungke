/**
* @Author: leemon
* @Date: 2019-5-19 22:55:06
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
        <el-form-item label="任务类型" prop="typePropertyId">
          <el-input v-model="dialogModal.typePropertyId"/>
        </el-form-item>
        <el-form-item label="发布人id" prop="userId">
          <el-input v-model="dialogModal.userId"/>
        </el-form-item>
        <el-form-item label="支持设备类型" prop="mobilePropertyId">
          <el-input v-model="dialogModal.mobilePropertyId"/>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="dialogModal.title"/>
        </el-form-item>
        <el-form-item label="截止时间" prop="deadlineTime">
          <el-input v-model="dialogModal.deadlineTime"/>
        </el-form-item>
        <el-form-item label="出价" prop="price">
          <el-input v-model="dialogModal.price"/>
        </el-form-item>
        <el-form-item label="任务数量" prop="count">
          <el-input v-model="dialogModal.count"/>
        </el-form-item>
        <el-form-item label="服务费金额" prop="feePrice">
          <el-input v-model="dialogModal.feePrice"/>
        </el-form-item>
        <el-form-item label="手续费百分比" prop="feePercent">
          <el-input v-model="dialogModal.feePercent"/>
        </el-form-item>
        <el-form-item label="任务规则id" prop="missionRuleId">
          <el-input v-model="dialogModal.missionRuleId"/>
        </el-form-item>
        <el-form-item label="链接" prop="url">
          <el-input v-model="dialogModal.url"/>
        </el-form-item>
        <el-form-item label="文字验证" prop="textVerify">
          <el-input v-model="dialogModal.textVerify"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dialogModal.remark"/>
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
  import { COREMISSIONDETAIL_ADD_URL, COREMISSIONDETAIL_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    typePropertyId: null,
    userId: null,
    mobilePropertyId: null,
    title: null,
    deadlineTime: null,
    price: null,
    count: null,
    feePrice: null,
    feePercent: null,
    missionRuleId: null,
    url: null,
    textVerify: null,
    remark: null
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
          typePropertyId: [{ required: true, message: "请输入发布任务的类型，在属性表中的id", trigger: "blur" }],
          userId: [{ required: true, message: "请输入发布人id", trigger: "blur" }],
          mobilePropertyId: [{ required: true, message: "请输入属性表里面的支持设备类型", trigger: "blur" }],
          title: [{ required: true, message: "请输入标题，12字以内", trigger: "blur" }],
          deadlineTime: [{ required: true, message: "请输入截止时间", trigger: "blur" }],
          price: [{ required: true, message: "请输入出价，单位分", trigger: "blur" }],
          count: [{ required: true, message: "请输入任务数量", trigger: "blur" }],
          feePrice: [{ required: true, message: "请输入服务费金额，单位为分", trigger: "blur" }],
          feePercent: [{ required: true, message: "请输入手续费的百分比，乘以100之后的数值", trigger: "blur" }],
          missionRuleId: [{ required: true, message: "请输入任务规则的id", trigger: "blur" }]
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
            const url = this.dialogModal.id ? COREMISSIONDETAIL_EDIT_URL : COREMISSIONDETAIL_ADD_URL
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
