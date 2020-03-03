/**
 * @Author: leemon
 * @Date: 2019-10-23 13:43
 * @Description:
 */
import { toastMsg } from "@/service/function"

export function alipayForBrowser (formData) {
  let form = formData
  const div = document.createElement("div")
  div.innerHTML = form
  document.body.appendChild(div)
  document.forms[0].submit()
}

export function alipayForApp (orderData, finishCallback) {
  plus.payment.getChannels(function (s) {
    for (const pay of s) {
      if (pay.id === "alipay") {
        plus.payment.request(pay, orderData, function () {
          finishCallback()
        }, function (e) {
          toastMsg("支付失败")
        })
        break
      }
    }
  }, function (e) {
    toastMsg("获取支付渠道失败，请联系客服")
  })
}
