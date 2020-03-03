/**
 * @Author: leemon
 * @Date: 2019-01-02 10:32
 * @Description:
 */

export function trimEndPathSep (path) {
  if (path && path.length > 2) {
    return _.trimEnd(path, "/")
  }
  return path
}
