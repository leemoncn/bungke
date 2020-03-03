/** * @Author: leemon * @Date: 2019-5-7 9:02:22 */
<template>
  <div>
    <el-dialog
      ref="dialogView"
      :close-on-click-modal="false"
      :title="getDialogTitle()"
      :visible.sync="isEditDialogShow"
      width="35%"
      height="300px"
      @opened="dialogOpened"
      @open="dialogOpen"
      @close="dialogClose"
    >
      <el-form
        :model="dialogModal"
        ref="dialogForm"
        :rules="dialogRules"
        label-position="left"
        label-width="80px"
        :disabled="!editDialogEditable"
      >
        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.missionId" disabled/>
        </el-form-item>
        <el-form-item label="发布者" prop="userId">
          <el-input v-model="dialogModal.userId" disabled/>
        </el-form-item>

        <el-form-item label="任务类型" prop="type_property_id">
          <el-input :value="getPropertyNameById(dialogModal.typePropertyId)" disabled/>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="dialogModal.title" disabled/>
        </el-form-item>
        <el-form-item label="单价(元)" prop="price">
          <el-input :value="dialogModal.price / 100.0" disabled/>
        </el-form-item>
        <el-form-item label="数量" prop="count">
          <el-input v-model="dialogModal.count" disabled/>
        </el-form-item>
        <el-form-item label="链接" prop="url">
          <el-input v-model="dialogModal.url" disabled/>
        </el-form-item>
        <el-form-item label="文字验证" prop="text_verify">
          <el-input v-model="dialogModal.textVerify" disabled/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dialogModal.remark" type="textarea" autosize resize="none" disabled/>
        </el-form-item>
        <el-form-item label="设备类型" prop="mobile_property_id">
          <el-input :value="getPropertyNameById(dialogModal.mobilePropertyId)" disabled/>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-input v-model="dialogModal.publishTime" disabled/>
        </el-form-item>
        <el-form-item label="截止时间" prop="deadlineTime">
          <el-input v-model="dialogModal.deadlineTime" disabled/>
        </el-form-item>
        <el-form-item label="置顶结束时间" prop="topEndTime">
          <el-input v-model="dialogModal.topEndTime" disabled/>
        </el-form-item>
        <el-form-item label="审核图样">
          <div v-for="(item, index) in dialogModal.sampleList" :key="index">
            <img :src="imgFullPath(item.path)" alt="" style="max-width: 100%;display: block;margin: 0 auto;"/>
            <el-divider>▲ 审核图{{ index + 1 }} ▲</el-divider>
          </div>
        </el-form-item>
        <el-form-item label="操作图样">
          <div v-for="(item, index) in dialogModal.explainList" :key="index">
            <p style="font-weight: bold" v-show="item.text">步骤{{ index + 1 }}: {{ item.text }}</p>
            <img :src="imgFullPath(item.img)" alt="" style="width: 100%;display: block" v-show="item.img"/>
            <el-divider>▲ 操作步骤{{ index + 1 }} ▲</el-divider>
          </div>
        </el-form-item>
        <el-form-item label="任务状态" prop="statusPropertyId">
          <el-select v-model="dialogModal.statusPropertyId" placeholder="请选择" :disabled="!statusEditable">
            <el-option
              v-for="(item, index) in selectableStatusList"
              :key="index"
              :label="getPropertyNameById(item)"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="原因"
          prop="reason"
          v-show="dialogModal.statusPropertyId === 29 || dialogModal.statusPropertyId === 57"
        >
          <el-input v-model="dialogModal.reason" :disabled="!reasonEditable"/>
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
  import { COREIMG_QUERY_URL, COREMISSION_DETAIL_STEP_URL, COREMISSION_EDIT_URL } from "@/config/host"
  import { getPropertyListByType, PropertyType } from "@/service/property"
  import { Loading } from "element-ui"

  const defaultDialogModal = {
    id: null,
    missionDetailId: null,
    userId: null,
    publishTime: null,
    usable: null,
    topEndTime: null,
    reason: null
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
      missionStatusPropertyList: function () {
        return getPropertyListByType(PropertyType.MISSION_PUBLISH)
      }
    },
    data () {
      return {
        selectableStatusList: [],
        statusEditable: false,
        reasonEditable: false,
        dialogModal: _.cloneDeep(defaultDialogModal),
        dialogRules: {
          statusPropertyId: [{ required: true, message: "请选择任务状态", trigger: "blur" }]
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
        this.$refs.dialogForm.validate(valid => {
          if (valid) {
            this.httpPostWithLoading(COREMISSION_EDIT_URL, {
              id: this.dialogModal.missionId,
              statusPropertyId: this.dialogModal.statusPropertyId,
              reason: this.dialogModal.reason
            }, ".el-dialog").then(({ data, success }) => {
              if (success) {
                this.$message({
                  message: "菜单编辑成功",
                  type: "success"
                })
                this.closeEditDialog()
                this.reloadParentData()
              }
            })
          }
        })
      },
      onCancelButtonPressed () {
        console.log("dialog = ", this.dialogModal)
        this.closeEditDialog()
      },
      dialogOpened () {
        this.$refs.dialogForm.clearValidate()
      },
      async dialogOpen () {
        this.selectableStatusList = []
        this.statusEditable = false
        this.reasonEditable = false
        let options = { target: ".el-dialog" }
        let loading = Loading.service(options)
        const sampleResult = await this.httpPost(COREIMG_QUERY_URL, {
          dataId: this.editDialogData.missionId,
          type: 0 // 审核图样
        })
        if (sampleResult.success) {
          const explainResult = await this.httpPost(COREMISSION_DETAIL_STEP_URL, {
            id: this.editDialogData.missionId
          })
          if (explainResult.success) {
            console.log("editDialogData = ", this.editDialogData)
            this.dialogModal = this.editDialogData ? _.cloneDeep(this.editDialogData) : _.cloneDeep(defaultDialogModal)
            this.dialogModal.explainList = explainResult.data
            this.dialogModal.sampleList = sampleResult.data
            // 根据初始化的任务状态决定任务可选状态
            if (this.editDialogData.statusPropertyId === 28) {
              // 待审核
              this.statusEditable = true
              this.reasonEditable = true
              this.selectableStatusList = [28, 32, 57] // 待审核、已发布、审核驳回
            }
            if (this.editDialogData.statusPropertyId === 32) {
              // 已发布
              this.statusEditable = true
              this.reasonEditable = true
              this.selectableStatusList = [32, 29] // 已发布、被封
            }
          }
        }
        loading.close()
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

<style scoped lang="css"></style>
