/**
* @Author: leemon
* @Date: 2019-5-9 20:37:25
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
        <el-form-item label="任务类型" prop="typePropertyId">
          <el-select v-model="dialogModal.typePropertyId" placeholder="请选择">
            <el-option
              v-for="(item,index) in missionTypePropertyList"
              :key="index"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="最低出价(元)" prop="minPrice">
          <el-input v-model="dialogModal.minPrice"/>
        </el-form-item>
        <el-form-item label="最低任务数量" prop="minCount">
          <el-input v-model="dialogModal.minCount"/>
        </el-form-item>
        <el-form-item label="审核验证图最多上传数量" prop="verifyImgCount">
          <el-input v-model="dialogModal.verifyImgCount" placeholder="一般数量为5"/>
        </el-form-item>
        <el-form-item label="状态" prop="usable">
          <el-select v-model="dialogModal.usable" placeholder="请选择" value="">
            <el-option label="开启" :value="true"/>
            <el-option label="关闭" :value="false"/>
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
  import { COREMISSIONRULE_ADD_URL, COREMISSIONRULE_EDIT_URL } from "@/config/host"
  import { getPropertyListByType, PropertyType } from "@/service/property"

  const defaultDialogModal = {
    id: null,
    typePropertyId: null,
    minPrice: null,
    minCount: null,
    verifyImgCount: null,
    usable: null
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
      missionTypePropertyList: function () {
        return getPropertyListByType(PropertyType.MISSION_TYPE)
      }
    },
    data () {
      return {
        dialogModal: _.cloneDeep(defaultDialogModal),
        dialogRules: {
          typePropertyId: [{ required: true, message: "请输入任务类型的属性表ID", trigger: "blur" }],
          minPrice: [{ required: true, message: "请输入最低出价，单位为分", trigger: "blur" }],
          minCount: [{ required: true, message: "请输入最低任务数量", trigger: "blur" }],
          verifyImgCount: [{ required: true, message: "请输入审核验证图最多上传数量", trigger: "blur" }]
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
            const url = this.dialogModal.id ? COREMISSIONRULE_EDIT_URL : COREMISSIONRULE_ADD_URL
            const requestData = _.cloneDeep(this.dialogModal)
            requestData.minPrice *= 100
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
        if (this.dialogModal) {
          this.dialogModal.minPrice /= 100
        }
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
