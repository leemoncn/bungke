/**
 * @Author: leemon
 * @Date: 2019-01-03 09:15
 * @Description:
 */

export const OFFICE_TYPE_LIST = [{
  value: "1",
  label: "公司"
}, {
  value: "2",
  label: "部门"
}, {
  value: "3",
  label: "小组"
}, {
  value: "4",
  label: "其他"
}]

export function getOfficeTypeNameWithValue (value) {
  for (let office of OFFICE_TYPE_LIST) {
    if (office.value === value) {
      return office.label
    }
  }
  return "其他"
}

export const OFFICE_GRADE_LIST = [{
  value: "1",
  label: "一级"
}, {
  value: "2",
  label: "二级"
}, {
  value: "3",
  label: "三级"
}, {
  value: "4",
  label: "四级"
}]

export function getOfficeGradeNameWithValue (value) {
  for (let office of OFFICE_GRADE_LIST) {
    if (office.value === value) {
      return office.label
    }
  }
  return "未知"
}
