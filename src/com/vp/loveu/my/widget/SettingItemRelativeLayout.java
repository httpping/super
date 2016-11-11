package com.vp.loveu.my.widget;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vp.loveu.R;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.CheckedAppUpdate;
import com.vp.loveu.my.activity.AccountBindActivity;
import com.vp.loveu.my.activity.HeartShowActivity;
import com.vp.loveu.my.activity.SettingActivity;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;
import com.zbar.lib.CaptureActivity;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月1日上午11:43:11
 * @功能 设置页面的item
 * @作者 mi
 */

public class SettingItemRelativeLayout extends RelativeLayout implements OnClickListener {

	protected String tag = "SettingItemRelativeLayout";
	private TextView mTvName;
	private View mLine;
	private ImageView mIvMore;

	public SettingItemRelativeLayout(Context context) {
		this(context, null);
	}

	public SettingItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.setting_item, this);
		initView();
	}

	private void initView() {
		mTvName = (TextView) findViewById(R.id.setting_item_tv_name);
		mLine = (View) findViewById(R.id.setting_item_line);
		mIvMore = (ImageView) findViewById(R.id.setting_item_iv_more);
		setOnClickListener(this);
		String state = Environment.getExternalStorageState();
	}

	public void setTvName(String name) {
		mTvName.setText(name);
	}

	public void setIsShowLine(boolean flag) {
		mLine.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
	}

	public void setIvMoreIsShow(boolean isshow) {
		mIvMore.setVisibility(isshow ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onClick(View v) {
		String name = mTvName.getText().toString().trim();

		if (name.equals(SettingActivity.nameOne[0])) {
			// 账号绑定
			getContext().startActivity(new Intent(getContext(), AccountBindActivity.class));
		} else if (name.equals(SettingActivity.nameOne[1])) {
			// 推送设置 --- 改为了扫一扫
			Intent intent = new Intent(getContext(), CaptureActivity.class);
			getContext().startActivity(intent);
		} else if (name.equals(SettingActivity.nameOne[2])) {
			// 清除缓存
			clearCache();
		} else if (name.equals(SettingActivity.nameTwo[0])) {
			// 意见反馈
			Intent intent = new Intent(getContext(), HeartShowActivity.class);
			intent.putExtra(HeartShowActivity.KEY, true);
			intent.putExtra(HeartShowActivity.TYPE, 1);
			getContext().startActivity(intent);
		} else if (name.equals(SettingActivity.nameTwo[1])) {
			// 常见问题
			InwardAction.parseAction(VpConstants.FAQ).toStartActivity(getContext());
		} else if (name.equals(SettingActivity.nameTwo[2])) {
			// 检查更新
			CheckedAppUpdate checkedAppUpdate = new CheckedAppUpdate((SettingActivity) getContext(), true, (SettingActivity) getContext());
			checkedAppUpdate.upGradeApp();
		}
		/*
		 * else if (name.equals(SettingActivity.nameThree[0])) { // 给我们评分 }
		 */
		else if (name.equals(SettingActivity.nameThree[0])) {
			// 关于爱U
			InwardAction.parseAction(VpConstants.ABOUT_US + "&vn=" + getPackageVersion()).toStartActivity(getContext());
		}
	}

	public String getPackageVersion() {
		PackageManager pm = getContext().getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getContext().getPackageName(), PackageManager.GET_CONFIGURATIONS);
			int versionCode = info.versionCode;
			String versionName = info.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 清除缓存 void TODO
	 */
	private void clearCache() {
		IOSActionSheetDialog addSheetItem = new IOSActionSheetDialog(getContext()).builder().setTitle("清除缓存").addSheetItem("确定",
				SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						new Thread() {
							public void run() {
								removeCacheFile(getContext().getCacheDir());
								File file = new File(getContext().getCacheDir().getParentFile().getPath() + "/app_webview");
								removeCacheFile(file);
								((SettingActivity) getContext()).runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(getContext(), "缓存清除成功", Toast.LENGTH_SHORT).show();
									}
								});
							};
						}.start();
					}
				});
		addSheetItem.show();
	}

	public static void removeCacheFile(File file) {
		if (file != null && file.isDirectory()) {
			File[] list = file.listFiles();
			for (int i = 0; i < list.length; i++) {
				File f = list[i];
				if (f.isDirectory()) {
					removeCacheFile(f);
				} else {
					f.delete();
				}
			}
		}
	}
}
