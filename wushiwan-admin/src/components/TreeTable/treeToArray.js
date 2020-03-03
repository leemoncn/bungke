/**
 * @Author: leemon
 * @Date:   2017-10-12 12:06:49
 */
import Vue from "vue"

export default function treeToArray (data, expandAll, parent = null, level = null) {
  let tmp = []
  Array.from(data).forEach(function (record) {
    if (record._expanded === undefined) {
      Vue.set(record, "_expanded", expandAll)
    }
    let _level = 1
    if (level !== undefined && level !== null) {
      _level = level + 1
    }
    Vue.set(record, "_level", _level)
    // 如果有父元素
    if (parent) {
      const newParent = _.cloneDeep(parent)
      delete newParent.children
      Vue.set(record, "parent", newParent)
    }
    tmp.push(record)
    if (record.children && record.children.length > 0) {
      const children = treeToArray(record.children, expandAll, record, _level)
      tmp = tmp.concat(children)
    }
  })
  return tmp
}
