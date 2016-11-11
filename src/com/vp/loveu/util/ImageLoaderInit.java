package com.vp.loveu.util;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.vp.loveu.R;

/**
 * image loader图片工具类
 */
public class ImageLoaderInit extends Application {

	private static boolean initState = false;
	public static int DEGREE_90 = 90;

	/**
	 * 
	 * @Title: initImageLoader
	 * @Description: 初始化
	 * @param context
	 *            void
	 */
	public static void initImageLoader(Context context) {

		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"tianhongvpclub/image/");

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
		initState = true;

	}

	/**
	 * 
	 * @Title: getInitState
	 * @Description: 获取初始化状态
	 * @return boolean
	 */
	public static boolean getInitState() {
		return initState;
	}

	/**
	 * javascript:;
	 * 
	 * @Title: setOptions
	 * @Description: 设置图片加载选项
	 * @param degree
	 *            圆角角度
	 * @return DisplayImageOptions
	 */
	public static DisplayImageOptions setOptions(int degree) {
		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(false).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(degree))//
				.build();
		return options;
	}

	/**
	 * 
	 * @Title: setOptions
	 * @Description: 设置图片加载选项 ,用于活动详情
	 * @return DisplayImageOptions
	 */
	public static DisplayImageOptions setActivityOptions() {
		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.bg)
				.showImageForEmptyUri(R.drawable.active_item_logo)
				.showImageOnFail(R.drawable.active_item_logo)
				.cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	/**
	 * 
	 * @Title: setOptions
	 * @Description: 设置活动详情头像图片加载选项 ,用于活动详情
	 * @return DisplayImageOptions
	 */
	public static DisplayImageOptions setActivityHeadOptions() {
		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.color.bg)
				.showImageForEmptyUri(R.drawable.image_default)
				.showImageOnFail(R.drawable.image_default)
				.cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}
	
	public static DisplayImageOptions setActivityMainOptions() {
		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.image_login_bg_start)
        .showImageForEmptyUri(R.drawable.image_login_bg_start)
        .showImageOnFail(R.drawable.image_login_bg_start)
        .resetViewBeforeLoading(true)
        .cacheOnDisk(true).cacheInMemory(true)
        .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .considerExifParams(true)
        .displayer(new SimpleBitmapDisplayer()).build();
		return options;
	}

	/**
	 * 
	 * @Title: setOptions
	 * @Description: 设置图片加载选项 ,正常在用的
	 * @return DisplayImageOptions
	 */
	public static DisplayImageOptions setOptions() {
		// 显示图片的配置

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.bg)
				.showImageForEmptyUri(R.drawable.image_recfragment_hear)
				.showImageOnFail(R.drawable.image_recfragment_hear)
				.cacheInMemory(false).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;

	}

	/**
	 * 
	 * @Title: setOptions
	 * @Description: 可以任意填充想添加的图片
	 * @return DisplayImageOptions
	 */
	public static DisplayImageOptions customOptions(int onLoadingRes,
			int emptyRes, int failRes) {
		// 显示图片的配置

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(onLoadingRes)
				.showImageForEmptyUri(emptyRes).showImageOnFail(failRes)
				.cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;

	}

}
