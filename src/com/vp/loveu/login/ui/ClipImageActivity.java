package com.vp.loveu.login.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.vp.loveu.R;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.widget.ClipImageLayout;

/**
 * @author：pzj
 * @date: 2015-10-23 下午3:57:40
 * @Description:裁剪图片的Activity
 */
public class ClipImageActivity extends  Activity implements OnClickListener {
	public static final String RESULT_PATH = "crop_image";
	private static final String KEY = "path";
	private ClipImageLayout mClipImageLayout = null;
	/** 裁剪图片地址 */
	public String filename = Environment.getExternalStorageDirectory()
			.getPath() + "/clip.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_image_layout);

		mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);
		String path = getIntent().getStringExtra(KEY);

		// 有的系统返回的图片是旋转了，有的没有旋转，所以处理
		int degreee = readBitmapDegree(path);
		Bitmap bitmap = createBitmap(path);
		bitmap=compressionBigBitmap(bitmap);
		if (bitmap != null) {
			if (degreee == 0) {
				mClipImageLayout.setImageBitmap(bitmap);
			} else {
				mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
			}
		} else {
			finish();
		}
		findViewById(R.id.okBtn).setOnClickListener(this);
		findViewById(R.id.cancleBtn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.okBtn) {
			Bitmap bitmap = mClipImageLayout.clip();
			saveBitmap(bitmap, filename);
			Intent intent = new Intent();
			intent.putExtra("path", filename);
			setResult(Activity.RESULT_OK, intent);
		}
		finish();
	}

	private void saveBitmap(Bitmap bitmap, String path) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}

		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fOut != null)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建图片
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap createBitmap(String path) {
		if (path == null) {
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		FileInputStream is = null;
		Bitmap bitmap = null;
		try {
			is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	// 读取图像的旋转度
	private int readBitmapDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// 旋转图片
	private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return resizedBitmap;
	}

	private Bitmap compressionBigBitmap(Bitmap bitmat2) {
		Bitmap destBitmap = null;
		float scaleValue;
		if (bitmat2.getWidth() > 1500||bitmat2.getHeight()>2000) {
			if(bitmat2.getWidth()>bitmat2.getHeight()){
			scaleValue = (float) (3000f / bitmat2.getWidth());}
			else{
				scaleValue = (float) (3000f / bitmat2.getHeight());
			}
			Matrix matrix = new Matrix();
			matrix.postScale(scaleValue, scaleValue);
			destBitmap = Bitmap.createBitmap(bitmat2, 0, 0, bitmat2.getWidth(),
					bitmat2.getHeight(), matrix, true);
		} else {
			return bitmat2;
		}
		return destBitmap;
	}
	
}
