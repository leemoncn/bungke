/**
* @Author: leemon
* @Date: 2019-4-27 11:29:34
*/
<template>
  <div>
    <el-dialog ref="dialogView" :close-on-click-modal="false" :title="getDialogTitle()" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-form :model="dialogModal" ref="dialogForm" :rules="dialogRules" label-position="left" label-width="100px"
               :disabled="!editDialogEditable">

        <el-form-item label="id" prop="id">
          <el-input v-model="dialogModal.id" disabled/>
        </el-form-item>
        <el-form-item label="菜单标题" prop="name">
          <el-input v-model="dialogModal.name"/>
        </el-form-item>
        <el-form-item label="父节点" prop="parentId">
          <el-button size="small" @click="onSelectParentButtonPressed">{{parentModalName}}</el-button>
        </el-form-item>
        <el-form-item label="链接" prop="href">
          <el-input v-model="dialogModal.href"/>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-button plain @click="()=>{this.showIconDialog = true}">
            <svg-icon :icon-class="dialogModal.icon" v-if="dialogModal.icon"/>
            <span v-else>无</span>
          </el-button>
        </el-form-item>
        <el-form-item label="是否显示" prop="isShow">
          <el-select v-model="dialogModal.isShow" placeholder="请选择">
            <el-option label="否" :value="false"/>
            <el-option label="是" :value="true"/>
          </el-select>
        </el-form-item>
        <el-form-item label="组件的路径" prop="component">
          <el-input v-model="dialogModal.component" placeholder="例如 CorePartner/index"/>
        </el-form-item>
        <el-form-item label="组件的名字" prop="componentName">
          <el-input v-model="dialogModal.componentName" placeholder="例如 CorePartnerManagement"/>
        </el-form-item>
        <el-form-item label="权限标识" prop="permission">
          <el-input v-model="dialogModal.permission" placeholder="例如 core_partner,如果是文件夹，则一般填链接名(不重复即可)"/>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="dialogModal.sort"/>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="onCancelButtonPressed()">取消</el-button>
        <el-button type="primary" @click="onConfirmButtonPressed()">确定</el-button>
      </div>

    </el-dialog>

    <el-dialog title="选择上级菜单" :visible.sync="showParentMenuTree" width="25%">
      <el-tree ref="treeView" :data="parentTreeData" :props="parentTreeProps"/>
      <div slot="footer" class="dialog-footer">
        <el-button @click="unselectTreeNode()">取消</el-button>
        <el-button type="primary" @click="selectTreeNode()">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="选择图标" :visible.sync="showIconDialog" width="40%">
      <el-row>
        <el-col>
          <div style="display: inline-block" @click="onIconPressed(name.replace('.svg',''))"
               v-for="name in fileNameList" :key="name">
            <svg-icon style="margin-left: 5px" :icon-class="name.replace('.svg','')" width="2em" height="2em"/>
          </div>
        </el-col>
      </el-row>
    </el-dialog>

  </div>
</template>

<script>
  import { SYSMENU_ADD_URL, SYSMENU_EDIT_URL } from "@/config/host"
  import { getParentRoute } from "@/util/router"
  import fileNameArray from "@/icon/svg/filename"
  import { mapGetters } from "vuex"

  const defaultDialogModal = {
    id: null,
    parentId: 0,
    name: null,
    sort: 0,
    href: null,
    icon: null,
    isShow: true,
    component: null,
    componentName: null,
    permission: null
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
      isAddChild: Boolean
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
      parentModalName: function () {
        if (this.parentModal) {
          return this.parentModal.name
        }
        return "根路径"
      },
      ...mapGetters([
        "menuList"
      ])
    },
    watch: {
      editDialogData: function (newData, oldData) {
        if (newData) {
          if (newData.parentId !== null) {
            this.parentModal = getParentRoute(this.$store.getters.menuList, newData.parentId)
          } else {
            this.parentModal = null
          }
        }
      }
    },
    data () {
      return {
        dialogModal: _.cloneDeep(defaultDialogModal),
        parentModal: null,
        showParentMenuTree: false,
        parentTreeData: [{
          id: 0,
          name: "根路径",
          children: _.cloneDeep(this.$store.getters.menuList)
        }],
        parentTreeProps: {
          children: "children",
          id: "id",
          label: function (data, node) {
            return data.name
          }
        },
        dialogRules: {
          // parentId: [{ required: true, message: "请输入父节点", trigger: "blur" }],
          name: [{ required: true, message: "请输入菜单标题", trigger: "blur" }],
          sort: [{ required: true, message: "请输入排序", trigger: "blur" }],
          href: [{ required: true, message: "请输入链接", trigger: "blur" }],
          isShow: [{ required: true, message: "请输入是否在菜单中显示", trigger: "blur" }]
        },
        showIconDialog: false,
        fileNameList: fileNameArray
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
            const url = this.dialogModal.id ? SYSMENU_EDIT_URL : SYSMENU_ADD_URL
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
        this.parentModal = null
        if (this.isAddChild) {
          this.dialogModal = _.cloneDeep(defaultDialogModal)
          this.dialogModal.sort = this.getChildrenMaxSort(this.editDialogData)
          this.dialogModal.parentId = this.editDialogData.id
          this.parentModal = getParentRoute(this.$store.getters.menuList, this.dialogModal.parentId)
        } else {
          this.dialogModal = this.editDialogData ? _.cloneDeep(this.editDialogData) : _.cloneDeep(defaultDialogModal)
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
      },
      onSelectParentButtonPressed () {
        this.showParentMenuTree = true
      },
      selectTreeNode () {
        const currentNode = this.$refs.treeView.getCurrentNode()
        if (currentNode) { // 选择了某一个节点
          this.dialogModal.parentId = currentNode.id
          if (currentNode.id !== null) {
            this.parentModal = getParentRoute(this.$store.getters.menuList, currentNode.id)
          } else {
            this.parentModal = null
          }
        }
        this.showParentMenuTree = false
      },
      unselectTreeNode () {
        this.showParentMenuTree = false
      },
      onIconPressed (name) {
        this.dialogModal.icon = name
        this.showIconDialog = false
      },
      getChildrenMaxSort (row) {
        if (row.children instanceof Array && row.children.length > 0) {
          const children = row.children
          // 最后一个元素的sort + 10
          return parseInt(children[children.length - 1].sort) + 10
        }
        return 10
      }
    }
  }
</script>

<style scoped lang="css">

</style>
