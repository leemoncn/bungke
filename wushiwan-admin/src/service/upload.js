/**
 * @Author: limeng
 * @Date: 2019-11-18 22:55
 * @Description: 七牛云上传
 */
import { httpPost } from "@/util/request"
import { QINIUKODO_UPLOAD_TOKEN_URL } from "@/config/host"

export async function uploadToQiniu (number, onFinish) {
  await httpPost(QINIUKODO_UPLOAD_TOKEN_URL, {
    value: number
  }).then(({ data, success }) => {
    if (success) {
      onFinish(data)
    }
  })
}
