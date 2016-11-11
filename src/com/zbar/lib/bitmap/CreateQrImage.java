package com.zbar.lib.bitmap;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
/**
 * 二维码生成器
 * @author tanping
 * 2015-12-2
 */
public class CreateQrImage {
	public static  int QR_WIDTH = 400;
	public static  int QR_HEIGHT =400;

	// 要转换的地址或字符串,可以是中文
	public static Bitmap create(String context,int width,int height) {
		try {
			
			if (width <=0 || height <=0) {
				width = 400;
				height = 400;
			}
			QR_HEIGHT = width;
			QR_HEIGHT = height;
			// 判断URL合法性
			if (context == null || "".equals(context) || context.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(context,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			
			//去掉白边
			int[] rec = bitMatrix.getEnclosingRectangle();  
			int resWidth = rec[2] + 1;  
			int resHeight = rec[3] + 1;  
			BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);  
			resMatrix.clear();  
			for (int i = 0; i < resWidth; i++) {  
			    for (int j = 0; j < resHeight; j++) {  
			        if (bitMatrix.get(i + rec[0], j + rec[1])) { 
			             resMatrix.set(i, j); 
			        } 
			    }  
			}  
			QR_WIDTH = resMatrix.getWidth();
			QR_HEIGHT = resMatrix.getHeight();
		   
		   int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
		   bitMatrix.clear();
		   bitMatrix = resMatrix;

			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

}
