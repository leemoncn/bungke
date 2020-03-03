<template>
  <el-table :data="formatData" :row-style="showRow" @row-click="rowClick" @selection-change="selectionChange"
            v-bind="$attrs" @sort-change="sortChange">
    <el-table-column type="selection" width="35" v-if="showSelection" fixed="left"/>
    <el-table-column v-if="columns.length===0" width="150">
      <template slot-scope="scope">
        <span v-for="space in scope.row._level" :key="space" class="ms-tree-space"/>
        <span v-if="iconShow(0,scope.row)" class="tree-ctrl" @click.stop="toggleExpanded(scope.$index)">
          <i v-if="!scope.row._expanded" class="el-icon-plus"/>
          <i v-else class="el-icon-minus"/>
        </span>
        {{ scope.$index }}
      </template>
    </el-table-column>
    <el-table-column v-for="(column, index) in columns" v-else :key="column.value" :label="column.text"
                     :width="column.width" :sortable="sortable(column.sortable)" :prop="column.prop">
      <template slot-scope="scope">
        <!-- Todo -->
        <!-- eslint-disable-next-line vue/no-confusing-v-for-v-if -->
        <span v-for="space in scope.row._level" v-if="index === 0" :key="space" class="ms-tree-space"/>
        <span v-if="iconShow(index,scope.row)" class="tree-ctrl" @click.stop="toggleExpanded(scope.$index)">
          <i v-if="!scope.row._expanded" class="el-icon-plus"/>
          <i v-else class="el-icon-minus"/>
        </span>
        {{ scope.row[column.value] }}
      </template>
    </el-table-column>
    <slot/>
  </el-table>
</template>

<script>
  import treeToArray from "./treeToArray"

  export default {
    name: "TreeTable",
    props: {
      /* eslint-disable */
      data: {
        type: [Array, Object],
        required: true
      },
      columns: {
        type: Array,
        default: () => []
      },
      evalFunc: Function,
      evalArgs: Array,
      expandAll: {
        type: Boolean,
        default: false
      },
      rowClick: {
        type: Function,
        default: function () {}
      },
      selectionChange: {
        type: Function,
        default: function () {}
      },
      showSelection: {
        type: Boolean,
        default: false
      },
      sortChange: {
        type: Function,
        default: () => {}
      }
    },
    computed: {
      // 格式化数据源
      formatData: function () {
        let tmp
        if (!Array.isArray(this.data)) {
          tmp = [this.data]
        } else {
          tmp = this.data
        }
        const func = this.evalFunc || treeToArray
        const args = this.evalArgs ? Array.concat([tmp, this.expandAll], this.evalArgs) : [tmp, this.expandAll]
        return func.apply(null, args)
      }
    },
    methods: {
      sortable: function (sort) {
        if (!sort) {
          return false
        }
        return sort
      },
      showRow: function (row) {
        const show = (row.row.parent ? (row.row.parent._expanded && row.row.parent._show) : true)
        row.row._show = show
        return show ? "animation:treeTableShow 0.5s;-webkit-animation:treeTableShow 0.5s;" : "display:none;"
      },
      changeAllChildrenNotExpanded: function (list) {
        for (let record of list) {
          record._expanded = false
          const children = record.children
          if (children instanceof Array && children.length > 0) {
            this.changeAllChildrenNotExpanded(children)
          }
        }
      },
      // 切换下级是否展开
      toggleExpanded: function (trIndex) {
        const record = this.formatData[trIndex]
        record._expanded = !record._expanded
        if (!record._expanded) {
          const children = record.children
          if (children instanceof Array) {
            this.changeAllChildrenNotExpanded(children)
          }
        }
      },
      // 图标显示
      iconShow(index, record) {
        return (index === 0 && record.children && record.children.length > 0)
      }
    }
  }
</script>
<style>
  @keyframes treeTableShow {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @-webkit-keyframes treeTableShow {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
</style>

<style lang="scss" scoped>
  $color-blue: #2196F3;
  $space-width: 14px;
  .ms-tree-space {
    position: relative;
    top: 1px;
    display: inline-block;
    font-style: normal;
    font-weight: 400;
    line-height: 1;
    width: $space-width;
    height: 14px;

    &::before {
      content: ""
    }
  }

  .processContainer {
    width: 100%;
    height: 100%;
  }

  table td {
    line-height: 26px;
  }

  .tree-ctrl {
    position: relative;
    cursor: pointer;
    color: $color-blue;
    margin-left: -$space-width;
  }
</style>
