package com.vp.loveu.index.myutils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.vp.loveu.R;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月30日上午9:32:48
 * @功能 获取displayoptionconfig配置
 * @作者 mi
 */

public class DisplayOptionsUtils {

	public static DisplayImageOptions getOptionsConfig() {
		return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image_loading) // resource
																							// or
				.showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
				.showImageOnFail(R.drawable.default_image_loading_fail) // resource or
				.resetViewBeforeLoading(false) // default_image_loading_fail
				.delayBeforeLoading(50).cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false)// default
				.build();
	}
}
