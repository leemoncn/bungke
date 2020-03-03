/**
* @Author: leemon
* @Date: 2019-11-19 15:26
* @Description:
*/
<template>
  <el-dialog ref="editorDialog" title="网页编辑" :visible.sync="isEditDialogShow" width="95%"
             :close-on-click-modal="false">
    <div style="height: 700px">
      <tinymce-editor @input="onEditorChange" :value="editorData"/>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="onCancelButtonPressed()">取消</el-button>
      <el-button type="primary" @click="onConfirmButtonPressed()">确定</el-button>
    </div>

  </el-dialog>
</template>

<script>
  import TinymceEditor from "@/components/TinymceEditor"

  export default {
    name: "EditorDialog",
    components: { TinymceEditor },
    props: {
      editorVisible: {
        type: Boolean,
        required: true
      },
      editorData: {
        type: Object
      }
    },
    computed: {
      isEditDialogShow: {
        get: function () {
          return this.editorVisible
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
        htmlValue: ""
      }
    },
    methods: {
      onConfirmButtonPressed () {
        this.$emit("confirm", this.htmlValue)
        this.closeEditDialog()
      },
      onCancelButtonPressed () {
        this.closeEditDialog()
      },
      closeEditDialog () {
        this.$emit("update:editorVisible", false)
      },
      onEditorChange (htmlValue) {
        this.htmlValue = htmlValue
      }
    }
  }
</script>

<style scoped>

</style>
