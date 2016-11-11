package com.vp.loveu.my.activity;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.my.bean.FollowBean.FollowData;
import com.vp.loveu.my.fragment.FansFragment;
import com.vp.loveu.my.fragment.FollowFragment;
import com.vp.loveu.my.listener.OnFollowUserListener;
import com.vp.loveu.widget.PagerSlidingTabStrip;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月30日上午11:48:22
 * @功能 关注和粉丝页面
 * @作者 mi
 */

public class FansActivity extends VpActivity implements OnFollowUserListener{

	private ViewPager mViewPager;
	private PagerSlidingTabStrip mIndicator;
	private ImageView mIvBack;
	private String[] titles = new String[]{"关注","粉丝"};
	private List<Fragment> mData = new ArrayList<Fragment>();
	private FollowFragment followFragment;
	private FansFragment fansFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans_activity);
		initView();
		initData();
	}

	private void initData() {
		followFragment = new FollowFragment(this);
		fansFragment = new FansFragment(this);
		mData.add(followFragment);
		mData.add(fansFragment);
		mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
		mIndicator.setViewPager(mViewPager);
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.fans_viewpager);
		mIndicator = (PagerSlidingTabStrip) findViewById(R.id.fans_indicator);
		mIvBack = (ImageView) findViewById(R.id.fans_iv_back);
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private class MyAdapter extends FragmentPagerAdapter{

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return mData.get(arg0);
		}

		@Override
		public int getCount() {
			return mData.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
	}

	@Override
	public void addFollow(FollowData bean) {
		fansFragment.addNotifyDataSetChanged(bean);
		followFragment.addNotifyDataSetChanged(bean);
	}

	@Override
	public void cancleFollow(FollowData bean) {
		fansFragment.cancleNotifyDatsetChanged(bean);
		followFragment.cancleNotifyDatsetChanged(bean);
	}
}
