/**
* @Author: leemon
* @Date: 2019-6-27 20:51:00
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow"
               width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="100px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="用户id" prop="userId">
          <el-input v-model="dialogModal.userId"/>
        </el-form-item>
        <el-form-item label="合作商" prop="partnerId">
          <el-select v-model="dialogModal.partnerId" placeholder="请选择" value="">
            <el-option v-for="(item,index) in partnerInfo" :key="index" :label="item.name" :value="item.id"
                       v-show="item.level !== 0"/>
          </el-select>
        </el-form-item>
        <el-form-item label="赠送时间" prop="isYear" v-show="editDialogEditable">
          <el-select v-model="dialogModal.isYear" placeholder="请选择" value="">
            <el-option label="月" :value="false"/>
            <el-option label="年" :value="true"/>
          </el-select>
        </el-form-item>
        <el-form-item label="花费的钱(元)" prop="price" v-show="!editDialogEditable">
          <el-input :value="dialogModal.price/100.0"/>
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
  import {
    COREPARTNERPURCHASE_SEND_URL
  } from "@/config/host"

  const defaultDialogModal = {
    id: null,
    userId: null,
    partnerId: null,
    price: null
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
      partnerInfo: {
        type: Array
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
          userId: [{ required: true, message: "请输入用户id", trigger: "blur" }],
          partnerId: [{ required: true, message: "请选择合作商", trigger: "blur" }],
          isYear: [{ required: true, message: "请选择赠送时间", trigger: "blur" }]
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
            this.httpPostWithLoading(COREPARTNERPURCHASE_SEND_URL, this.dialogModal, ".el-dialog").then(({ data, success }) => {
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
