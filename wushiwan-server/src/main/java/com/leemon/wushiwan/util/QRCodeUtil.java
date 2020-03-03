package com.leemon.wushiwan.util;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.leemon.wushiwan.exception.LogicException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 二维码工具类
 * @author: limeng
 * @create: 2018-09-07 14:46
 **/
public class QRCodeUtil {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	/**
	 * 将字符串转换成base64 image字符串
	 *
	 * @return
	 */
	public static String strToQRCodeBase64Str(String content, int width, int height) {
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		Map<EncodeHintType, String> hints = new HashMap<>(2);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); //设置字符集编码类型
		BitMatrix bitMatrix;
		String base64Img;
		try {
			bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			BufferedImage image = toBufferedImage(bitMatrix);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			//输出二维码图片流
			ImageIO.write(image, "png", outputStream);
			base64Img = Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (Exception e1) {
			throw new LogicException("生成二维码base64字符串错误，content = " + content + ",err = " + e1);
		}
		return base64Img;
	}

	public static String convertToHtmlImgFormat(String base64) {
		return "data:image/png;base64," + base64;
	}

	private static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}
}
