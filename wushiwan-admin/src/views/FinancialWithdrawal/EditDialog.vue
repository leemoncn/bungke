/**
* @Author: leemon
* @Date: 2019-6-29 15:06:53
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
        <!--<el-form-item label="userId" prop="userId">-->
        <!--<el-input v-model="dialogModal.userId"/>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="申请提现的货币数值" prop="num">-->
        <!--<el-input v-model="dialogModal.num"/>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="货币转换为人民币的数值（去掉给上级的分成后）" prop="money">-->
        <!--<el-input v-model="dialogModal.money"/>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="货币的类型" prop="typePropertyId">-->
        <!--<el-input v-model="dialogModal.typePropertyId"/>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="货币变化的原因" prop="reasonPropertyId">-->
        <!--<el-input v-model="dialogModal.reasonPropertyId"/>-->
        <!--</el-form-item>-->
        <el-form-item label="是否批准" prop="approve">
          <el-select v-model="dialogModal.approve" placeholder="请选择">
            <el-option label="批准" :value="true"/>
            <el-option label="不批准" :value="false"/>
          </el-select>
        </el-form-item>
        <!--<el-form-item label="提现批准时间" prop="approveTime">-->
        <!--<el-input v-model="dialogModal.approveTime"/>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="批准人" prop="approveUser">-->
        <!--<el-input v-model="dialogModal.approveUser"/>-->
        <!--</el-form-item>-->

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="onCancelButtonPressed()">取消</el-button>
        <el-button type="primary" @click="onConfirmButtonPressed()">确定</el-button>
      </div>

    </el-dialog>

  </div>
</template>

<script>
  import { FINANCIALWITHDRAWAL_ADD_URL, FINANCIALWITHDRAWAL_REVIEW_WITHDRAWAL_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    // userId: null,
    // num: null,
    // money: null,
    // typePropertyId: null,
    // reasonPropertyId: null,
    approve: null
    // approveTime: null,
    // approveUser: null
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
          // userId: [{ required: true, message: "请输入userId", trigger: "blur" }],
          // num: [{ required: true, message: "请输入申请提现的货币数值", trigger: "blur" }],
          // money: [{ required: true, message: "请输入货币转换为人民币的数值（去掉给上级的分成后）", trigger: "blur" }],
          // typePropertyId: [{ required: true, message: "请输入货币的类型", trigger: "blur" }],
          // reasonPropertyId: [{ required: true, message: "请输入货币变化的原因", trigger: "blur" }],
          approve: [{ required: true, message: "请选择是否批准", trigger: "blur" }]
          // approveTime: [{ required: true, message: "请输入提现批准时间", trigger: "blur" }],
          // approveUser: [{ required: true, message: "请输入批准人", trigger: "blur" }],
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
            const url = this.dialogModal.id ? FINANCIALWITHDRAWAL_REVIEW_WITHDRAWAL_URL : FINANCIALWITHDRAWAL_ADD_URL
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
