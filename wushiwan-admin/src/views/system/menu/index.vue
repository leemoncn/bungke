/**
* @Author: leemon
* @Date: 2019-4-27 11:29:34
* @Description: 菜单管理
*/
<template>
  <el-container style="height: 100%" class="app-container">
    <el-main>
      <div class="filter-container">
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-plus" v-permission="'sys_menu_add'"
                   @click="onAddButtonPressed">添加
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-edit" v-permission="'sys_menu_edit'"
                   @click="onEditButtonPressed">修改
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-delete" v-permission="'sys_menu_delete'"
                   @click="onDeleteButtonPressed">删除
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-refresh"
                   @click="onRefreshPressed">刷新
        </el-button>
      </div>

      <el-table :data="tableData" row-key="id" style="width: 100%" tooltip-effect="dark" border fit
                @sort-change="onTableSortChange"
                @selection-change="onTableSelectionChange">
        <el-table-column type="selection" width="40" fixed="left" align="center"/>
        <el-table-column type="index" width="40" fixed="left"/>
        <el-table-column prop="name" label="菜单标题"/>
        <el-table-column prop="href" label="链接"/>
        <el-table-column prop="icon" label="图标" align="center">
          <template slot-scope="scope">
            <svg-icon :icon-class="scope.row.icon" v-if="scope.row.icon"/>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column prop="isShow" label="是否显示" sortable="custom"
                         :formatter="(row, column, cellValue, index)=>cellValue?'是':'否'"/>
        <el-table-column prop="component" label="组件路径"/>
        <el-table-column prop="componentName" label="组件名字" sortable="custom"/>
        <el-table-column prop="permission" label="权限标识" sortable="custom"/>
        <el-table-column prop="sort" label="排序" sortable="custom"/>

        <el-table-column label="操作" fixed="right" width="324">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="info"
              @click.stop="onItemViewButtonPressed(scope.$index, scope.row)">查看
            </el-button>
            <el-button
              size="mini"
              type="primary"
              @click.stop="onItemEditButtonPressed(scope.row)"
              v-permission="'sys_menu_edit'">编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click.stop="onItemDeleteButtonPressed(scope.row)"
              v-permission="'sys_menu_delete'">删除
            </el-button>
            <el-button
              size="mini"
              @click.stop="onAddChildButtonPressed(scope.row)"
              v-permission="'sys_menu_add'">添加下级菜单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="loadTableData"
        @current-change="loadTableData"
        style="margin-top: 20px"
        background
        :current-page.sync="currentPage"
        :page-sizes="getPageSizes()"
        :page-size.sync="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount"/>

      <edit-dialog :edit-dialog-data="editDialogData" :edit-dialog-visible.sync="editDialogVisible"
                   @reload="loadTableData" :is-add-child="isAddChild"
                   :edit-dialog-editable="editDialogEditable"/>

    </el-main>
  </el-container>
</template>

<script>
  import { SYSMENU_DELETE_URL, SYSMENU_LIST_URL } from "@/config/host"
  import EditDialog from "./EditDialog"
  import { TABLE_DEFAULT_PAGE_SIZE } from "@/config/define"

  const defaultSearchForm = {}

  export default {
    name: "SysMenuManagement",
    components: { EditDialog },
    data () {
      return {
        tableData: [],
        editDialogData: null,
        editDialogVisible: false,
        editDialogEditable: false,
        tableSelectionData: [],
        searchForm: _.cloneDeep(defaultSearchForm),
        currentPage: 1,
        pageSize: TABLE_DEFAULT_PAGE_SIZE,
        totalCount: 0,
        orderBy: "",
        orderColumn: "",
        previousListRequestIsSearch: false,
        isAddChild: false
      }
    },
    mounted: function () {
      this.loadTableData()
    },
    methods: {
      getPageSizes: function () {
        return [TABLE_DEFAULT_PAGE_SIZE, 20, 50, 100]
      },
      onTableSortChange: function ({ column, prop, order }) {
        if (!prop) {
          this.orderBy = ""
          this.orderColumn = ""
        } else {
          this.orderColumn = prop
          if (order === "descending") {
            this.orderBy = "DESC"
          } else {
            this.orderBy = "ASC"
          }
        }
        this.proceedListRequest(this.previousListRequestIsSearch)
      },
      createListRequestData: function (data) {
        const page = {
          pageNo: this.currentPage,
          pageSize: this.pageSize,
          order: this.orderBy,
          column: this.orderColumn
        }
        if (data) {
          return Object.assign(data, page)
        }
        return page
      },
      onSearchButtonPressed: function () {
        this.proceedListRequest(true)
      },
      onResetSearchButtonPressed: function () {
        this.searchForm = _.cloneDeep(defaultSearchForm)
        this.previousListRequestIsSearch = false
        this.currentPage = 1
        this.loadTableData()
      },
      loadTableData: function () {
        this.proceedListRequest(this.previousListRequestIsSearch)
      },
      proceedListRequest: function (isSearch) {
        this.httpPostWithLoading(SYSMENU_LIST_URL, this.createListRequestData(isSearch ? this.searchForm : null)).then(({ data, success }) => {
          if (success) {
            this.tableData = data || []
            this.totalCount = data.total
            this.previousListRequestIsSearch = isSearch
          }
        })
      },
      getOneTableSelectionData () {
        if (this.tableSelectionData.length === 0) {
          this.$message.error("请先选择一条数据")
          return null
        }
        if (this.tableSelectionData.length > 1) {
          this.$message.error("只能选择一条数据")
          return null
        }
        return this.tableSelectionData[0]
      },
      onAddButtonPressed: function () {
        this.isAddChild = false
        this.openEditDialog(null, true)
      },
      onDeleteButtonPressed: function () {
        const row = this.getOneTableSelectionData()
        if (row) {
          this.onItemDeleteButtonPressed(row)
        }
      },
      onEditButtonPressed: function () {
        const row = this.getOneTableSelectionData()
        if (row) {
          this.onItemEditButtonPressed(row)
        }
      },
      onRefreshPressed: function () {
        this.onResetSearchButtonPressed()
      },
      onItemEditButtonPressed: function (row) {
        this.isAddChild = false
        this.openEditDialog(row, true)
      },
      onItemDeleteButtonPressed: function (row) {
        this.$confirm(`删除节点<${row.name}>, 是否继续?`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.httpPostWithLoading(SYSMENU_DELETE_URL, { id: row.id }).then(({ success }) => {
            if (success) {
              this.$message({
                message: "菜单删除成功",
                type: "success"
              })
              this.loadTableData()
            }
          })
        }).catch(() => { // 点击取消
        })
      },
      onItemViewButtonPressed: function (index, row) {
        this.isAddChild = false
        this.openEditDialog(row, false)
      },
      onAddChildButtonPressed: function (row) {
        this.isAddChild = true
        this.openEditDialog(row, true)
      },
      onTableSelectionChange: function (selection) {
        this.tableSelectionData = selection
      },
      openEditDialog: function (row, isEditable) {
        this.editDialogEditable = isEditable
        this.editDialogData = row
        this.editDialogVisible = true
      }
    }
  }
</script>

<style scoped lang="less">

</style>
