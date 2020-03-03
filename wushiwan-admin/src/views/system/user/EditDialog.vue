/**
* @Author: leemon
* @Date: 2019-4-28 21:34:06
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
        <el-form-item label="上级id" prop="superiorId">
          <el-input v-model="dialogModal.superiorId"/>
        </el-form-item>
        <el-form-item label="分成id(代理商)" prop="agencyId">
          <el-input v-model="dialogModal.agencyId"/>
        </el-form-item>
        <el-form-item label="代理商id" prop="partnerId">
          <el-input v-model="dialogModal.partnerId"/>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="dialogModal.nickname"/>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="dialogModal.phone"/>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="dialogModal.email"/>
        </el-form-item>
        <el-form-item label="支付宝" prop="alipay">
          <el-input v-model="dialogModal.alipay"/>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="dialogModal.realName"/>
        </el-form-item>
        <el-form-item label="登陆状态" prop="loginStatusPropertyId">
          <el-select v-model="dialogModal.loginStatusPropertyId" placeholder="请选择">
            <el-option label="允许登录" :value="3"/>
            <el-option label="禁止登录" :value="4"/>
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="dialogModal.userType" placeholder="请选择">
            <el-option label="前台用户" :value="1"/>
            <el-option label="后台用户" :value="2"/>
          </el-select>
        </el-form-item>
        <el-form-item label="保证金" prop="deposit">
          <el-input v-model="dialogModal.deposit"/>
        </el-form-item>
        <el-form-item label="任务币" prop="missionCoin">
          <el-input v-model="dialogModal.missionCoin"/>
        </el-form-item>
        <el-form-item label="收入" prop="earning">
          <el-input v-model="dialogModal.earning"/>
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
  import { SYSUSER_ADD_URL, SYSUSER_EDIT_URL } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    superiorId: null,
    agencyId: null,
    partnerId: null,
    nickname: null,
    phone: null,
    email: null,
    loginStatusPropertyId: null,
    userType: null,
    deposit: null,
    missionCoin: null,
    earning: null,
    realName: null,
    alipay: null
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
          // superiorId: [{ message: "请输入用户的上级id", trigger: "blur" }],
          // agencyId: [{ required: true, message: "请输入我作为代理商的分成级别", trigger: "blur" }],
          // partnerId: [{ required: true, message: "请输入代理商的id", trigger: "blur" }],
          // nickname: [{ required: true, message: "请输入nickname", trigger: "blur" }],
          // phone: [{ message: "请输入用户绑定的手机号", trigger: "blur" }],
          // email: [{ message: "请输入邮箱", trigger: "blur" }],
          // loginStatusPropertyId: [{ required: true, message: "请输入登陆状态，比如允许登陆、禁止登陆、需绑定手机才可以登陆等等", trigger: "blur" }],
          // userType: [{ required: true, message: "请输入用户类型", trigger: "blur" }],
          // deposit: [{ required: true, message: "请输入保证金，单位为分", trigger: "blur" }],
          // missionCoin: [{ required: true, message: "请输入任务币，单位分", trigger: "blur" }],
          // earning: [{ required: true, message: "请输入我当前收入，包括任务收入和分红，单位分", trigger: "blur" }]
          // alipay: [{ required: true, message: "请输入我当前收入，包括任务收入和分红，单位分", trigger: "blur" }]
          // realName: [{ required: true, message: "请输入我当前收入，包括任务收入和分红，单位分", trigger: "blur" }]
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
            const url = this.dialogModal.id ? SYSUSER_EDIT_URL : SYSUSER_ADD_URL
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
