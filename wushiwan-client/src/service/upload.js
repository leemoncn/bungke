/**
 * @Author: limeng
 * @Date: 2019-11-18 22:55
 * @Description: 七牛云上传
 */
import { httpPost } from "@/util/request"
import { QINIUKODO_UPLOAD_TOKEN_URL } from "@/config/host"
import { isApp } from "@/service/plus"
import * as qiniu from "qiniu-js"
import { imgFullPath } from "@/util/img"

export async function uploadToQiniu (imgFileList, onFinish, onError) {
  if (!imgFileList || imgFileList.length === 0) {
    onFinish()
    return
  }
  let imgCount = imgFileList.length
  const result = await httpPost(QINIUKODO_UPLOAD_TOKEN_URL, {
    value: imgCount
  })
  if (result.success) {
    // 执行成功的次数
    let uploadSuccessCount = 0
    // 执行的次数
    let uploadProceedCount = 0
    const data = result.data
    const list = data.list
    if (isApp()) { // app版本使用plus.uploader上传
      for (let i = 0; i < list.length; i++) {
        const item = list[i]
        let uploader = plus.uploader.createUpload("http://upload.qiniup.com/", {}, function (up, state) {
          if (state === 200) {
            uploadProceedCount++
            uploadSuccessCount++
            if (uploadProceedCount === imgCount && uploadProceedCount === uploadSuccessCount) {
              if (onFinish) {
                onFinish(data.key, data.list)
              }
            }
          } else {
            uploadProceedCount++
            if (uploadProceedCount === imgCount && uploadProceedCount !== uploadSuccessCount) {
              if (onError) {
                onError()
              }
            }
          }
        })
        const file = imgFileList[i].file
        let fileName = file.name
        uploader.addData("key", item.fileName)
        uploader.addData("token", item.token)
        uploader.addData("fileName", fileName)
        uploader.addData("x:redisKey", data.key)
        uploader.addFile(file.fullPath, { "key": "file" })
        uploader.start()
      }
    } else { // web版本使用qiniu的js sdk上传
      for (let i = 0; i < list.length; i++) {
        const item = list[i]
        const file = imgFileList[i].file
        let observable = qiniu.upload(file, item.fileName, item.token, {
          fname: file.name,
          params: {
            "x:redisKey": data.key
          }
        }, {
          region: qiniu.region.z0
        })
        observable.subscribe(() => {}, () => {
          uploadProceedCount++
          if (uploadProceedCount === imgCount && uploadProceedCount !== uploadSuccessCount) {
            if (onError) {
              onError()
            }
          }
        }, () => {
          uploadProceedCount++
          uploadSuccessCount++
          if (uploadProceedCount === imgCount && uploadProceedCount === uploadSuccessCount) {
            if (onFinish) {
              onFinish(data.key, data.list)
            }
          }
        })
      }
    }
  } else { // 后台获取token失败
    if (onError) {
      onError()
    }
  }
}

export function createFileContentArrayFromFileArray (fileArray) {
  let arr = []
  for (const file of fileArray) {
    if (file.content.startsWith("data:image")) {
      arr.push(file.content)
    } else {
      arr.push(imgFullPath(file.content))
    }
  }
  return arr
}
