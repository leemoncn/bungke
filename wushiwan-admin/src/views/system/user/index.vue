/**
* @Author: leemon
* @Date: 2019-5-6 8:59:18
* @Description: 用户管理
*/
<template>
  <el-container style="height: 100%" class="app-container">
    <el-main>
      <div class="filter-container">
        <el-form :inline="true" :model="searchForm" ref="searchForm">
          <el-form-item label="用户id" prop="id">
            <el-input v-model="searchForm.id" autocomplete="new-password"/>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="searchForm.phone" autocomplete="new-password"/>
          </el-form-item>
          <el-form-item label="用户上级id" prop="superiorId">
            <el-input v-model="searchForm.superiorId" autocomplete="new-password"/>
          </el-form-item>
          <el-form-item label="代理商id" prop="partnerId">
            <el-input v-model="searchForm.partnerId" autocomplete="new-password"/>
          </el-form-item>
          <el-button type="primary" @click="onSearchButtonPressed">查询</el-button>
          <el-button type="info" @click="onResetSearchButtonPressed">重置</el-button>
        </el-form>
      </div>
      <div class="filter-container">
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-plus" v-permission="'sys_user_add'"
                   @click="onAddButtonPressed">添加
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-edit" v-permission="'sys_user_edit'"
                   @click="onEditButtonPressed">修改
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-delete" v-permission="'sys_user_delete'"
                   @click="onDeleteButtonPressed">删除
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-refresh"
                   @click="onRefreshPressed">刷新
        </el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" tooltip-effect="dark" border fit @sort-change="onTableSortChange"
                @selection-change="onTableSelectionChange">
        <el-table-column type="selection" width="40" fixed="left" align="center"/>
        <el-table-column type="index" width="40" fixed="left"/>
        <el-table-column prop="id" label="id" sortable="custom"/>
        <el-table-column prop="userType" label="用户类型" :formatter="userTypeFormatter"/>
        <el-table-column prop="deposit" label="保证金" sortable="custom"/>
        <el-table-column prop="missionCoin" label="任务币" sortable="custom"/>
        <el-table-column prop="earning" label="收入" sortable="custom"/>
        <el-table-column prop="alipay" label="支付宝"/>
        <el-table-column prop="realName" label="真实姓名"/>
        <el-table-column prop="nickname" label="昵称"/>
        <el-table-column prop="phone" label="手机号"/>
        <el-table-column prop="email" label="邮箱"/>
        <el-table-column prop="superiorId" label="上级id"/>
        <el-table-column prop="agencyId" label="分成id(代理商)"/>
        <el-table-column prop="partnerId" label="代理商id"/>
        <el-table-column prop="loginStatusPropertyId" label="登陆状态" :formatter="loginStatusPropertyIdFormatter"/>
        <el-table-column prop="loginIp" label="最后登陆IP"/>
        <el-table-column prop="loginTime" label="最后登陆日期"/>

        <el-table-column prop="createTime" label="创建时间" width="155" sortable="custom"/>
        <el-table-column label="操作" fixed="right" width="280">
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
              v-permission="'sys_user_edit'">编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click.stop="onItemDeleteButtonPressed(scope.row)"
              v-permission="'sys_user_delete'">删除
            </el-button>
            <el-button
              v-show="scope.row.pushId"
              size="mini"
              type="primary"
              @click.stop="onItemPushButtonPressed(scope.row.id)"
              v-permission="'sys_user_delete'">推送
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
                   @reload="loadTableData"
                   :edit-dialog-editable="editDialogEditable"/>

      <push-dialog :edit-dialog-visible.sync="pushDialogVisible" :user-id="userId"/>

    </el-main>
  </el-container>
</template>

<script>
  import { SYSUSER_DELETE_URL, SYSUSER_LIST_URL } from "@/config/host"
  import EditDialog from "./EditDialog"
  import { TABLE_DEFAULT_PAGE_SIZE } from "@/config/define"
  import PushDialog from "@/views/Push/PushDialog"

  const defaultSearchForm = {
    id: null,
    phone: null,
    superiorId: null,
    partnerId: null
  }

  export default {
    name: "SysUserManagement",
    components: { PushDialog, EditDialog },
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
        pushDialogVisible: false,
        userId: null
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
        if (isSearch) {
          if (!this.searchForm.phone) {
            this.searchForm.phone = null
          }
        }
        this.httpPostWithLoading(SYSUSER_LIST_URL, this.createListRequestData(isSearch ? this.searchForm : null)).then(({ data, success }) => {
          if (success) {
            this.tableData = data.records || []
            this.totalCount = data.total
            this.previousListRequestIsSearch = isSearch
            this.currentPage = data.current
            this.pageSize = data.size
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
        this.openEditDialog(row, true)
      },
      onItemPushButtonPressed (userId) {
        this.userId = userId
        this.pushDialogVisible = true
      },
      onItemDeleteButtonPressed: function (row) {
        this.$confirm(`删除节点<${row.id}>, 是否继续?`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.httpPostWithLoading(SYSUSER_DELETE_URL, { id: row.id }).then(({ success }) => {
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
        this.openEditDialog(row, false)
      },
      onTableSelectionChange: function (selection) {
        this.tableSelectionData = selection
      },
      openEditDialog: function (row, isEditable) {
        this.editDialogEditable = isEditable
        this.editDialogData = row
        this.editDialogVisible = true
      },
      userTypeFormatter: function (row, column) {
        if (row.userType === 1) {
          return "前台用户"
        }
        if (row.userType === 2) {
          return "后台用户"
        }
        return "未知"
      },
      loginStatusPropertyIdFormatter: function (row) {
        const propertyList = this.$store.getters.propertyList
        if (propertyList.length === 0) {
          return "未知"
        }
        const property = propertyList.find((value, index, arr) => {
          return value.id === row.loginStatusPropertyId
        })
        if (property) {
          return property.name
        }
        return "未知"
      }
    }
  }
</script>

<style scoped lang="less">

</style>
