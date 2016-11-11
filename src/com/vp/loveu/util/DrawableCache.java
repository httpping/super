package com.vp.loveu.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 防止溢出
 * 
 */
public class DrawableCache {
	static private DrawableCache cache;
	/** 用于Chche内容的存储 */
	private Hashtable<String, DrawableRef> drawableRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Drawable> q;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class DrawableRef extends SoftReference<Drawable> {
		private String _key = "";

		public DrawableRef(Drawable bmp, ReferenceQueue<Drawable> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}
	
	private DrawableCache() {
		drawableRefs = new Hashtable<String, DrawableRef>();
		q = new ReferenceQueue<Drawable>();

	}

	/**
	 * 取得缓存器实例
	 */
	public static DrawableCache getInstance() {
		if (cache == null) {
			cache = new DrawableCache();
		}
		return cache;

	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	private void addCacheDrawable(Drawable bmp, String key) {
		cleanCache() ;
		DrawableRef ref = new DrawableRef(bmp, q, key);
		drawableRefs.put(key, ref);
	}

	/**
	 * 依据所指定的文件名获取图片
	 */
	public Drawable getDrawable(String loadFile) {
		Drawable drawableImage = null;
		if (drawableRefs.containsKey(loadFile)) {
			DrawableRef ref = (DrawableRef) drawableRefs.get(loadFile);
			drawableImage = (Drawable) ref.get();
		}
		if (drawableImage == null) {
			try {
				drawableImage = Drawable.createFromPath(loadFile);
				if(null!=drawableImage){
					this.addCacheDrawable(drawableImage, loadFile);
				}
			} catch (Exception e) {
				drawableImage=null;
			}
		}
		return drawableImage;
	}

	/**
	 * 依据所指定的文件名获取圆形图片
	 */
	public Drawable getDrawableRo(String loadFile) {
		Drawable drawableImage = null;
		if (drawableRefs.containsKey(loadFile+"ro")) {
			DrawableRef ref = (DrawableRef) drawableRefs.get(loadFile+"ro");
			drawableImage = (Drawable) ref.get();
		}
		if (drawableImage == null) {
			try {
				Bitmap	bitmapImage = BitmapFactory.decodeFile(loadFile);
				if(bitmapImage!=null){
					drawableImage= new BitmapDrawable(toRoundBitmap(bitmapImage));   
				}
				if(null!=drawableImage){
					this.addCacheDrawable(drawableImage, loadFile+"ro");
				}
			} catch (Exception e) {
				drawableImage=null;
			}
		}
		return drawableImage;
	}
	/**
	 * 依据所指定的文件名获取压缩
	 */
	public Drawable getDrawableSh(String loadFile,int w) {
		Drawable drawableImage = null;
		if (drawableRefs.containsKey(loadFile+"sh")) {
			DrawableRef ref = (DrawableRef) drawableRefs.get(loadFile+"sh");
			drawableImage = (Drawable) ref.get();
		}
		if (drawableImage == null) {
			try {
//				Bitmap	bitmapImage = BitmapFactory.decodeFile(loadFile);
//				if(bitmapImage!=null){
					drawableImage= new BitmapDrawable(getimage(loadFile,w));   
//				}
				if(null!=drawableImage){
					this.addCacheDrawable(drawableImage, loadFile+"sh");
				}
			} catch (Exception e) {
				drawableImage=null;
			}
		}
		return drawableImage;
	}
	 
	
	
	/**
	 * 
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * 
	 * @return
	 */

	public Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width,
		height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;

	}
	
	
	/**
	 * 图片压缩方法实现
	 * 
	 * @param srcPath
	 * @return
	 */
	private Bitmap getimage(String filename, int itemWidth) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filename, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int ww = itemWidth;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可	
		int be = 1;// be=1表示不缩放
		if (w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(filename, newOpts);
		// System.out.println(srcPath);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 质量压缩
	 * 
	 * @param image
	 * @return
	 */
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap;
		bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}
	
	
	
	private void cleanCache() {
		DrawableRef ref = null;
		while ((ref = (DrawableRef) q.poll()) != null) {
			drawableRefs.remove(ref._key);
		}
	}
	// 清除Cache内的全部内容
	public void clearCache() {
		cleanCache();
		drawableRefs.clear();
		System.gc();
		System.runFinalization();
	}

	

}
