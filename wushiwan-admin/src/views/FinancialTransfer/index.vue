/**
* @Author: leemon
* @Date: 2019-10-22 21:16:38
* @Description: 支付宝转账记录
*/
<template>
  <el-container style="height: 100%" class="app-container">
    <el-main>
      <div class="filter-container">
        <el-form :inline="true" :model="searchForm" ref="searchForm">
            <el-form-item label="申请提现用户的id" prop="userId">
              <el-input v-model="searchForm.userId" autocomplete="new-password"/>
            </el-form-item>
            <el-form-item label="FinancialWithdrawal提现申请表的id" prop="withdrawalId">
              <el-input v-model="searchForm.withdrawalId" autocomplete="new-password"/>
            </el-form-item>
            <el-form-item label="第三方支付的转账单号" prop="thirdTransferNo">
              <el-input v-model="searchForm.thirdTransferNo" autocomplete="new-password"/>
            </el-form-item>
            <el-form-item label="系统生成的转账单号" prop="transferNo">
              <el-input v-model="searchForm.transferNo" autocomplete="new-password"/>
            </el-form-item>
          <el-button type="primary" @click="onSearchButtonPressed">查询</el-button>
          <el-button type="info" @click="onResetSearchButtonPressed">重置</el-button>
        </el-form>
      </div>
      <div class="filter-container">
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-plus" v-permission="'financial_transfer_add'"
                   @click="onAddButtonPressed">添加
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-edit" v-permission="'financial_transfer_edit'"
                   @click="onEditButtonPressed">修改
        </el-button>
        <!--<el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-delete" v-permission="'financial_transfer_delete'"-->
                   <!--@click="onDeleteButtonPressed">删除-->
        <!--</el-button>-->
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-refresh"
                   @click="onRefreshPressed">刷新
        </el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" tooltip-effect="dark" border fit @sort-change="onTableSortChange"
                @selection-change="onTableSelectionChange">
        <el-table-column type="selection" width="40" fixed="left" align="center"/>
        <el-table-column type="index" width="40" fixed="left"/>

      <el-table-column prop="id" label="id" sortable="custom" align="center"/>
      <el-table-column prop="userId" label="申请提现用户的id" sortable="custom" align="center"/>
      <el-table-column prop="withdrawalId" label="FinancialWithdrawal提现申请表的id" sortable="custom" align="center"/>
      <el-table-column prop="thirdTransferNo" label="第三方支付的转账单号" sortable="custom" align="center"/>
      <el-table-column prop="transferNo" label="系统生成的转账单号" sortable="custom" align="center"/>

        <el-table-column prop="createTime" label="创建时间" width="155" sortable="custom"/>
        <el-table-column label="操作" fixed="right" width="210">
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
              v-permission="'financial_transfer_edit'">编辑
            </el-button>
            <!--<el-button-->
              <!--size="mini"-->
              <!--type="danger"-->
              <!--@click.stop="onItemDeleteButtonPressed(scope.row)"-->
              <!--v-permission="'financial_transfer_delete'">删除-->
            <!--</el-button>-->
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

    </el-main>
  </el-container>
</template>

<script>
  import { FINANCIALTRANSFER_DELETE_URL, FINANCIALTRANSFER_LIST_URL } from "@/config/host"
  import EditDialog from "./EditDialog"
  import { TABLE_DEFAULT_PAGE_SIZE } from "@/config/define"

  const defaultSearchForm = {
      userId: null,
      withdrawalId: null,
      thirdTransferNo: null,
      transferNo: null
  }

  export default {
    name: "FinancialTransferManagement",
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
        previousListRequestIsSearch: false
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
        const page = { pageNo: this.currentPage, pageSize: this.pageSize, order: this.orderBy, column: this.orderColumn }
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
        this.httpPostWithLoading(FINANCIALTRANSFER_LIST_URL, this.createListRequestData(isSearch ? this.searchForm : null)).then(({ data, success }) => {
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
      onItemDeleteButtonPressed: function (row) {
        this.$confirm(`删除节点<${row.id}>, 是否继续?`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.httpPostWithLoading(FINANCIALTRANSFER_DELETE_URL, { id: row.id }).then(({ success }) => {
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
      }
    }
  }
</script>

<style scoped lang="less">

</style>
