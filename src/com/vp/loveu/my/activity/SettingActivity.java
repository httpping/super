package com.vp.loveu.my.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vp.loveu.MainActivity;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.index.myutils.CheckedAppUpdate;
import com.vp.loveu.index.myutils.CheckedAppUpdate.OnCheckedListener;
import com.vp.loveu.my.widget.SettingItemRelativeLayout;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月1日上午11:22:24
 * @功能 设置的界面
 * @作者 mi
 */

public class SettingActivity extends VpActivity implements OnCheckedListener {

	private LinearLayout mLyContainerOne;
	private LinearLayout mLyContainerTwo;
	private LinearLayout mLyContainerThree;
	private Button mBtOut;
	private boolean isFirst;
	private IOSActionSheetDialog addSheetItem;
	public static final String[] nameOne = new String[] { "账号绑定", "扫一扫", "清除缓存" };;
	public static final String[] nameTwo = new String[] { "意见反馈", "常见问题", "检查更新" };
	// public static final String[] nameThree = new String[] { "给我们评分", "关于爱U"};
	public static final String[] nameThree = new String[] { "关于爱U" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void initData() {
		SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(this);
		int type = sp.getIntegerValue("UserType", 1);

		for (int i = 0; i < nameOne.length; i++) {
			SettingItemRelativeLayout item = new SettingItemRelativeLayout(this);
			item.setTvName(nameOne[i]);
			if (i == nameOne.length - 1) {
				item.setIsShowLine(false);
				item.setIvMoreIsShow(false);
			}
			if (i == 1) {
				if ((type&16) >0) {
					item.setVisibility(View.VISIBLE);
				}else{
					item.setVisibility(View.GONE);
				}
			}
				
			mLyContainerOne.addView(item);
		}
		for (int i = 0; i < nameTwo.length; i++) {
			SettingItemRelativeLayout item = new SettingItemRelativeLayout(this);
			item.setTvName(nameTwo[i]);
			if (i == nameTwo.length - 1) {
				item.setIsShowLine(false);
				item.setIvMoreIsShow(false);
			}
			mLyContainerTwo.addView(item);
		}
		for (int i = 0; i < nameThree.length; i++) {
			SettingItemRelativeLayout item = new SettingItemRelativeLayout(this);
			item.setTvName(nameThree[i]);
			if (i == nameThree.length - 1) {
				item.setIsShowLine(false);
			}
			mLyContainerThree.addView(item);
		}
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("设置");
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mLyContainerOne = (LinearLayout) findViewById(R.id.setting_ly_container_one);
		mLyContainerTwo = (LinearLayout) findViewById(R.id.setting_ly_container_two);
		mLyContainerThree = (LinearLayout) findViewById(R.id.setting_ly_container_three);
		mBtOut = (Button) findViewById(R.id.setting_bt_out);
		mBtOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addSheetItem = new IOSActionSheetDialog(SettingActivity.this).builder().setTitle("退出登录").addSheetItem("确定",
						SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						Intent intent2 = new Intent(SettingActivity.this, MainActivity.class);
						LoginStatus.loginOut();
						intent2.putExtra("command", "logout");
						startActivity(intent2);
						// removeCacheFile(getCacheDir());
					}
				});
				addSheetItem.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == CheckedAppUpdate.REQUEST_CODE && arg1 == Activity.RESULT_OK) {
			// 说明安装成功
		} else {
			// 说明安装被取消
			if (isFirst) {
				new CheckedAppUpdate(this, true, this).upGradeApp();// 说明是强制升级
																	// --- 继续弹出
			} else {
				// 不是强制升级不用管
			}
		}
	}

	private boolean isFours;

	@Override
	public void onDownCancle(boolean isFours) {
		if (isFours) {
			new CheckedAppUpdate(this, true, this).upGradeApp();
		}
	}

	@Override
	public void onDownError(boolean isFours) {
	}

	@Override
	public void onDownFinish(boolean isFours) {
		this.isFours = isFours;
	}

	@Override
	public void onNotUpground() {
	}
}
