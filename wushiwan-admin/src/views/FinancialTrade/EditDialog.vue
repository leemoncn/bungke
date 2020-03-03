/**
* @Author: leemon
* @Date: 2019-7-14 9:54:00
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
        <el-form-item label="单号" prop="tradeNo">
          <el-input v-model="dialogModal.tradeNo"/>
        </el-form-item>
        <el-form-item label="金额，单位分" prop="amount">
          <el-input v-model="dialogModal.amount"/>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="dialogModal.title"/>
        </el-form-item>
        <el-form-item label="内容" prop="description">
          <el-input v-model="dialogModal.description"/>
        </el-form-item>
        <el-form-item label="第三方订单号" prop="thirdTradeNo">
          <el-input v-model="dialogModal.thirdTradeNo"/>
        </el-form-item>
        <el-form-item label="交易状态,0是未完成，1是已完成" prop="finished">
          <el-input v-model="dialogModal.finished"/>
        </el-form-item>
        <el-form-item label="支付完成时间" prop="finishTime">
          <el-input v-model="dialogModal.finishTime"/>
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
  import { FINANCIALTRADE_ADD_URL, FINANCIALTRADE_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    userId: null,
    tradeNo: null,
    amount: null,
    title: null,
    description: null,
    thirdTradeNo: null,
    finished: null,
    finishTime: null
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
		      tradeNo: [{ required: true, message: "请输入单号", trigger: "blur" }],
		      amount: [{ required: true, message: "请输入金额，单位分", trigger: "blur" }],
		      title: [{ required: true, message: "请输入标题", trigger: "blur" }],
		      description: [{ required: true, message: "请输入内容", trigger: "blur" }],
		      thirdTradeNo: [{ required: true, message: "请输入第三方订单号", trigger: "blur" }],
		      finished: [{ required: true, message: "请输入交易状态,0是未完成，1是已完成", trigger: "blur" }],
		      finishTime: [{ required: true, message: "请输入支付完成时间", trigger: "blur" }]
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
          const url = this.dialogModal.id ? FINANCIALTRADE_EDIT_URL : FINANCIALTRADE_ADD_URL
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
