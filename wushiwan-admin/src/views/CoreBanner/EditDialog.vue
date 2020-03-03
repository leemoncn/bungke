/**
* @Author: leemon
* @Date: 2019-11-18 20:04:03
*/
<template>
  <div>
    <el-dialog ref="dialogView" :title="getDialogTitle()" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose" :close-on-click-modal="false">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="80px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="图片(356x154)" placeholder="图片将会以356x154的分辨率显示" prop="img">
          <el-upload
            class="avatar-uploader"
            action="http://upload.qiniup.com/"
            :show-file-list="false"
            :data="uploadData"
            :on-error="handleAvatarFailed"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <img v-if="dialogModal.img" :src="imgFullPath(dialogModal.img)" class="avatar" alt="">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="html" prop="html">
          <el-input v-model="dialogModal.html" placeholder="可以输入网址或者编辑网页"/>
          <el-button @click="editorDialogVisible = true">编辑网页</el-button>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="dialogModal.sort"/>
        </el-form-item>
        <el-form-item label="是否可用" prop="usable">
          <el-select v-model="dialogModal.usable" placeholder="请选择" value="">
            <el-option label="是" :value="true"/>
            <el-option label="否" :value="false"/>
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="onCancelButtonPressed()">取消</el-button>
        <el-button type="primary" @click="onConfirmButtonPressed()">确定</el-button>
      </div>

    </el-dialog>

    <editor-dialog :editor-visible.sync="editorDialogVisible" @confirm="onEditorDialogConfirm"
                   :editorData="dialogModal.html"/>
  </div>
</template>

<script>
  import { COREBANNER_ADD_URL, COREBANNER_EDIT_URL } from "@/config/host"
  import { uploadToQiniu } from "@/service/upload"
  import EditorDialog from "@/components/EditorDialog"

  const defaultDialogModal = {
    id: null,
    img: null,
    html: null,
    sort: null,
    usable: null
  }

  export default {
    name: "EditDialog",
    components: { EditorDialog },
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
          img: [{ required: true, message: "请上传图片", trigger: "blur" }],
          html: [{ required: false, message: "请输入网址或编辑网页代码", trigger: "blur" }],
          sort: [{ required: true, message: "请输入排序", trigger: "blur" }],
          usable: [{ required: true, message: "请输入是否可用", trigger: "blur" }]
        },
        uploadData: {},
        editorDialogVisible: false
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
            const url = this.dialogModal.id ? COREBANNER_EDIT_URL : COREBANNER_ADD_URL
            this.httpPostWithLoading(url, this.dialogModal, ".el-dialog").then(({ data, success }) => {
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
      },
      handleAvatarSuccess (res, file) {
        this.dialogModal.img = res.data.key
        this.$alert("图片上传成功")
        this.hideLoading()
      },
      handleAvatarFailed () {
        this.$alert("图片上传失败")
        this.hideLoading()
      },
      async beforeAvatarUpload (file) {
        const isJPG = file.type === "image/jpeg"
        const isPNG = file.type === "image/png"
        // const isLt2M = file.size / 1024 / 1024 < 2
        // if (!isLt2M) {
        //   this.$message.error("上传头像图片大小不能超过 2MB!")
        // }
        if (!isJPG && !isPNG) {
          this.$message.error("上传图片只能是 JPG或PNG 格式!")
          return false
        }

        this.showLoading()
        let _this = this
        await uploadToQiniu(1, (data) => {
          const list = data.list
          _this.dialogModal.key = data.key
          _this.uploadData.token = list[0].token
          _this.uploadData.key = list[0].fileName
          _this.uploadData.fileName = file.name
          _this.uploadData["x:redisKey"] = data.key
        })
        return true
      },
      onEditorDialogConfirm (value) {
        this.dialogModal.html = value
      }
    }
  }
</script>

<style scoped lang="css">
  .avatar-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 356px;
  }

  .avatar-uploader:hover {
    border-color: #409EFF;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 356px;
    height: 154px;
    line-height: 154px;
    text-align: center;
  }

  .avatar {
    width: 356px;
    height: 154px;
    display: block;
    object-fit: fill;
  }
</style>
