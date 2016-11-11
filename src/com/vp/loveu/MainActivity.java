package com.vp.loveu;

import java.util.List;
import java.util.Map;

import com.amap.api.maps2d.model.Text;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.bean.AppconfigBean;
import com.vp.loveu.channel.utils.MusicUtils;
import com.vp.loveu.coupon.ui.MyCouponActivity;
import com.vp.loveu.index.myutils.CheckedAppUpdate;
import com.vp.loveu.index.myutils.CheckedAppUpdate.OnCheckedListener;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.ui.LoginActivity;
import com.vp.loveu.message.bean.ChatEmoji;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.message.utils.FaceConversionUtil;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.service.XmppService;
import com.vp.loveu.service.XmppService.XmppServiceBinder;
import com.vp.loveu.util.FragmentFactory;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.ToastUtil;
import com.vp.loveu.util.VPLog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * @项目名称name loveu1.0
 * 
 * @时间2015年11月12日上午11:00:26
 * @功能 home页面
 * @作者mi
 */

public class MainActivity extends VpActivity implements OnCheckedListener {

	private ViewPager mViewPager;
	private RadioGroup mRadioGroup;
	private long mCurrentTime = 0;
	private long temptime;
	private ServiceConnection mServiceConnection;
	private XmppServiceBinder mBinder;
	private FragmentFactory fragmentFactory = new FragmentFactory();
	private SharedPreferencesHelper sp;

	public ImageView mHeadImageView;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Intent intent = new Intent(this, XmppService.class);
		// startService(intent);
		
		ScreenUtils.initScreen(this);
		String command = getIntent().getStringExtra("command");
		if (!TextUtils.isEmpty(command)) {// 发送了命令
			if (command.equals("logout")) {// 注销
				Intent intent3 = new Intent(this, LoginActivity.class);
				startActivity(intent3);
				finish();
				return;
			}
			if (command.endsWith("quit")) {
				finish();// 退出
				return;
			}
		}
		sp = SharedPreferencesHelper.getInstance(this);
		initPublicTitle();
		if (!TextUtils.isEmpty(sp.getStringValue("app_top_color"))) {
			try {
				String stringValue = sp.getStringValue("app_top_color");
				mPubTitleView.setBgColor(stringValue);
				//有时候颜色值可能不正确  所以try下 防止程序崩溃
			} catch (Exception e) {
			}
		}
		
		
		
		initView();
		// mPubTitleView.bgView.setAlpha(1f);
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.default_portrait);
		mPubTitleView.mBtnLeft.setVisibility(View.GONE);
		mPubTitleView.mBtnLeft.getLayoutParams().height = DensityUtil.dip2px(this, 36);
		mPubTitleView.mBtnLeft.getLayoutParams().width = DensityUtil.dip2px(this, 36);
		mHeadImageView = new ImageView(this);
		mHeadImageView.setScaleType(ScaleType.CENTER_CROP);
		int padding = 1;// DensityUtil.dip2px(this, 2);
		mPubTitleView.mBtnLeft.setPadding(padding, padding, padding, padding);
		mHeadImageView.setLayoutParams(mPubTitleView.mBtnLeft.getLayoutParams());
		mHeadImageView.setBackgroundResource(R.drawable.index_circle_text_style);
		mHeadImageView.setPadding(padding, padding, padding, padding);

		mPubTitleView.mLeftLayout.addView(mHeadImageView);
		mPubTitleView.mLeftLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, MyCenterActivity.class));
			}
		});
		// mPubTitleView.mBtnRight.setBackgroundResource(R.drawable.icon_scan);
		mPubTitleView.mTvTitle.setText("爱U");
		
		/*mPubTitleView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,MyCouponActivity.class);
				startActivity(intent);
			}
		});*/
		mPubTitleView.mTvTitle.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					long currentTimeMillis = System.currentTimeMillis();
					if ((currentTimeMillis - mCurrentTime) < 800) {
						FragmentStatePagerAdapter adapter = (FragmentStatePagerAdapter) mViewPager.getAdapter();
						View view = adapter.getItem(mViewPager.getCurrentItem()).getView();
						if (view instanceof ViewGroup) {
							ViewGroup group = (ViewGroup) view;
							for (int i = 0; i < group.getChildCount(); i++) {
								View childAt = group.getChildAt(i);
								if (childAt instanceof ScrollView) {
									ScrollView scroll = (ScrollView) childAt;
									if (scroll.getScrollY() != 0) {
										scroll.scrollTo(0, 0);
									}
								}
							}
						}
					}
					mCurrentTime = currentTimeMillis;
					break;
				default:
					break;
				}
				return false;
			}
		});

		// 友盟
		MobclickAgent.updateOnlineConfig(this);
		onlineRemind();
		/*
		 * Intent intent = new Intent(this,ConfictLoginActivity.class);
		 * startActivity(intent);
		 */

		checkUpdate(0);

		
	}
	
	private void checkUpdate(long delayedTime) {
		// 检查更新
		new CheckedAppUpdate(this, false, this).upGradeApp();// 检测升级
	}

	public void onlineRemind() {

		Intent intent = new Intent(MainActivity.this, XmppService.class);
		startService(intent);

		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				VPLog.d("main", "onServiceConnected");
				mBinder = (XmppServiceBinder) service;
				Runnable serv = new Runnable() {
					public void run() {
						VPLog.d("main", System.currentTimeMillis() + "");
						mBinder.getService().mhHandler.sendEmptyMessageDelayed(XmppService.GET_ONLINE_REMIND, 15000);// 发送好友提醒
						/**/
						VPLog.d("main", System.currentTimeMillis() + "");
					}
				};
				new Thread(serv).start();
			}
		};
		bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(tag, "onNewIntent");
		setIntent(intent);
		String command = intent.getStringExtra("command");
		if (!TextUtils.isEmpty(command)) {
			if (command.equals("logout")) {// 注销
				Intent intent3 = new Intent(this, LoginActivity.class);
				startActivity(intent3);
				finish();
			}
			if (command.endsWith("quit")) {
				finish();// 退出
			}
		}
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.home_viewpager);
		mRadioGroup = (RadioGroup) findViewById(R.id.home_radiogroup);
		HomePagerAdapter mAdapter = new HomePagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(4);
		RadioButton button = (RadioButton) mRadioGroup.getChildAt(0);
		button.setChecked(true);// 默认选中第一个
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
					RadioButton button = (RadioButton) mRadioGroup.getChildAt(i);
					button.setChecked(false);
					if (position == i) {
						button.setChecked(true);
					}
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.home_rb_one:
					mViewPager.setCurrentItem(0);
					break;
				case R.id.home_rb_two:
					mViewPager.setCurrentItem(1);
					break;
				case R.id.home_rb_three:
					mViewPager.setCurrentItem(2);
					break;
				case R.id.home_rb_fours:
					mViewPager.setCurrentItem(3);
					break;

				default:
					break;
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		DisplayImageOptions option = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_portrait) // resource
																														// or
				.showImageForEmptyUri(R.drawable.default_portrait) // resource
																	// or
				.showImageOnFail(R.drawable.default_portrait) // resource or
				.resetViewBeforeLoading(false) // default
				.delayBeforeLoading(50).cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
				.displayer(new RoundedBitmapDisplayer(DensityUtil.dip2px(this, 40))).build();
		
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		
		Map<String, AppconfigBean> infos = VpApplication.getInstance().getAppInfoBean();
		VPLog.d(tag, "infos :" + infos);
		PushNoticeBean.sendUpdateBroadcast(this);// 通知更新view
		
		
		if (loginInfo == null) {
			return;
		}
		String head = loginInfo.getPortrait();
		ImageLoader.getInstance().displayImage(head, mHeadImageView, option);


		/*
		 * String spec ="loveu://activity/zhe?a=abc&b=bcd#1"; //spec
		 * ="qijian://test.uri.activity?action#1"; URI uri = URI.create(spec);
		 * Uri uri2 = Uri.parse(spec); String fragment = uri2.getFragment();
		 * VPLog.d(tag, "f:"+fragment +" q:" + uri2.getQuery() +" ah:" +
		 * uri2.getAuthority() +" lf:" + uri2.getLastPathSegment() +" go:"
		 * +uri2.getQueryParameter("go") ); VPLog.d(tag, "f:"+fragment +" q:" +
		 * uri.getQuery() +" ah:" + uri.getAuthority() ); VPLog.d(tag,
		 * "d:"+uri.getQuery() +" aa:"+uri2.getQueryParameter("a")+" b:"+
		 * uri2.getQueryParameter("b"));
		 * 
		 * InwardAction action = InwardAction.parseAction(spec); VPLog.d(tag,
		 * "action:"+action + " " + action.getValueForKey("a") );
		 */

		/*
		 * List<String> arrList = new LinkedList<>();
		 * arrList.add("http://baidu.com"); arrList.add("file://adfd.txt");
		 * mClient = new VpHttpClient(this); UpLoadUtil upLoadUtil = new
		 * UpLoadUtil(mClient, arrList, "", ""); boolean b =upLoadUtil.start(new
		 * OnUpLoadResult() {
		 * 
		 * @Override public void finish(UpLoadUtil data) { VPLog.d(tag,
		 * "result:"+ data); } }); VPLog.d(tag, "bb:" +b);
		 */

		// 初始化emoji表情
		new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... params) {
				try {
					List<List<ChatEmoji>> emojis = FaceConversionUtil.getInstace().emojiLists;
					if (emojis == null || emojis.size() == 0) {
						FaceConversionUtil.getInstace().getFileText(MainActivity.this);
						emojis = FaceConversionUtil.getInstace().emojiLists;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();

	}


	private class HomePagerAdapter extends FragmentStatePagerAdapter {

		public HomePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragmentFactory.getFragment(arg0);
		}

		@Override
		public int getCount() {
			return 4;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*
		 * Intent intent4 = new Intent(this, XmppService.class);
		 * stopService(intent4);
		 */
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
			mServiceConnection = null;
		}
		// 注销
		String command = getIntent().getStringExtra("command");
		VPLog.d(tag, "command:" + command);
		// ToastUtil.showToast(this, command+"", 1);
		if (!TextUtils.isEmpty(command)) {
			if (command.equals("logout") || command.endsWith("quit")) {// 注销
				Intent intent2 = new Intent(this, XmppService.class);
				stopService(intent2);
			}

		}
		try {
			new MusicUtils(this).stopMusicServices(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		/*
		 * Intent intent = new Intent(this,MusicService.class);
		 * stopService(intent);
		 */

		/*
		 * Uri uri = Uri.parse("loveu://launch"); Intent intent2 = new
		 * Intent(Intent.ACTION_VIEW,uri); startActivity(intent2);
		 */
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
			if (System.currentTimeMillis() - temptime > 2000) // 2s内再次选择back键有效
			{
				System.out.println(Toast.LENGTH_LONG);
				ToastUtil.showToast(this, "请在按一次返回退出", Toast.LENGTH_LONG);
				temptime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};

	@Override
	public void onDownCancle(boolean isFours) {
		if (isFours) {
			// 强制升级
			finish();
		} else {
		}
	}

	@Override
	public void onDownError(boolean isFours) {
		if (isFours) {
			// 强制升级
			finish();
		} else {
		}
	}

	private boolean mIsFours;

	@Override
	public void onDownFinish(boolean isFours) {
		// 去安装
		mIsFours = isFours;
	}

	@Override
	public void onNotUpground() {
		// 不需要升级
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == Activity.RESULT_OK && arg0 == CheckedAppUpdate.REQUEST_CODE) {
			android.os.Process.killProcess(android.os.Process.myPid());// 杀死自己
		} else {
		}
	}
}
