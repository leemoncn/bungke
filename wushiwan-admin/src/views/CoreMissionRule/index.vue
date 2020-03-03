/**
* @Author: leemon
* @Date: 2019-5-9 20:37:25
* @Description: 任务规则管理
*/
<template>
  <el-container style="height: 100%" class="app-container">
    <el-main>
      <div class="filter-container">
        <el-form :inline="true" :model="searchForm" ref="searchForm">
          <el-form-item label="任务类型" prop="typePropertyId">
            <el-select v-model="searchForm.typePropertyId" placeholder="请选择">
              <el-option
                v-for="(item,index) in missionTypePropertyList"
                :key="index"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="onSearchButtonPressed">查询</el-button>
          <el-button type="info" @click="onResetSearchButtonPressed">重置</el-button>
        </el-form>
      </div>
      <div class="filter-container">
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-plus"
                   v-permission="'core_mission_rule_add'"
                   @click="onAddButtonPressed">添加
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-edit"
                   v-permission="'core_mission_rule_edit'"
                   @click="onEditButtonPressed">修改
        </el-button>
        <el-button class="filter-item" style="margin-left: 10px;" icon="el-icon-delete"
                   v-permission="'core_mission_rule_delete'"
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
        <el-table-column prop="typePropertyId" label="任务类型" :formatter="missionTypeFormatter"/>
        <el-table-column prop="minPrice" label="最低出价(元)" sortable="custom"
                         :formatter="(row)=>{return row.minPrice / 100.0}"/>
        <el-table-column prop="minCount" label="最低任务数量" sortable="custom"/>
        <el-table-column prop="verifyImgCount" label="审核验证图最多上传数量" sortable="custom"/>
        <el-table-column prop="usable" label="状态"
                         :formatter="(row)=>{return row.usable?'开启':'关闭'}"/>

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
              v-permission="'core_mission_rule_edit'">编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click.stop="onItemDeleteButtonPressed(scope.row)"
              v-permission="'core_mission_rule_delete'">删除
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

    </el-main>
  </el-container>
</template>

<script>
  import { COREMISSIONRULE_DELETE_URL, COREMISSIONRULE_LIST_URL } from "@/config/host"
  import EditDialog from "./EditDialog"
  import { TABLE_DEFAULT_PAGE_SIZE } from "@/config/define"
  import { getPropertyListByType, PropertyType, getPropertyById } from "@/service/property"

  const defaultSearchForm = {
    typePropertyId: null
  }

  export default {
    name: "CoreMissionRuleManagement",
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
    computed: {
      missionTypePropertyList: function () {
        return getPropertyListByType(PropertyType.MISSION_TYPE)
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
        this.httpPostWithLoading(COREMISSIONRULE_LIST_URL, this.createListRequestData(isSearch ? this.searchForm : null)).then(({ data, success }) => {
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
          this.httpPostWithLoading(COREMISSIONRULE_DELETE_URL, { id: row.id }).then(({ success }) => {
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
      missionTypeFormatter: function (item) {
        const property = getPropertyById(item.typePropertyId)
        return property.name
      }
    }
  }
</script>

<style scoped lang="less">

</style>
