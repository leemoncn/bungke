/**
 * @Author: limeng
 * @Date: 2019-05-11 16:26
 * @Description:
 */

import { IMG_HOST } from "@/config/host"

export function imgFullPath (path) {
  if (!path) {
    return ""
  }
  return IMG_HOST + "/" + path + "?imageslim"
}
