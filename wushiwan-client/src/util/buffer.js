/**
 * @Author: limeng
 * @Date: 2019-05-08 20:38
 * @Description:
 */

// 压缩图片的代码
// const buffer = Buffer.from(this.sampleImgArray[0].split(",")[1], "base64")
// const jimpImg = await Jimp.read(buffer)
// const compressedBuffer = await jimpImg.quality(50).getBufferAsync(Jimp.MIME_JPEG)
// const formData = new FormData()
// const arrayBuffer = bufferToArrayBuffer(compressedBuffer)
// formData.append("file", new Blob([arrayBuffer], { type: "image/jpeg" }), "abc.jpg")
// formData.append("form", new Blob([JSON.stringify({ id: 444 })], {
//   type: "application/json"
// }))

export function bufferToArrayBuffer (buffer) {
  let ab = new ArrayBuffer(buffer.length)
  let view = new Uint8Array(ab)
  for (let i = 0; i < buffer.length; ++i) {
    view[i] = buffer[i]
  }
  return ab
}

// From ArrayBuffer to Buffer:
export function arrayBufferToBuffer (ab) {
  let buffer = Buffer.alloc(ab.byteLength)
  let view = new Uint8Array(ab)
  for (let i = 0; i < buffer.length; ++i) {
    buffer[i] = view[i]
  }
  return buffer
}
