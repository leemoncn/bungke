/**
* @Author: leemon
* @Date: 2019-4-29 21:27:42
*/
<template>
  <div class="permission-container">
    <el-dialog ref="dialogView" :close-on-click-modal="false" title="编辑权限" :visible.sync="isEditDialogShow" width="35%" height="300px"
               @opened="dialogOpened" @open="dialogOpen" @close="dialogClose">
      <el-tree
        ref="treeView"
        :data="treeData"
        show-checkbox
        :default-checked-keys="defaultCheckedKeys"
        node-key="id"
        :props="treeDataProps">
      </el-tree>

      <div slot="footer" class="dialog-footer">
        <el-button @click="onCancelButtonPressed()">取消</el-button>
        <el-button type="primary" @click="onConfirmButtonPressed()">确定</el-button>
      </div>

    </el-dialog>

  </div>
</template>

<script>
  import { SYSMENU_LIST_URL, SYSROLE_EDIT_ALL_PERMISSIONS_URL, SYSROLE_ALL_PERMISSIONS_URL } from "@/config/host"

  const defaultDialogModal = {}

  export default {
    name: "PermissionEditDialog",
    props: {
      editDialogVisible: {
        type: Boolean,
        required: true
      },
      editDialogData: {// roleId
        type: Number
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
        treeData: [],
        treeDataProps: {
          children: "children",
          id: "id",
          label: function (data, node) {
            return data.name
          }
        },
        defaultCheckedKeys: []
      }
    },
    methods: {
      async onConfirmButtonPressed () {
        const loading = this.$loading({ target: ".permission-container .el-dialog" })
        const checkedPermission = {}
        this.$refs.treeView.getCheckedNodes(false, true).forEach((p) => {
          if (typeof p.id === "number") {
            checkedPermission[p.id.toString()] = {}
          } else if (typeof p.id === "string") {
            const ps = p.id.split("-")
            checkedPermission[ps[0].toString()][ps[1]] = true
          }
        })
        const result = await this.httpPostWithLoading(SYSROLE_EDIT_ALL_PERMISSIONS_URL, {
          "roleId": this.editDialogData,
          "data": checkedPermission
        })
        loading.close()
        if (result.success) {
          this.$message({
            message: "权限编辑成功",
            type: "success"
          })
          this.closeEditDialog()
        }
      },
      onCancelButtonPressed () {
        this.closeEditDialog()
      },
      dialogOpened () {

      },
      addMenuButton (treeData) {
        for (const td of treeData) {
          if (td.children && td.children.length > 0) {
            this.addMenuButton(td.children)
          } else {
            // if (td.component) {
            td.children.push({
              id: td.id + "-view",
              name: "查看"
            })
            td.children.push({
              id: td.id + "-add",
              name: "添加"
            })
            td.children.push({
              id: td.id + "-edit",
              name: "编辑"
            })
            td.children.push({
              id: td.id + "-delete",
              name: "删除"
            })
            // }
          }
        }
      },
      async dialogOpen () {
        this.defaultCheckedKeys = []
        const roleId = this.editDialogData
        const loading = this.$loading({ target: ".permission-container .el-dialog" })
        // 获取所有的菜单
        const allMenu = await this.httpPostWithLoading(SYSMENU_LIST_URL, null, ".el-dialog")
        const allPermission = await this.httpPostWithLoading(SYSROLE_ALL_PERMISSIONS_URL, { roleId: roleId }, ".el-dialog")
        if (allMenu.success && allPermission.success) {
          const treeData = allMenu.data
          // 先添加出来所有的菜单，不管有没有权限
          this.addMenuButton(treeData)
          this.treeData = treeData
          // 按照权限，把拥有的菜单打上勾
          allPermission.data.forEach((p) => {
            this.defaultCheckedKeys.push(p.menuId + "-view")
            if (p.addPermission) {
              this.defaultCheckedKeys.push(p.menuId + "-add")
            }
            if (p.editPermission) {
              this.defaultCheckedKeys.push(p.menuId + "-edit")
            }
            if (p.deletePermission) {
              this.defaultCheckedKeys.push(p.menuId + "-delete")
            }
          })
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
        // this.$emit("reload")
      }
    }
  }
</script>

<style scoped lang="css">
  .permission-container {

  }
</style>
