/**
* @Author: leemon
* @Date: 2019-5-10 10:38:07
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="180px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="名字" prop="name">
          <el-input v-model="dialogModal.name"/>
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <el-input v-model="dialogModal.level" placeholder="非合作商为0，其他依次增长"/>
        </el-form-item>
        <el-form-item label="发布任务手续费(%)" prop="feePercent">
          <el-input v-model="dialogModal.feePercent"/>
        </el-form-item>
        <el-form-item label="布任务最低服务费(元)" prop="minFeePrice">
          <el-input v-model="dialogModal.minFeePrice"/>
        </el-form-item>
        <el-form-item label="任务币提现手续费(%)" prop="missionPaymentPercent">
          <el-input v-model="dialogModal.missionPaymentPercent"/>
        </el-form-item>
        <el-form-item label="月卡价格(元/月)" prop="mouthPrice">
          <el-input v-model="dialogModal.mouthPrice"/>
        </el-form-item>
        <el-form-item label="年卡价格(元/年)" prop="yearPrice">
          <el-input v-model="dialogModal.yearPrice"/>
        </el-form-item>
        <el-form-item label="免费推荐时间(小时/日)" prop="adHour">
          <el-input v-model="dialogModal.adHour"/>
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
  import { COREPARTNER_ADD_URL, COREPARTNER_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    name: null,
    level: null,
    feePercent: null,
    missionPaymentPercent: null,
    minFeePrice: null,
    mouthPrice: null,
    yearPrice: null,
    adHour: null
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
          name: [{ required: true, message: "请输入合作商的级别名字", trigger: "blur" }],
          feePercent: [{ required: true, message: "请输入交易手续费的百分比，乘以100之后的数值", trigger: "blur" }],
          missionPaymentPercent: [{ required: true, message: "请输入任务币的提现手续费，乘以100之后的数值", trigger: "blur" }],
          minFeePrice: [{ required: true, message: "请输入发布任务最低的服务费金额，单位元", trigger: "blur" }],
          mouthPrice: [{ required: true, message: "请输入月卡价格，单位元/月", trigger: "blur" }],
          yearPrice: [{ required: true, message: "请输入年卡价格，单位元/年", trigger: "blur" }],
          adHour: [{ required: true, message: "请输入免费推荐的时间，单位小时/日", trigger: "blur" }]
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
            const url = this.dialogModal.id ? COREPARTNER_EDIT_URL : COREPARTNER_ADD_URL
            const requestData = _.cloneDeep(this.dialogModal)
            requestData.minFeePrice = requestData.minFeePrice * 100
            requestData.mouthPrice = requestData.mouthPrice * 100
            requestData.yearPrice = requestData.yearPrice * 100
            this.httpPostWithLoading(url, requestData, ".el-dialog").then(({ data, success }) => {
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
        this.dialogModal.minFeePrice = this.dialogModal.minFeePrice / 100
        this.dialogModal.mouthPrice = this.dialogModal.mouthPrice / 100
        this.dialogModal.yearPrice = this.dialogModal.yearPrice / 100
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
