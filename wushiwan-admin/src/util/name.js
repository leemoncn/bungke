/**
 * @Author: leemon
 * @Date: 2019-01-10 10:31
 * @Description:
 */

// 下划线转换驼峰
export function toCamel (str) {
  return str.replace(/([^_])(?:_+([^_]))/g, function ($0, $1, $2) {
    return $1 + $2.toUpperCase()
  })
}

// 驼峰转换下划线
export function toLowerLine (str) {
  let temp = str.replace(/[A-Z]/g, function (match) {
    return "_" + match.toLowerCase()
  })
  if (temp.slice(0, 1) === "_") { // 如果首字母是大写，执行replace时会多一个_，这里需要去掉
    temp = temp.slice(1)
  }
  return temp
}
